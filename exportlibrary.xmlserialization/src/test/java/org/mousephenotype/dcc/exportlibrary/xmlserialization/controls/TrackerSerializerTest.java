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
package org.mousephenotype.dcc.exportlibrary.xmlserialization.controls;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;

import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class TrackerSerializerTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TrackerSerializerTest.class);
    private static Reader reader;
    private static Properties derbyProperties;
    private static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    private static final String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    
    private static final String derbyPropertiesFilename = "derby.properties";
    private static final String derbyfilename = "trackerSerializer";
    public static TrackerSerializer trackerSerializer;
    public static HibernateManager hibernateManager;
    private static final String proceduresFilename = "data/procedures.xml";
    private static final String specimenFilename = "data/specimens.xml";
    private static final long trackerID = 10L;
    //"yyyy-MM-dd'T'HH:mm:ssZ";
    private static Calendar submissionDate = DatatypeConverter.parseDateTime("2012-09-04T12:50:00+0000");

    @BeforeClass
    public static void setup() {

        try {
            reader = new Reader(derbyPropertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("error reading {} file", derbyPropertiesFilename, ex);
            Assert.fail();
        }
        derbyProperties = reader.getProperties();

        Assert.assertNotNull(derbyProperties);

        derbyProperties.setProperty("hibernate.hbm2ddl.auto", "create");
        derbyProperties.setProperty("hibernate.connection.url", "jdbc:derby:test_databases/trackerserializer;create=true");
        derbyProperties.setProperty("hibernate.ejb.entitymanager_factory_name", "coreSerializer_derby_localhost");
        derbyProperties.setProperty("hibernate.ejb.entitymanager_factory_name", derbyfilename);

        hibernateManager = new HibernateManager(derbyProperties, PERSISTENCE_UNITNAME);

        try {
            TrackerSerializerTest.trackerSerializer = new TrackerSerializer(hibernateManager);
        } catch (PersistenceException ex) {
            logger.error("error getting persistence instance", ex);
            Assert.fail();
        }
        Assert.assertNotNull(TrackerSerializerTest.trackerSerializer);
    }

    @AfterClass
    public static void tearDown() {
        try {
            TrackerSerializerTest.trackerSerializer.fullClose();
        } catch (IllegalStateException ex) {
            logger.error("error closing the entity manager factory", ex);
        }
    }

    @Test
    public void serializeProcedureSubmissionTest() {

        Long submissionhjID = -1L;

        try {
            submissionhjID = TrackerSerializerTest.trackerSerializer.serializeProcedureSubmissionSet(trackerID, submissionDate, proceduresFilename);
        }catch(FileNotFoundException ex){
            logger.error("", ex);
            Assert.fail();
        }
        catch (JAXBException | IllegalStateException | EntityExistsException | IllegalArgumentException | TransactionRequiredException | XMLloadingException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        Assert.assertTrue(submissionhjID != -1L);

        logger.info("submission hjid {} ", submissionhjID);



        Submission submission = hibernateManager.load(Submission.class, submissionhjID);

        List<CentreProcedure> centreProcedures = null;
        try {
            centreProcedures = XMLUtils.unmarshal(CONTEXT_PATH, CentreProcedureSet.class, proceduresFilename).getCentre();
        } catch (JAXBException | FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        Assert.assertNotNull(centreProcedures);

        Assert.assertEquals(centreProcedures, submission.getCentreProcedure());

    }

    @Test
    public void serializeSpecimenSubmissionTest() {
        Long submissionhjID = -1L;
        try {
            submissionhjID = TrackerSerializerTest.trackerSerializer.serializeSpecimenSubmissionSet(trackerID, submissionDate, specimenFilename);
        } catch (FileNotFoundException | JAXBException | IllegalStateException | EntityExistsException | IllegalArgumentException | TransactionRequiredException | XMLloadingException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        logger.info("submission hjid {} ", submissionhjID);

        Assert.assertTrue(submissionhjID != -1L);

        Submission submission = hibernateManager.load(Submission.class, submissionhjID);


        List<CentreSpecimen> centreSpecimens = null;
        try {
            centreSpecimens = XMLUtils.unmarshal(CONTEXT_PATH, CentreSpecimenSet.class, specimenFilename).getCentre();
        } catch (JAXBException | FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        Assert.assertNotNull(centreSpecimens);
        Assert.assertEquals(centreSpecimens, submission.getCentreSpecimen());

    }
}
