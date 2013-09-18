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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;
import org.apache.commons.configuration.ConfigurationException;

import org.hibernate.HibernateException;
import org.junit.Assert;
import org.junit.Test;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class CacherTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CacherTest.class);
    private static final String PROPERTIES_FILENAME = "src/test/resources/hsqldb.properties";
    private static final String PERSISTENCE_UNIT_NAME = "org.mousephenotype.dcc.exportlibrary.external";
    private static HibernateManager hibernateManager;
    private static Reader reader;

    @Test
    public void foo() {
    }

    @Test
    public void testLoadFull() {

        Cacher writingImpressCache = null;
        Properties writeConnectionProperties = null;
        writingImpressCache = new Cacher();
        try {
            reader = new Reader(PROPERTIES_FILENAME);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            org.junit.Assert.fail();
        }
        writeConnectionProperties = reader.getProperties();

        Assert.assertNotNull(writeConnectionProperties);
        writeConnectionProperties.put("hibernate.ejb.entitymanager_factory_name", "Impress");

        try {
            CacherTest.hibernateManager = new HibernateManager(writeConnectionProperties, PERSISTENCE_UNIT_NAME);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        try {
            writingImpressCache.loadFromWS();
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        //writeConnectionProperties.put("hibernate.hbm2ddl.auto", "create");
        //writeConnectionProperties.put("hibernate.connection.url", "jdbc:hsqldb:file:hsqldb/Impress");


        Assert.assertNotNull(CacherTest.hibernateManager);
        try {
            hibernateManager.persist(writingImpressCache.getImpressPipelineContainer());
        } catch (IllegalStateException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (EntityExistsException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (TransactionRequiredException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (RuntimeException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        try {
            CacherTest.hibernateManager.close();
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }

    }
}
