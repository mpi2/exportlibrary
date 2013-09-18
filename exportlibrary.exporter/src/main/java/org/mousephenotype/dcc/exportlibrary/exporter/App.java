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
package org.mousephenotype.dcc.exportlibrary.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.exporter.dbloading.Loader;
import org.mousephenotype.dcc.exportlibrary.exporter.xmlgeneration.XMLGenerator;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.io.console.Argument;
import org.mousephenotype.dcc.utils.io.console.ConsoleReader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends ConsoleReader {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private Reader reader;
    private HibernateManager hibernateManager;
    private Loader loader;
    private XMLGenerator xmlGenerator;

    public App(List<Argument<?>> arguments) {
        super(arguments);

    }

    public static void main(String[] args) {

        List<Argument<?>> arguments;
        arguments = new ArrayList<>();
        //
        Argument<String> persistencePropertiesFileNameArgument = new Argument<>(String.class, "p", "databaseProp", "databaseProp", false, "database properties filename");
        Argument<String> strainIDArgument = new Argument<>(String.class, "s", "strainID", "strainID", false, "strainID to export from the database");
        Argument<Boolean> mutantsArgument = new Argument<>(Boolean.class, "m", "mutants", "mutants", false, "valid mutants to export from the database");
        Argument<Boolean> baselineArgument = new Argument<>(Boolean.class, "b", "baseline", "baseline", false, "valid mutants to export from the database");
        Argument<Boolean> singleColonyIDArgument = new Argument<>(Boolean.class, "g", "single", "single", false, "valid mutants to export from the database");
        Argument<Boolean> experimentsArgument = new Argument<>(Boolean.class, "e", "experiments", "experiments", false, "valid experiments with valid specimens to export from the database");
        Argument<Boolean> experimentsBaselineArgument = new Argument<>(Boolean.class, "x", "experimentsOnbaselineanimals", "experimentsOnbaselineanimals", false, "valid experiments with valid specimens on baseline animals to export from the database");
        Argument<String> colonyIDArgument = new Argument<>(String.class, "c", "colonyID", "colonyID", false, "colonyID to extract the experiments ");
        Argument<String> resultFilenameArgument = new Argument<>(String.class, "r", "result", "result", false, "result filename");
        //
        arguments.add(persistencePropertiesFileNameArgument);
        arguments.add(strainIDArgument);
        arguments.add(mutantsArgument);
        arguments.add(resultFilenameArgument);
        arguments.add(experimentsArgument);
        arguments.add(colonyIDArgument);
        arguments.add(singleColonyIDArgument);
        arguments.add(experimentsBaselineArgument);
        arguments.add(baselineArgument);
        //
        App app = new App(arguments);
        app.setValues(args);

        if (app.exceptionThrown) {
            System.exit(-1);
        }

        if (persistencePropertiesFileNameArgument.getValue() == null || persistencePropertiesFileNameArgument.getValue().isEmpty()) {
            logger.error("persistencePropertiesFilenameArgument required");
            System.exit(-1);
        } else {
            app.setup(persistencePropertiesFileNameArgument.getValue());
        }
        String resultFilename;
        if (resultFilenameArgument.getValue() == null || resultFilenameArgument.getValue().isEmpty()) {
            resultFilename = app.getResultFilename(true);
        } else {
            resultFilename = resultFilenameArgument.getValue();
        }

        if (mutantsArgument.getRawValue() != null && mutantsArgument.getValue()) {
            logger.info("extracting mutants");
            app.run(resultFilename);
            System.exit(0);
        }
        
        
        if(baselineArgument.getRawValue()!=null  && baselineArgument.getValue()){
            logger.info("extracting baseline animals");
            app.runBaselineAnimals(resultFilename);
            System.exit(0); //if everything goes OK for integration tests.
        }
        
        if (strainIDArgument.getRawValue() != null && !strainIDArgument.getValue().isEmpty()) {
            app.run(strainIDArgument.getValue(), resultFilename);
            System.exit(0); //if everything goes OK for integration tests.
        }
        if (experimentsArgument.getRawValue() != null && experimentsArgument.getValue()) {
            String colonyID = null;
            if (colonyIDArgument.getRawValue() != null && !colonyIDArgument.getRawValue().isEmpty()) {
                colonyID = colonyIDArgument.getRawValue();
                logger.info("valid experimens for colonyID {}",colonyID);
                app.runValidExperiments(colonyID, resultFilename);
                System.exit(0); //if everything goes OK for integration tests.
            }
        }

        if (experimentsArgument.getRawValue() != null && experimentsArgument.getValue()) {
            logger.info("loading valid experiments");
            app.runValidExperiments();
            System.exit(0); //if everything goes OK for integration tests.
        }

        if (singleColonyIDArgument.getRawValue() != null && singleColonyIDArgument.getValue()) {
            logger.info("extracting an example for each colonyID");
            app.getSingleColonyID(resultFilename);
            System.exit(0); //if everything goes OK for integration tests.
        }

        if (experimentsBaselineArgument.getRawValue() != null && experimentsBaselineArgument.getValue()) {
            logger.info("running valid experiments on baseline animals");
            app.runValidExperimentsOnBaselineAnimals();
            System.exit(0); //if everything goes OK for integration tests.
        }


    }

    @Override
    public String example() {
        return "";
    }

    @Override
    public int getMinimumOptionalParameters() {
        return -1;
    }
    /*
     Centres . yyy-mm-dd.impc.zip

     centres.yyy-mm-dd.experiment.impc.xml
     * 
     * 2013_06_17T13_37_10+0000.
     * 
     * H.2012-11-20.03.experiment.impc.xml
     * UCD.2013-06-17.experiment.impc.xml
     * UCD2013-06-17experiment.impc.xml

     */

    public String getColonyIDFilename(String colonyID) {
        return "experiments_for_" + colonyID + "_" + DatatypeConverter.printDateTimeForFilename(DatatypeConverter.now()) + ".xml";
    }

    public String getExperimentsFilename(CentreProcedureSet centreProcedureSet) {
        StringBuilder sb = new StringBuilder();
        Set<CentreILARcode> centreIDs = new HashSet<>();

        for (CentreProcedure centreProcedure : centreProcedureSet.getCentre()) {
            centreIDs.add(centreProcedure.getCentreID());
        }
        Iterator<CentreILARcode> it = centreIDs.iterator();
        while (it.hasNext()) {
            sb.append(it.next().value());
            sb.append(".");
        }
        //sb.deleteCharAt(sb.lastIndexOf("_"));

        sb.append(DatatypeConverter.printDate(DatatypeConverter.now()));
        //sb.append(".experiment.impc.xml");
        return sb.toString();
    }
    private Map<String, Integer> maxFilenames = new HashMap<>();

    public String assignFilename(String experimentFilename) {
        Integer value = 0;
        if (maxFilenames.containsKey(experimentFilename)) {
            value = maxFilenames.get(experimentFilename);
            maxFilenames.put(experimentFilename, ++value);
            return experimentFilename + "." + value + ".experiment.impc.xml";

        } else {
            maxFilenames.put(experimentFilename, 0);
            return experimentFilename + ".experiment.impc.xml";
        }
    }

    private void runValidExperiments() {
        this.loader = new Loader(hibernateManager);
        List<String> colonyIDsForValidExperiments = this.loader.getColonyIDsForValidExperiments();
        CentreProcedureSet validExperiments = null;
        if (colonyIDsForValidExperiments != null && !colonyIDsForValidExperiments.isEmpty()) {
            this.xmlGenerator = new XMLGenerator();
            for (String colonyID : colonyIDsForValidExperiments) {
                logger.info("extracting info for colonyID {}", colonyID);
                validExperiments = this.loader.getValidExperiments(colonyID);
                try {
                    String filename = assignFilename(getExperimentsFilename(validExperiments));
                    logger.info("serializing info for colonyID {} to {}", colonyID, filename);

                    this.xmlGenerator.serialize(validExperiments, filename);
                } catch (JAXBException ex) {
                    logger.error("", ex);
                }
            }
        } else {
            logger.error("no colonies found");
        }
        this.tearDown();
    }

    private void runValidExperimentsOnBaselineAnimals() {
        this.loader = new Loader(hibernateManager);
        List<String> colonyIDsForValidExperiments = this.loader.getColonyIDsForValidExperimentsOnBaselineAnimals();
        CentreProcedureSet validExperiments = null;
        if (colonyIDsForValidExperiments != null && !colonyIDsForValidExperiments.isEmpty()) {
            this.xmlGenerator = new XMLGenerator();
            for (String colonyID : colonyIDsForValidExperiments) {
                logger.info("extracting info for colonyID {}", colonyID);
                validExperiments = this.loader.getValidExperiments(colonyID);
                try {
                    String filename = assignFilename(getExperimentsFilename(validExperiments));
                    logger.info("serializing info for colonyID {} to {}", colonyID, filename);

                    this.xmlGenerator.serialize(validExperiments, filename);
                } catch (JAXBException ex) {
                    logger.error("", ex);
                }
            }
        } else {
            logger.error("no colonies found");
        }
        this.tearDown();
    }

    private void runValidExperiments(String colonyID, String filename) {
        this.loader = new Loader(hibernateManager);
        CentreProcedureSet centreProcedureSet = null;
        try {
            centreProcedureSet = this.loader.getValidExperiments(colonyID);
        } catch (Exception ex) {
            logger.error("", ex);

        }
        if (centreProcedureSet != null && centreProcedureSet.isSetCentre()) {
            this.xmlGenerator = new XMLGenerator();
            logger.info("serializing results to {}", filename);
            try {
                this.xmlGenerator.serialize(centreProcedureSet, filename);
            } catch (JAXBException ex) {
                logger.error("", ex);
            }
        } else {
            logger.error("no valid mutants found in the database");
        }
        this.tearDown();
    }

    
    
    private void runBaselineAnimals(String filename) {
        this.loader = new Loader(hibernateManager);
        CentreSpecimenSet centreSpecimenSet = null;
        try {
            centreSpecimenSet = this.loader.getValidBaselines();

        } catch (ConfigurationException ex) {
            logger.error("", ex);

        }

        if (centreSpecimenSet != null) {
            this.xmlGenerator = new XMLGenerator();
            logger.info("serializing results to {}", filename);
            try {
                this.xmlGenerator.serialize(centreSpecimenSet, filename);
            } catch (JAXBException ex) {
                logger.error("", ex);
            }
        } else {
            logger.error("no valid mutants found in the database");
        }

        this.tearDown();
    }
    
    
    
    //running valid mutants
    private void run(String filename) {
        this.loader = new Loader(hibernateManager);
        CentreSpecimenSet centreSpecimenSet = null;
        try {
            centreSpecimenSet = this.loader.getValidMutants();

        } catch (ConfigurationException ex) {
            logger.error("", ex);

        }

        if (centreSpecimenSet != null) {
            this.xmlGenerator = new XMLGenerator();
            logger.info("serializing results to {}", filename);
            try {
                this.xmlGenerator.serialize(centreSpecimenSet, filename);
            } catch (JAXBException ex) {
                logger.error("", ex);
            }
        } else {
            logger.error("no valid mutants found in the database");
        }

        this.tearDown();
    }

    private void getSingleColonyID(String filename) {
        this.loader = new Loader(hibernateManager);
        CentreSpecimenSet centreSpecimenSet = null;
        try {
            centreSpecimenSet = this.loader.getSingleColonyID();

        } catch (ConfigurationException ex) {
            logger.error("", ex);

        }

        if (centreSpecimenSet != null) {
            this.xmlGenerator = new XMLGenerator();
            logger.info("serializing results to {}", filename);
            try {
                this.xmlGenerator.serialize(centreSpecimenSet, filename);
            } catch (JAXBException ex) {
                logger.error("", ex);
            }
        } else {
            logger.error("no valid mutants found in the database");
        }
        this.tearDown();
    }

    private void run(String strainID, String filename) {
        this.loader = new Loader(hibernateManager);
        CentreSpecimenSet centreSpecimenSet = null;

        try {
            logger.info("trying to load {} strainID", strainID);
            centreSpecimenSet = this.loader.getStrain(strainID);
        } catch (Exception ex) {
            logger.error("", ex);
        }

        if (centreSpecimenSet != null) {
            logger.info("serializing results to {}", filename);
            this.xmlGenerator = new XMLGenerator();
            try {
                this.xmlGenerator.serialize(centreSpecimenSet, filename);
            } catch (JAXBException ex) {
                logger.error("", ex);
            }
        } else {
            logger.error("no strains found with strainID {}", strainID);
        }

        this.tearDown();

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
    }

    private void tearDown() {
        try {
            this.hibernateManager.close();
        } catch (Exception ex) {
            logger.error("error closing database connection", ex);
        }
    }

    private String getResultFilename(Boolean isSpecimen) {
        if (isSpecimen) {
            return "specimen_" + DatatypeConverter.printDateTimeForFilename(DatatypeConverter.now()) + "_.xml";
        } else {
            return "procedures_" + DatatypeConverter.printDateTimeForFilename(DatatypeConverter.now()) + "_.xml";
        }
    }

    
}
