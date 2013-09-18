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
package org.mousephenotype.dcc.exportlibrary.xmlserialization.consoleapps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;

import org.mousephenotype.dcc.exportlibrary.xmlserialization.controls.CoreSerializer;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.controls.TrackerSerializer;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.io.console.Argument;
import org.mousephenotype.dcc.utils.io.console.ConsoleReader;
import org.mousephenotype.dcc.utils.io.jar.JARUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class XMLSerializer extends ConsoleReader {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLSerializer.class);
    private static final String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private static final int MIN_ARGS = 2;
    private static final int MAX_ARGS = 6;
    //
    private static final String DCC_MASTER_DB_CONNECTION_PROPERTIES_FILENAME = "mysql.properties";
    private static final String DCC_DERBY_DB_CONNECTION_PROPERTIES_FILENAME = "derby.properties";
    private static final String DCC_MASTER_KEYWORD = "dcc";
    private static final String DCC_DERBY_KEYWORD = "local";
    //
    
    //
    private TrackerSerializer trackerSerializer;
    //
    private CoreSerializer coreSerializer;
    //
    private static final int PARSE_ARGS_FAIL = 100;
    private static final int DB_PROPERTIES_FILE_NOT_FOUND = 101;
    private static final int MISSING_XML_FILE_PATH = 102;
    private static final int DB_ERROR_CONNECTION = 103;
    private static final int DB_ERROR_SERIALIZING = 104;
    /*
     * private static final int private static final int private static final
     * int
     */

    private void closeTrackerQuietly() {
        try {
            this.trackerSerializer.fullClose();
        } catch (IllegalStateException ex) {
            logger.error("error closing trackerSerializer", ex);
        }
    }

    private void closeCoreQuietly() {
        try {
            this.coreSerializer.fullClose();
        } catch (IllegalStateException ex) {
            logger.error("error closing coreSerializer", ex);
        }
    }

    private boolean trakerRun4procedures(String procedureResultsFilename, Long trackingID, Calendar submissionDate, Boolean closeTracker) {
        boolean success = true;
        try {
            this.trackerSerializer.serializeProcedureSubmissionSet(trackingID, submissionDate, procedureResultsFilename);
        } catch (JAXBException ex) {
            logger.error("error processing the xml file ", procedureResultsFilename, ex);
            success = false;
        } catch (FileNotFoundException ex) {
            logger.error("{} not found ", procedureResultsFilename, ex);
            success = false;
        } catch (IllegalStateException ex) {
            logger.error("transaction is active", ex);
            success = false;
        } catch (EntityExistsException ex) {
            logger.error("specimen set already exists in the db", ex);
            success = false;
        } catch (IllegalArgumentException ex) {
            logger.error("illegal state persisting specimens", ex);
            success = false;
        } catch (TransactionRequiredException ex) {
            logger.error("no transaction available", ex);
            success = false;
        } catch (XMLloadingException ex) {
            logger.error("", ex);
            success = false;
        } catch (IOException ex) {
            logger.error("", ex);
            success = false;
        } finally {
            if (closeTracker) {
                this.closeTrackerQuietly();
            }
        }
        return success;
    }

    private boolean trakerRun4specimens(String specimenResultsFilename, Long trackingID, Calendar submissionDate, Boolean closeTracker) {
        boolean success = true;
        try {
            this.trackerSerializer.serializeSpecimenSubmissionSet(trackingID, submissionDate, specimenResultsFilename);
        } catch (JAXBException ex) {
            logger.error("error processing the xml file ", specimenResultsFilename, ex);
            success = false;
        } catch (FileNotFoundException ex) {
            logger.error("{} not found ", specimenResultsFilename, ex);
            success = false;
        } catch (IllegalStateException ex) {
            logger.error("transaction is active", ex);
            success = false;
        } catch (EntityExistsException ex) {
            logger.error("specimen set already exists in the db", ex);
            success = false;
        } catch (IllegalArgumentException ex) {
            logger.error("illegal state persisting specimens", ex);
            success = false;
        } catch (TransactionRequiredException ex) {
            logger.error("no transaction available", ex);
            success = false;
        } catch (XMLloadingException ex) {
            logger.error("", ex);
            success = false;
        } catch (IOException ex) {
            logger.error("", ex);
            success = false;
        } finally {
            if (closeTracker) {
                this.closeTrackerQuietly();
            }
        }
        return success;
    }

    private void coreRun4procedures(String procedureResultsFilename, Boolean closeCore) {
        try {
            this.coreSerializer.serializeProceduresOnly(procedureResultsFilename);
        } catch (JAXBException ex) {
            logger.error("error processing the xml file {}", procedureResultsFilename, ex);
        } catch (FileNotFoundException ex) {
            logger.error("{} not found ", procedureResultsFilename, ex);
        } catch (IllegalStateException ex) {
            logger.error("transaction is active", ex);
        } catch (EntityExistsException ex) {
            logger.error("specimen set already exists in the db", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("illegal state persisting specimens", ex);
        } catch (TransactionRequiredException ex) {
            logger.error("no transaction available", ex);
        } catch (IOException ex) {
            logger.error("", ex);
        } finally {
            if (closeCore) {
                this.closeCoreQuietly();
            }
        }
    }

    private void coreRun4specimens(String specimenResultsFilename, Boolean closeCore) {
        try {
            this.coreSerializer.serializeSpecimensOnly(specimenResultsFilename);
        } catch (JAXBException ex) {
            logger.error("error processing the xml file ", specimenResultsFilename, ex);
        } catch (FileNotFoundException ex) {
            logger.error("{} not found ", specimenResultsFilename, ex);
        } catch (IllegalStateException ex) {
            logger.error("transaction is active", ex);
        } catch (EntityExistsException ex) {
            logger.error("specimen set already exists in the db", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("illegal state persisting specimens", ex);
        } catch (TransactionRequiredException ex) {
            logger.error("no transaction available", ex);
        } catch (IOException ex) {
            logger.error("", ex);
        } finally {
            if (closeCore) {
                this.closeCoreQuietly();
            }
        }
    }

    private void coreRun(String specimenResultsFilename, String procedureResultsFilename) {
        try {
            this.coreSerializer.run(specimenResultsFilename, procedureResultsFilename);
        } catch (JAXBException ex) {
            logger.error("error processing the xml files ", ex);
        } catch (FileNotFoundException ex) {
            logger.error("xml files not found ", ex);
        } catch (IllegalStateException ex) {
            logger.error("transaction is active", ex);
        } catch (EntityExistsException ex) {
            logger.error("specimen or procedure set already exists in the db", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("illegal state persisting specimens or procedures", ex);
        } catch (TransactionRequiredException ex) {
            logger.error("no transaction available", ex);
        } catch (IOException ex) {
            logger.error("", ex);
        } finally {
            this.closeCoreQuietly();
        }
    }

    private static void removeMLOG() {

        System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.log4j.Log4jMLog");
        System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "INFO");
        //http://www.mchange.com/projects/c3p0/index.html#log_properties_box
    }

    public static void main(String[] args) {

        XMLSerializer.removeMLOG();

        Argument<String> specimenResultsFilenameArgument = new Argument<>(String.class, "s", "specimens", "specimensFilename", true, "the file containing the specimens");
        Argument<String> procedureResultsFilenameArgument = new Argument<>(String.class, "p", "procedures", "procedureFilename", true, "the file containing the experimental results");
        Argument<Long> trackingIDArgument = new Argument<>(Long.class, "t", "trackingID", "trackingID", false, "the id for the file by the tracker");
        Argument<Calendar> submissionDateArgument = new Argument<>(Calendar.class, "r", "submissionDate", "submissionDate", false, "the id for the file by the tracker");
        Argument<String> persistencePropertiesFileNameArgument = new Argument<>(String.class, "d", "databaseProp", "databaseProp", false, "database properties filename If " + DCC_MASTER_KEYWORD + " defaults to localhost If " + DCC_DERBY_KEYWORD + " generates a new local derby database for each execution");
        //
        Argument<Boolean> isCoreArgument = new Argument<>(Boolean.class, "c", "isCore", "isCore", true, " say true if want to serialize specimen, procedures or both independently from submission");
        Argument<Boolean> test2dbSuccessfulArgument = new Argument<>(Boolean.class, "k", "komp", "comp", true, "if true checks that objects in memory are in the database");
        Argument<Boolean> createDatabaseArgument = new Argument<>(Boolean.class, "n", "databaseNew", "databaseNew", true, "if true creates a new database and then serialices the documents");
        //
        Argument<String> ListOfProceduresArgument = new Argument<>(String.class, "q", "listOfProcedureFilenames", "listOfProcedureFilenames", true, "wildcard to match names of xml procedures");
        Argument<String> ListOfSpecimentsArgument = new Argument<>(String.class, "u", "listOfSpecimenFilenames", "listOfSpecimenFilenames", true, "wildcard to match names of xml specimens");
        //
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(specimenResultsFilenameArgument);
        arguments.add(procedureResultsFilenameArgument);
        arguments.add(trackingIDArgument);
        arguments.add(submissionDateArgument);
        arguments.add(persistencePropertiesFileNameArgument);
        arguments.add(isCoreArgument);
        arguments.add(test2dbSuccessfulArgument);
        arguments.add(createDatabaseArgument);
        arguments.add(ListOfProceduresArgument);
        arguments.add(ListOfSpecimentsArgument);
        //
        XMLSerializer xMLSerializer = new XMLSerializer(arguments);
        xMLSerializer.setValues(args);
        //
        if (xMLSerializer.exceptionThrown) {
            System.exit(PARSE_ARGS_FAIL);
        }
        //
        if (args.length < 2 * MIN_ARGS || args.length > 2 * MAX_ARGS) {
            XMLSerializer.logger.error("number of arguments out of limits. {} args passed.10 max args \n{}", args.length, xMLSerializer.usage());
            System.exit(PARSE_ARGS_FAIL);
        }
        //
        if (!xMLSerializer.parsingOK()) {
            XMLSerializer.logger.error("parameters not parsed correctly", xMLSerializer.usage());
            System.exit(PARSE_ARGS_FAIL);
        }
        logger.info("parameters parsed correctly");



        if (isCoreArgument.getRawValue() != null && isCoreArgument.getValue()) {
            boolean serializeProcedures = false;
            boolean serializeSpecimens = false;
            if (procedureResultsFilenameArgument.getRawValue() != null
                    && !procedureResultsFilenameArgument.getRawValue().equals("")) {
                if (XMLSerializer.fileExists(procedureResultsFilenameArgument.getValue())) {
                    serializeProcedures = true;
                }
            }
            if (specimenResultsFilenameArgument.getRawValue() != null
                    && !specimenResultsFilenameArgument.getRawValue().equals("")) {
                if (XMLSerializer.fileExists(specimenResultsFilenameArgument.getValue())) {
                    serializeSpecimens = true;
                }
            }

            if (serializeProcedures || serializeSpecimens) {
                Properties persistenceProperties = null;
                try {
                    persistenceProperties = XMLSerializer.persistTo(persistencePropertiesFileNameArgument,
                            
                            createDatabaseArgument.getRawValue() != null ? createDatabaseArgument.getValue() : false);
                } catch (FileNotFoundException | ConfigurationException ex) {
                    logger.error("could not find file corresponding to {}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                } catch (IOException ex) {
                    logger.error("could not read file corresponding to {}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                }
                try {
                    xMLSerializer.coreSerializer = new CoreSerializer(PERSISTENCE_UNITNAME, persistenceProperties);
                } catch (PersistenceException ex) {
                    logger.error("error setting up database connections", ex);
                    System.exit(DB_ERROR_CONNECTION);
                }
                if (serializeProcedures && serializeSpecimens) {
                    xMLSerializer.coreRun(specimenResultsFilenameArgument.getValue(), procedureResultsFilenameArgument.getValue());
                }

                if (serializeProcedures && !serializeSpecimens) {
                    xMLSerializer.coreRun4procedures(procedureResultsFilenameArgument.getValue(), true);

                }
                if (!serializeProcedures && serializeSpecimens) {
                    xMLSerializer.coreRun4specimens(specimenResultsFilenameArgument.getValue(), true);
                }
                if (test2dbSuccessfulArgument.getRawValue() != null && test2dbSuccessfulArgument.getValue() == true) {
                    xMLSerializer.test2dbSuccessful(isCoreArgument.getValue(), serializeProcedures, serializeSpecimens);
                }

            }
            return;
        }

        //
        if (procedureResultsFilenameArgument.getRawValue() != null
                && !procedureResultsFilenameArgument.getRawValue().equals("")
                && !trackingIDArgument.getRawValue().equals("")
                && !submissionDateArgument.getRawValue().equals("")) {
            if (XMLSerializer.fileExists(procedureResultsFilenameArgument.getValue())) {
                Properties persistenceProperties = null;
                try {
                    persistenceProperties = XMLSerializer.persistTo(persistencePropertiesFileNameArgument,
                             
                            createDatabaseArgument.getRawValue() != null ? createDatabaseArgument.getValue() : false);
                } catch (FileNotFoundException ex) {
                    logger.error("could not find file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                } catch (IOException ex) {
                    logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                } catch (ConfigurationException ex) {
                    logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                }

                try {
                    xMLSerializer.trackerSerializer = new TrackerSerializer(PERSISTENCE_UNITNAME, persistenceProperties);
                } catch (PersistenceException ex) {
                    logger.error("error setting up database connections", ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                }
                //
                if (!xMLSerializer.trakerRun4procedures(procedureResultsFilenameArgument.getValue(),
                        trackingIDArgument.getValue(), submissionDateArgument.getValue(), true)) {
                    System.exit(DB_ERROR_SERIALIZING);
                }
                //
                if (test2dbSuccessfulArgument.getRawValue() != null && test2dbSuccessfulArgument.getValue() == true) {
                    xMLSerializer.test2dbSuccessful(false, true, false);
                }
            } else {
                logger.error("{} doesn't exist", procedureResultsFilenameArgument.getValue());
                System.exit(MISSING_XML_FILE_PATH);
            }
            logger.info("procedures serialized successfully");
            return;
        }

        if (specimenResultsFilenameArgument.getRawValue() != null
                && !specimenResultsFilenameArgument.getRawValue().equals("")
                && !trackingIDArgument.getRawValue().equals("")
                && !submissionDateArgument.getRawValue().equals("")) {
            if (XMLSerializer.fileExists(specimenResultsFilenameArgument.getValue())) {
                Properties persistenceProperties = null;
                try {
                    persistenceProperties = XMLSerializer.persistTo(persistencePropertiesFileNameArgument,
                            
                            createDatabaseArgument.getRawValue() != null ? createDatabaseArgument.getValue() : false);
                } catch (FileNotFoundException ex) {
                    logger.error("could not find file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                } catch (IOException ex) {
                    logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                } catch (ConfigurationException ex) {
                    logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                }
                try {
                    xMLSerializer.trackerSerializer = new TrackerSerializer(PERSISTENCE_UNITNAME, persistenceProperties);
                } catch (PersistenceException ex) {
                    logger.error("error setting up database connections", ex);
                    System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
                }

                if (!xMLSerializer.trakerRun4specimens(specimenResultsFilenameArgument.getValue(),
                        trackingIDArgument.getValue(),
                        submissionDateArgument.getValue(),
                        true)) {
                    System.exit(DB_ERROR_SERIALIZING);
                }
                if (test2dbSuccessfulArgument.getRawValue() != null && test2dbSuccessfulArgument.getValue() == true) {
                    xMLSerializer.test2dbSuccessful(false, true, false);
                }

            } else {
                logger.error("{} doesn't exist", specimenResultsFilenameArgument.getValue());
                System.exit(MISSING_XML_FILE_PATH);
            }
            logger.info("specimens serialized successfully");
            return;
        }

        if (ListOfProceduresArgument.getRawValue() != null) {
            Properties persistenceProperties = null;
            try {
                persistenceProperties = XMLSerializer.persistTo(persistencePropertiesFileNameArgument,
                       
                        createDatabaseArgument.getRawValue() != null ? createDatabaseArgument.getValue() : false);
            } catch (FileNotFoundException ex) {
                logger.error("could not find file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
            } catch (IOException ex) {
                logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
            } catch (ConfigurationException ex) {
                logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
            }
            try {
                xMLSerializer.trackerSerializer = new TrackerSerializer(PERSISTENCE_UNITNAME, persistenceProperties);
            } catch (PersistenceException ex) {
                logger.error("error setting up database connections", ex);
                System.exit(DB_ERROR_CONNECTION);
            }

            logger.info("current directory {}", JARUtils.getCwd(XMLSerializer.class));
            File[] files = JARUtils.getFiles(JARUtils.getCwd(XMLSerializer.class), ListOfProceduresArgument.getRawValue().replace("'", ""));
            for (File file : files) {
                try {
                    logger.info("persisting {}", file.getCanonicalPath());
                    xMLSerializer.trakerRun4procedures(file.getCanonicalPath(),
                            trackingIDArgument.getValue(),
                            submissionDateArgument.getValue(), false);
                } catch (IOException ex) {
                    logger.error("error persisting procedures", ex);
                }
            }
            xMLSerializer.closeTrackerQuietly();
            return;
        }

        if (ListOfSpecimentsArgument.getRawValue() != null) {
            Properties persistenceProperties = null;
            try {
                persistenceProperties = XMLSerializer.persistTo(persistencePropertiesFileNameArgument,
                        
                        createDatabaseArgument.getRawValue() != null ? createDatabaseArgument.getValue() : false);
            } catch (FileNotFoundException ex) {
                logger.error("could not find file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
            } catch (IOException ex) {
                logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
            } catch (ConfigurationException ex) {
                logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
            }
            try {
                xMLSerializer.trackerSerializer = new TrackerSerializer(PERSISTENCE_UNITNAME, persistenceProperties);
            } catch (PersistenceException ex) {
                logger.error("error setting up database connections", ex);
                System.exit(DB_ERROR_CONNECTION);
            }

            logger.info("current directory {}", JARUtils.getCwd(XMLSerializer.class));
            File[] files = JARUtils.getFiles(JARUtils.getCwd(XMLSerializer.class), ListOfProceduresArgument.getRawValue().replace("'", ""));

            for (File file : files) {
                try {
                    logger.info("persisting {}", file.getCanonicalPath());
                    //xMLSerializer.coreRun4specimens(file.getCanonicalPath(), false);
                    xMLSerializer.trakerRun4specimens(file.getCanonicalPath(),
                            trackingIDArgument.getValue(),
                            submissionDateArgument.getValue(),
                            false);
                } catch (IOException ex) {
                    logger.error("error persisting specimens", ex);
                }
            }
            //xMLSerializer.closeCoreQuietly();
            xMLSerializer.closeTrackerQuietly();
            return;
        }

        if (createDatabaseArgument.getRawValue() != null && createDatabaseArgument.getValue()) {
            Properties persistenceProperties = null;
            try {
                persistenceProperties = XMLSerializer.persistTo(persistencePropertiesFileNameArgument,
                       
                        true);
            } catch (FileNotFoundException ex) {
                logger.error("could not find file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
            } catch (IOException ex) {
                logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
            } catch (ConfigurationException ex) {
             logger.error("could not read file corresponding to{}", persistencePropertiesFileNameArgument.getRawValue(), ex);
                System.exit(DB_PROPERTIES_FILE_NOT_FOUND);
            }
            try {
                xMLSerializer.trackerSerializer = new TrackerSerializer(PERSISTENCE_UNITNAME, persistenceProperties);
            } catch (PersistenceException ex) {
                logger.error("error setting up database connections", ex);
                System.exit(DB_ERROR_CONNECTION);
            }
            xMLSerializer.closeTrackerQuietly();
            logger.info("database tables created successfully");
            return;
        }




        logger.error("parameter combination not understood {}", xMLSerializer.usage());

    }

    private static String getDerbyFilename() {
        return DatatypeConverter.printDateTimeForFilename(DatatypeConverter.now());
    }

    

    private static Properties persistTo(Argument<String> persistencePropertiesFileName,  Boolean create) throws FileNotFoundException, IOException, ConfigurationException {
        Properties properties = null;
        Reader reader = null;
        switch (persistencePropertiesFileName.getValue()) {
            case DCC_MASTER_KEYWORD:
                reader = new Reader(DCC_MASTER_DB_CONNECTION_PROPERTIES_FILENAME);
                properties = reader.getProperties();
                logger.info("persisting to DCC MASTER DB CONNECTION");
                break;
            case DCC_DERBY_KEYWORD:
                logger.info("persisting using {} derby connection", DCC_DERBY_KEYWORD);
                reader = new Reader(DCC_DERBY_DB_CONNECTION_PROPERTIES_FILENAME);
                properties = reader.getProperties();
                String derbyfilename = getDerbyFilename();
                
                properties.setProperty("hibernate.hbm2ddl.auto", "create");
                properties.setProperty("hibernate.ejb.entitymanager_factory_name", derbyfilename);

                properties.setProperty("hibernate.connection.url", "jdbc:derby:" + derbyfilename + ";create=true");
                logger.info("persisting to {}", derbyfilename);
                break;
            default:
                logger.info("persisting using {} configuration", persistencePropertiesFileName.getValue());
                try (FileInputStream fileInputStream = new FileInputStream(persistencePropertiesFileName.getValue())) {
                    properties = new Properties();

                    properties.load(fileInputStream);
                }
                break;
        }
        if (create) {
            properties.setProperty("hibernate.hbm2ddl.auto", "create");
        }


        return properties;
    }

    public XMLSerializer(List<Argument<?>> arguments) {
        super(arguments);
    }

    @Override
    public String example() {

        StringBuilder sb = new StringBuilder("example \n java -jar ");
        String jarFilename = "jarfile.jar";
        try {
            jarFilename = XMLSerializer.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            jarFilename = jarFilename.substring(jarFilename.lastIndexOf("/") == -1 ? 0 : jarFilename.lastIndexOf("/") + 1, jarFilename.length());
        } catch (Exception e) {
        }

        sb.append(jarFilename);
        sb.append(" -s specimenFile.xml ");
        sb.append(" -t 24");
        sb.append(" -d dcc");
        sb.append(" -r 2012-05-31'T'22:59:59Z ");
        sb.append(" \nor\n");

        sb.append(jarFilename);
        sb.append(" -p procedureResultsFile.xml \n");
        sb.append(" -t 24");
        sb.append(" -d databasePropertiesFilename.properties");
        sb.append(" -r 2012-05-31'T'22:59:59Z ");

        return sb.toString();
    }

    @Override
    public int getMinimumOptionalParameters() {
        return 3;
    }

    private static boolean fileExists(String filename) {
        if (new File(filename).exists()) {
            return true;
        } else {
            XMLSerializer.logger.error("{} No such file or directory", filename);
        }
        return false;
    }

    private void test2dbSuccessful(boolean isCore, boolean isprocedures, boolean isSpecimens) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
