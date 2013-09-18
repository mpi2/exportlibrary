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
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class CoreSerializerTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CoreSerializerTest.class);
    private static Reader reader;
    private static Properties persistenceProperties = null;
    private static final String derbyPropertiesFilename = "derby.properties";
    private static final String specimenResultsFilename = "data/specimens.xml";
    private static final String procedureResultsFilename = "data/procedures.xml";
    private static CoreSerializer coreSerializer = null;
    public static HibernateManager hibernateManager;

    @BeforeClass
    public static void setup() {
        CoreSerializerTest.persistenceProperties = null;
        try {
            reader = new Reader(derbyPropertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("file {} cannot be found", derbyPropertiesFilename, ex);
        }
        persistenceProperties = reader.getProperties();

        persistenceProperties.setProperty("hibernate.hbm2ddl.auto", "create");

        persistenceProperties.setProperty("hibernate.connection.url", "jdbc:derby:test_databases/coreserializer;create=true");
        persistenceProperties.setProperty("hibernate.ejb.entitymanager_factory_name", "coreSerializer_derby_localhost");


        CoreSerializerTest.hibernateManager = new HibernateManager(persistenceProperties, CoreSerializer.PERSISTENCE_UNITNAME);
        CoreSerializerTest.coreSerializer = new CoreSerializer(CoreSerializerTest.hibernateManager);
    }

    @AfterClass
    public static void tearDown() {
        try {
            CoreSerializerTest.coreSerializer.close();
        } catch (IllegalStateException ex) {
            logger.error("error closing coreSerializer", ex);
        }
    }

    @Test
    public void testRun() {
        try {
            coreSerializer.run(specimenResultsFilename, procedureResultsFilename);
        } catch (JAXBException ex) {
            logger.error("error processing the xml files ", ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("xml files not found ", ex);
            Assert.fail();
        } catch (IllegalStateException ex) {
            logger.error("transaction is active", ex);
            Assert.fail();
        } catch (EntityExistsException ex) {
            logger.error("specimen or procedure set already exists in the db", ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("illegal state persisting specimens or procedures", ex);
            Assert.fail();
        } catch (TransactionRequiredException ex) {
            logger.error("no transaction available", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        List<CentreSpecimenSet> query = null;
        try {
            query = CoreSerializerTest.hibernateManager.query("from CentreSpecimenSet", CentreSpecimenSet.class);
        } catch (IllegalStateException | PersistenceException ex) {
            logger.error("exception reading the database", ex);
        }
        Assert.assertNotNull(query);
        Assert.assertTrue(query.size() > 0);
        Assert.assertTrue(query.get(0).getCentre().get(0).getCentreID().equals(CentreILARcode.J));

        List<CentreProcedureSet> query2 = null;
        try {
            query2 = CoreSerializerTest.hibernateManager.query("from CentreProcedureSet", CentreProcedureSet.class);
        } catch (IllegalStateException | PersistenceException ex) {
            logger.error("exception reading the database", ex);
        }
        Assert.assertNotNull(query2);
        Assert.assertTrue(query2.size() > 0);
        Assert.assertTrue(query2.get(0).getCentre().get(0).getCentreID().equals(CentreILARcode.J));
    }

    @Test
    public void testserializeSpecimensOnly() {

        try {
            coreSerializer.serializeSpecimensOnly(specimenResultsFilename);
        } catch (JAXBException ex) {
            logger.error("error processing the xml files ", ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("xml files not found ", ex);
            Assert.fail();
        } catch (IllegalStateException ex) {
            logger.error("transaction is active", ex);
            Assert.fail();
        } catch (EntityExistsException ex) {
            logger.error("specimen or procedure set already exists in the db", ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("illegal state persisting specimens or procedures", ex);
            Assert.fail();
        } catch (TransactionRequiredException ex) {
            logger.error("no transaction available", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testserializeProceduresOnly() {

        try {
            coreSerializer.serializeProceduresOnly(procedureResultsFilename);
        } catch (JAXBException ex) {
            logger.error("error processing the xml files ", ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("xml files not found ", ex);
            Assert.fail();
        } catch (IllegalStateException ex) {
            logger.error("transaction is active", ex);
            Assert.fail();
        } catch (EntityExistsException ex) {
            logger.error("specimen or procedure set already exists in the db", ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("illegal state persisting specimens or procedures", ex);
            Assert.fail();
        } catch (TransactionRequiredException ex) {
            logger.error("no transaction available", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }
}
