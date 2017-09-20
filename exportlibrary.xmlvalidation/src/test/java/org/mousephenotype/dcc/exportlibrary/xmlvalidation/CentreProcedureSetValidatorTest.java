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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.*;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.SubmissionSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.VALIDATIONRESOURCES_IDS;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.imits.IMITSBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress.ImpressBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.support.SpecimenWSclient;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.utils.ValidationException;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class CentreProcedureSetValidatorTest {

    static {
        System.setProperty("derby.system.home", System.getProperty("test.databases.folder"));
    }
    private static final String contextPath = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CentreProcedureSetValidatorTest.class);
    private static final String EXTERNAL_RESOURCES_FOLDER = "test_databases";
   // private static SubmissionSet submissionSet;
   // private static Submission submission;
   // private static CentreProcedure centreProcedure;
   // private static CentreProcedureSet centreProcedureSet;
    private static CentreProcedureSetValidator centreProcedureSetValidator;
    //private static ValidationSet validationSet;
    //private static ValidationReportSet validationReportSet;
    private static Map<IOParameters.VALIDATIONRESOURCES_IDS, Incarnator<?>> xmlValidationResources;
    private static HibernateManager hibernateManager;
    private static HibernateManager hibernateManagerXMLResources;
    private static SpecimenWSclient specimenWSclient;
    private static String propertiesFilename = "hibernate.test.common.properties";
    private static String propertiesFilenameXMLResources = System.getProperty("test.databases.conf") + "/xmlvalidationresources.derby.properties";
    private static Properties properties;
    private static Properties propertiesXMLResources;

    @BeforeClass
    public static void setupCentreProcedure() {
        try {
            if (!(new File(EXTERNAL_RESOURCES_FOLDER).exists())) {
                new File(EXTERNAL_RESOURCES_FOLDER).mkdir();
            }

            properties = null;
            Reader reader = null;
            try {
                reader = new Reader(propertiesFilename);
            } catch (ConfigurationException ex) {
                logger.error("", ex);
                Assert.fail();
            }

            properties = reader.getProperties();
            Assert.assertNotNull(properties);

            hibernateManager = new HibernateManager(properties, contextPath);

            Assert.assertNotNull(hibernateManager);
            propertiesXMLResources = null;

            try {
                reader = new Reader(propertiesFilenameXMLResources);
            } catch (ConfigurationException ex) {
                logger.error("", ex);
                Assert.fail();
            }

            propertiesXMLResources = reader.getProperties();

            Assert.assertNotNull(propertiesXMLResources);

            hibernateManagerXMLResources = new HibernateManager(propertiesXMLResources, "org.mousephenotype.dcc.exportlibrary.xmlvalidation.external");

            Assert.assertNotNull(hibernateManagerXMLResources);

//            submissionSet = new SubmissionSet();
//            submission = new Submission();
//            centreProcedureSet = new CentreProcedureSet();
//            centreProcedure = new CentreProcedure();
//
//            submissionSet.getSubmission().add(submission);
//            submission.setCentreProcedure(centreProcedureSet.getCentre());
//
//            centreProcedure.setCentreID(CentreILARcode.J);
//            centreProcedure.setPipeline("IMPC_001");
//            centreProcedure.setProject("IMPC_001");
//            centreProcedureSet.getCentre().add(centreProcedure);
//
//            Experiment experiment = new Experiment();
//            centreProcedure.getExperiment().add(experiment);
//            Procedure procedure = new Procedure();
//            procedure.setProcedureID("IMPC_ACS_001");
//            experiment.setProcedure(procedure);
//
//            SimpleParameter simpleParameter = new SimpleParameter();
//            simpleParameter.setParameterID("IMPC_ACS_012_001");
//            simpleParameter.setValue("2013-03-01T00:00:00+0000");
//            procedure.getSimpleParameter().add(simpleParameter);

            //
            //validationSet = new ValidationSet();
           // validationReportSet = new ValidationReportSet();
            //
            xmlValidationResources = new EnumMap<>(IOParameters.VALIDATIONRESOURCES_IDS.class);

            xmlValidationResources.put(VALIDATIONRESOURCES_IDS.ImpressBrowser, new ImpressBrowser(hibernateManagerXMLResources));
            xmlValidationResources.put(VALIDATIONRESOURCES_IDS.Imits, getIMITSBrowser());
            

            specimenWSclient = new SpecimenWSclient("http://mousephenotype.org/phenodcc-validation-ws/resources/mousesummary/byspecimenid");

//            try {
//                centreProcedureSetValidator = new CentreProcedureSetValidator(centreProcedureSet, validationSet, validationReportSet, xmlValidationResources, specimenWSclient);
//            } catch (Exception ex) {
//                logger.error("", ex);
//                Assert.fail();
//            }

//            try {
//                //hibernateManager.persist(centreProcedureSet);
//                hibernateManager.persist(submissionSet);
//            } catch (Exception ex) {
//                logger.error("", ex);
//                Assert.fail();
//            }
//            logger.info("validating example");
//            centreProcedureSetValidator.validateWithHandler();
//            centreProcedureSetValidator.compileValidationSet();
//            logger.info("persisting {} results", validationSet.getValidation().size());
//            hibernateManager.persist(validationSet);
//            logger.info("validation process finished");
        } catch (IllegalArgumentException iae) {
            logger.error("An IllegalArgument Exception was thown! This may well have been the result of the databases not being populated");
            // do nothing! Assert.fail();
        } catch (Exception ex) {
            logger.error("An exception was thrown!", ex);
            Assert.fail();
        }

    }

    private static IMITSBrowser getIMITSBrowser() {
        IMITSBrowser iMITSBrowser = new IMITSBrowser(hibernateManagerXMLResources);
        Properties imitsClientProperties = new Properties();
        imitsClientProperties.put("imits.username", "j.atienza@har.mrc.ac.uk");
        imitsClientProperties.put("imits.password", "4geneData!");
        iMITSBrowser.setConnectionProperties(imitsClientProperties);
        return iMITSBrowser;
    }

    @AfterClass
    public static void teardown() {
        hibernateManager.close();
        hibernateManagerXMLResources.close();
    }


    @Ignore
    @Test
    public void testRemoveValidationResults() {
        List<ValidationSet> query = hibernateManager.query("from ValidationSet", ValidationSet.class);
        //
        hibernateManager.remove(query.get(0));
        //
        query = hibernateManager.query("from ValidationSet", ValidationSet.class);
        Assert.assertTrue(query.isEmpty());
        //
        List<Validation> query1 = hibernateManager.query("from Validation", Validation.class);
        Assert.assertTrue(query1.isEmpty());
        //
        List<CentreProcedureSet> query2 = hibernateManager.query("from CentreProcedureSet", CentreProcedureSet.class);
        Assert.assertFalse(query2.isEmpty());
        //
        List<CentreProcedure> query3 = hibernateManager.query("from CentreProcedure", CentreProcedure.class);
        Assert.assertFalse(query3.isEmpty());
        //
        List<Experiment> query4 = hibernateManager.query("from Experiment", Experiment.class);
        Assert.assertFalse(query4.isEmpty());
        //
        List<Procedure> query5 = hibernateManager.query("from Procedure", Procedure.class);
        Assert.assertFalse(query5.isEmpty());
        //
        List<SimpleParameter> query6 = hibernateManager.query("from SimpleParameter", SimpleParameter.class);
        Assert.assertFalse(query6.isEmpty());

    }

    @Test
    public void testNoIncrementValue() {
        CentreProcedureSet centreProcedureSetInternal = null;
        try {
            centreProcedureSetInternal = XMLUtils.unmarshal(contextPath, CentreProcedureSet.class, "src/test/resources/data/SeriesParameterNoIncrementValue.xml");
        } catch (JAXBException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedureSetInternal);

        try {
            ValidationSet validationSet = new ValidationSet();
            ValidationReportSet validationReportSet = new ValidationReportSet();
            CentreProcedureSetValidator validator2 = new CentreProcedureSetValidator(centreProcedureSetInternal, validationSet, validationReportSet, xmlValidationResources, specimenWSclient);
            validator2.validateWithHandler();
            Assert.assertTrue(validator2.getErrorExceptions().size() > 1);
            for (ValidationException errorException : validator2.getErrorExceptions()) {
                Assert.assertTrue(errorException.getValidation().getTestID().equalsIgnoreCase("CentreProcedureSetValidator_seriesParameterValueIncrementStatusEmpty"));
                break;
            }
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testOntologyTermTest() {
        CentreProcedureSet centreProcedureSetInternal = null;
        try {
            centreProcedureSetInternal = XMLUtils.unmarshal(contextPath, CentreProcedureSet.class, "src/test/resources/data/J.2014-07-23.01.experiment.impc.xml");
        } catch (JAXBException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedureSetInternal);

        try {
            ValidationSet validationSet = new ValidationSet();
            ValidationReportSet validationReportSet = new ValidationReportSet();
            CentreProcedureSetValidator validator2  = new CentreProcedureSetValidator(centreProcedureSetInternal, validationSet, validationReportSet, xmlValidationResources, specimenWSclient);
            validator2.validateWithHandler();
            Assert.assertTrue(validator2.getErrorExceptions().size() > 1);
            for (ValidationException errorException : validator2.getErrorExceptions()) {

                logger.info("{} {}", errorException.getValidation().getTestID(), errorException.getMessage());
//                Assert.assertTrue(errorException.getValidation().getTestID().equalsIgnoreCase("CentreProcedureSetValidator_seriesParameterValueIncrementStatusEmpty"));
//                break;
            }
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail(ex.getMessage());
        }
    }

    
    @Test
    public void testKoreaILAR() {
        CentreProcedureSet centreProcedureSetInternal = null;
        try {
            centreProcedureSetInternal = XMLUtils.unmarshal(contextPath, CentreProcedureSet.class, "src/test/resources/data/Krb.2016-04-15.1.specimen.impc.xml");
        } catch (JAXBException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedureSetInternal);

        try {
            ValidationSet validationSet = new ValidationSet();
            ValidationReportSet validationReportSet = new ValidationReportSet();
            CentreProcedureSetValidator validator2 =  new CentreProcedureSetValidator(centreProcedureSetInternal, validationSet, validationReportSet, xmlValidationResources, specimenWSclient);
            validator2.validateWithHandler();
            Assert.assertTrue(validator2.getErrorExceptions().size() == 0);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail(ex.getMessage());
        }
    }
    
    @Test
    public void testWrongColonyInLine() {
        CentreProcedureSet centreProcedureSetInternal = null;
        try {
            centreProcedureSetInternal = XMLUtils.unmarshal(contextPath, CentreProcedureSet.class, "src/test/resources/data/Ucd.2014-03-18.308.experiment.impc.xml");
        } catch (JAXBException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedureSetInternal);

        try {
            ValidationSet validationSet = new ValidationSet();
            ValidationReportSet validationReportSet = new ValidationReportSet();
            CentreProcedureSetValidator validator2 =  new CentreProcedureSetValidator(centreProcedureSetInternal, validationSet, validationReportSet, xmlValidationResources, specimenWSclient);
            validator2.validateWithHandler();
            Assert.assertTrue(validator2.getErrorExceptions().size() == 1);
            for (ValidationException errorException : validator2.getErrorExceptions()) {

                logger.info("{} {}", errorException.getValidation().getTestID(), errorException.getMessage());
                Assert.assertTrue(errorException.getValidation().getTestID().equalsIgnoreCase("CentreProcedureSetValidator_line_colony_no_found"));
//                break;
            }
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail(ex.getMessage());
        }
    }

     @Test
    public void testEmptyColonyInLine() {
        CentreProcedureSet centreProcedureSetInternal = null;
        try {
            centreProcedureSetInternal = XMLUtils.unmarshal(contextPath, CentreProcedureSet.class, "src/test/resources/data/Tcp.2013-10-15.50.experiment.impc.xml");
        } catch (JAXBException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedureSetInternal);

        try {
            ValidationSet validationSet = new ValidationSet();
            ValidationReportSet validationReportSet = new ValidationReportSet();
            CentreProcedureSetValidator validator2 =  new CentreProcedureSetValidator(centreProcedureSetInternal, validationSet, validationReportSet, xmlValidationResources, specimenWSclient);
            validator2.validateWithHandler();
            Assert.assertTrue(validator2.getErrorExceptions().size() == 2);
            for (ValidationException errorException : validator2.getErrorExceptions()) {

                logger.info("{} {}", errorException.getValidation().getTestID(), errorException.getMessage());
                Assert.assertTrue(errorException.getValidation().getTestID().equalsIgnoreCase("CentreProcedureSetValidator_line_colony_no_found"));
//                break;
            }
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail(ex.getMessage());
        }
    }
    
    @Test
    public void testStatusCodeOfUnknownColonies(){
                CentreProcedureSet centreProcedureSetInternal = null;
        try {
            centreProcedureSetInternal = XMLUtils.unmarshal(contextPath, CentreProcedureSet.class, "src/test/resources/data/Tcp.2015-02-17.117.experiment.impc.xml");
        } catch (JAXBException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedureSetInternal);

        try {
            ValidationSet validationSet = new ValidationSet();
            ValidationReportSet validationReportSet = new ValidationReportSet();
            CentreProcedureSetValidator validator2 =  new CentreProcedureSetValidator(centreProcedureSetInternal, validationSet, validationReportSet, xmlValidationResources, specimenWSclient);
            validator2.validateWithHandler();
            Assert.assertTrue(validator2.getErrorExceptions().isEmpty());
            
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail(ex.getMessage());
        }
    }
    
    @Ignore
    @Test
    public void testFile() {
        CentreProcedureSet centreProcedureSetInternal = null;
        try {
            centreProcedureSetInternal = XMLUtils.unmarshal(contextPath, CentreProcedureSet.class, "src/test/resources/data/J.2014-07-23.01.experiment.impc.xml");
        } catch (JAXBException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedureSetInternal);
        CentreProcedureSetValidator validator2 = null;
        try {
            ValidationSet validationSet = new ValidationSet();
            ValidationReportSet validationReportSet = new ValidationReportSet();
            validator2 = new CentreProcedureSetValidator(centreProcedureSetInternal, validationSet, validationReportSet, xmlValidationResources, specimenWSclient);
        
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }

        Assert.assertNotNull(validator2);
        try {
            validator2.validateWithHandler();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }

        Assert.assertTrue(validator2.getErrorExceptions().size() > 1);
        for (ValidationException errorException : validator2.getErrorExceptions()) {
            logger.info("{} {}", errorException.getValidation().getTestID(), errorException.getMessage());
        }

    }

}
