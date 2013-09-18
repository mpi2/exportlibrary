/**
 * Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
 *
 * MEDICAL RESEARCH COUNCIL UK MRC
 *
 * Harwell Mammalian Genetics Unit
 *
 * http://www.har.mrc.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.dcc.exportlibrary.traverser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ResourceVersion;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.io.console.Argument;
import org.mousephenotype.dcc.utils.io.console.ConsoleReader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class Traverser extends ConsoleReader {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Traverser.class);
    private static String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private Reader reader;
    private HibernateManager hibernateManager;
    private Orchestrator orchestrator;
    private SpecimenOrchestrator specimenOrchestrator;
    private SubmissionSetLoader submissionSetLoader;

    public Traverser(List<Argument<?>> arguments) {
        super(arguments);
    }

    private void setup(String persistencePropertiesFilename) {
        logger.info("setting up database connection");
        try {

            reader = new Reader(persistencePropertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
        }

        try {
            this.hibernateManager = new HibernateManager(reader.getProperties(), PERSISTENCE_UNITNAME);
        } catch (Exception ex) {
            logger.error("error trying to connect to the database", ex);
        }
        this.submissionSetLoader = new SubmissionSetLoader(hibernateManager);
        this.orchestrator = new Orchestrator(hibernateManager, submissionSetLoader);
        this.specimenOrchestrator = new SpecimenOrchestrator(hibernateManager, submissionSetLoader);

    }

    private ResourceVersion getResourceVersion() {
        ResourceVersion resourceVersion = new ResourceVersion();
        return resourceVersion;
    }

    private void tearDown() {
        logger.info("trying to close database connections");
        try {
            if (this.hibernateManager != null) {
                this.hibernateManager.close();
            }
        } catch (Exception ex) {
            logger.error("error closing database connection", ex);
        }
    }

    public static void main(String[] args) {
        Argument<String> centreIlarCodeArgument = new Argument<>(String.class, "c", "centre", "centre", true, "centreIlarCode of [Bcm,Gmc,H,Ics,J,Krb,Ning,Rbrc,Tcp,Ucd,Wtsi]");
        Argument<Calendar> submissionDateArgument = new Argument<>(Calendar.class, "d", "date", "date", true, "for everysubmission on that date yyyy-mm-dd onwards");
        Argument<Long> trackingIDArgument = new Argument<>(Long.class, "t", "trackingID", "trackingID", true, "submission trackerID");
        Argument<String> persistencePropertiesFileNameArgument = new Argument<>(String.class, "p", "databaseProp", "databaseProp", false, "database properties filename");
        Argument<String> documentTypeArgument = new Argument<>(String.class, "f", "file", "file", true, "file type can be specimen or procedure");
        //
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(centreIlarCodeArgument);
        arguments.add(submissionDateArgument);
        arguments.add(trackingIDArgument);
        arguments.add(persistencePropertiesFileNameArgument);
        arguments.add(documentTypeArgument);

        //
        Traverser traverser = new Traverser(arguments);
        traverser.attachShutDownHook();
        traverser.setValues(args);
        //

        if (traverser.exceptionThrown) {
            System.exit(-1);
        }
        if (persistencePropertiesFileNameArgument.getValue() == null || persistencePropertiesFileNameArgument.getValue().isEmpty()) {
            logger.error("persistence properties filename required to connect to the database {}", traverser.usage());
            System.exit(0);
        }

        traverser.setup(persistencePropertiesFileNameArgument.getValue());


        if (trackingIDArgument.getRawValue() != null) {
            logger.info("running for trackingID {}", trackingIDArgument.getRawValue());
            boolean isSpecimen = false;
            try {
                isSpecimen = traverser.specimenOrchestrator.run(trackingIDArgument.getValue(), traverser.getResourceVersion());
            } catch (HibernateException | IOException ex) {
                logger.error("", ex);
                traverser.tearDown();
                System.exit(-1);
            }
            if (!isSpecimen) {
                try {
                    traverser.orchestrator.run(trackingIDArgument.getValue(), traverser.getResourceVersion());
                } catch (HibernateException | IOException ex) {
                    logger.error("", ex);
                    traverser.tearDown();
                    System.exit(-1);
                }

            }
            traverser.tearDown();
            System.exit(0);
        }



        if (centreIlarCodeArgument.getRawValue() != null && !centreIlarCodeArgument.getRawValue().isEmpty()
                && (submissionDateArgument.getRawValue() == null || (submissionDateArgument.getRawValue() != null
                && submissionDateArgument.getRawValue().isEmpty()))) {
            CentreILARcode centreILARcode = null;
            try {
                centreILARcode = CentreILARcode.fromValue(centreIlarCodeArgument.getRawValue());
            } catch (IllegalArgumentException ex) {
                logger.error("[{}] is not a valid ilarcode. Now closing. {}", centreIlarCodeArgument.getRawValue(), traverser.usage());
                traverser.tearDown();
                System.exit(-1);
            }
            if (documentTypeArgument.getRawValue() != null) {
                if (documentTypeArgument.getRawValue().equals("procedure")) {
                    try {
                        logger.info("running for {}", centreILARcode.toString());
                        traverser.orchestrator.run(centreILARcode, null, null, traverser.getResourceVersion(), false);
                    } catch (HibernateException | IOException ex) {
                        logger.error("", ex);
                        traverser.tearDown();
                        System.exit(-1);
                    } finally {
                        traverser.tearDown();
                    }

                }
                if (documentTypeArgument.getRawValue().equals("specimen")) {
                    try {
                        logger.info("running for {}", centreILARcode.toString());
                        traverser.specimenOrchestrator.run(centreILARcode, traverser.getResourceVersion());
                    } catch (HibernateException | IOException ex) {
                        logger.error("", ex);
                        traverser.tearDown();
                        System.exit(-1);
                    } finally {
                        traverser.tearDown();
                    }
                } else {
                    logger.error("Please specify one of the two valid values -f specimen or -f procedure");
                    traverser.tearDown();
                    System.exit(-1);
                }
            } else {
                logger.error("Please specify one of the two valid values -f specimen or -f procedure");
                traverser.tearDown();
                System.exit(-1);
            }
            System.exit(0);
        }

        if (centreIlarCodeArgument.getRawValue() != null && !centreIlarCodeArgument.getRawValue().isEmpty()
                && submissionDateArgument.getRawValue() != null && !submissionDateArgument.getRawValue().isEmpty()) {
            CentreILARcode centreILARcode = null;
            try {
                centreILARcode = CentreILARcode.fromValue(centreIlarCodeArgument.getRawValue());
            } catch (IllegalArgumentException ex) {
                logger.error("[{}] is not a valid ilarcode. Now closing. {}", centreIlarCodeArgument.getRawValue(), traverser.usage());
                traverser.tearDown();
                System.exit(-1);
            }
            if (documentTypeArgument.getRawValue() != null) {
                if (documentTypeArgument.getRawValue().equals("procedure")) {
                    try {
                        logger.info("running for centre {} on dates equal or after {}", centreILARcode.toString(), submissionDateArgument.getRawValue());
                        traverser.orchestrator.run(centreILARcode, traverser.getResourceVersion(), submissionDateArgument.getValue(), false);
                    } catch (HibernateException | IOException ex) {
                        logger.error("", ex);
                        traverser.tearDown();
                        System.exit(-1);
                    } finally {
                        traverser.tearDown();
                    }
                }
                if (documentTypeArgument.getRawValue().equals("specimen")) {
                    try {
                        logger.info("running for {}", centreILARcode.toString());
                        traverser.specimenOrchestrator.run(centreILARcode, traverser.getResourceVersion(), submissionDateArgument.getValue());
                    } catch (HibernateException | IOException ex) {
                        logger.error("", ex);
                        traverser.tearDown();
                        System.exit(-1);
                    } finally {
                        traverser.tearDown();
                    }
                } else {
                    logger.error("Please specify one of the two valid values -f specimen or -f procedure");
                    traverser.tearDown();
                    System.exit(-1);
                }
            } else {
                logger.error("Please specify one of the two valid values -f specimen or -f procedure");
                traverser.tearDown();
                System.exit(-1);
            }
            System.exit(0);
        }


        if (centreIlarCodeArgument.getRawValue() == null && submissionDateArgument.getRawValue() == null) {
            if (documentTypeArgument.getRawValue() != null) {
                if (documentTypeArgument.getRawValue().equals("procedure")) {
                    try {
                        logger.info("running on each submission ever on each centre");
                        Orchestrator.run(traverser.hibernateManager, traverser.submissionSetLoader);
                    } catch (HibernateException | IOException ex) {
                        logger.error("", ex);
                        traverser.tearDown();
                        System.exit(-1);
                    } finally {
                        traverser.tearDown();
                    }
                }
                if (documentTypeArgument.getRawValue().equals("specimen")) {
                    try {
                        logger.info("running on each submission ever on each centre");
                        SpecimenOrchestrator.run(traverser.hibernateManager, traverser.submissionSetLoader);
                    } catch (HibernateException | IOException ex) {
                        logger.error("", ex);
                        traverser.tearDown();
                        System.exit(-1);
                    } finally {
                        traverser.tearDown();
                    }
                } else {
                    logger.error("Please specify one of the two valid values -f specimen or -f procedure");
                    traverser.tearDown();
                    System.exit(-1);
                }
            }
            System.exit(0);
        }

        if (centreIlarCodeArgument.getRawValue() == null && submissionDateArgument.getValue() != null) {
            if (documentTypeArgument.getRawValue() != null) {
                if (documentTypeArgument.getRawValue().equals("procedure")) {
                    try {
                        logger.info("running on each centre on dates equal or after {}", submissionDateArgument.getRawValue());
                        Orchestrator.run(traverser.hibernateManager, submissionDateArgument.getValue(), traverser.submissionSetLoader);
                    } catch (HibernateException | IOException ex) {
                        logger.error("", ex);
                        traverser.tearDown();
                        System.exit(-1);
                    } finally {
                        traverser.tearDown();
                    }
                }
                if (documentTypeArgument.getRawValue().equals("specimen")) {
                    try {
                        logger.info("running on each centre on dates equal or after {}", submissionDateArgument.getRawValue());
                        SpecimenOrchestrator.run(traverser.hibernateManager, traverser.submissionSetLoader, submissionDateArgument.getValue());
                    } catch (HibernateException | IOException ex) {
                        logger.error("", ex);
                        traverser.tearDown();
                        System.exit(-1);
                    } finally {
                        traverser.tearDown();
                    }
                } else {
                    logger.error("Please specify one of the two valid values -f specimen or -f procedure");
                    traverser.tearDown();
                    System.exit(-1);
                }
            }
            System.exit(0);
        } else {
            traverser.tearDown();
        }



    }

    @Override
    public String example() {
        StringBuilder sb = new StringBuilder("example \n java -jar ");
        String jarFilename = "jarfile.jar";
        try {
            jarFilename = Traverser.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            jarFilename = jarFilename.substring(jarFilename.lastIndexOf("/") == -1 ? 0 : jarFilename.lastIndexOf("/") + 1, jarFilename.length());
        } catch (Exception e) {
        }

        sb.append(jarFilename);

        return sb.toString();
    }

    @Override
    public int getMinimumOptionalParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Inside Add Shutdown Hook");
                tearDown();

            }
        });
        System.out.println("Shut Down Hook Attached.");
    }
}
