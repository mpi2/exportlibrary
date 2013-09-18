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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.collectionmodifiers;

import java.util.Properties;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import junitx.util.PrivateAccessor;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class DerivedGenotypicInformationGeneratorTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DerivedGenotypicInformationGeneratorTest.class);
    private static HibernateManager hibernateManager;
    private static Reader reader;
    private static final String PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external";
    private static final String propertiesFilename = "xmlvalidationresources.derby.properties";
    //private static final String propertiesFilename = "/home/julian/executions/xmlvalidationresourcescollection/20130222/xmlvalidationresources.prince.mysql.properties";
    private static DerivedGenotypicInformationGenerator derivedGenotypicInformationGenerator = null;

    @BeforeClass
    public static void setup() {

        System.setProperty("derby.system.home", "src/test/resources/testDatabases");

        
        Properties properties = null;
        try {
            reader = new Reader(propertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        properties = reader.getProperties();
        Assert.assertNotNull(properties);

        try {
            hibernateManager = new HibernateManager(properties, PERSISTENCEUNITNAME);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }

    
        Assert.assertNotNull(properties);

        properties.remove("hibernate.hbm2ddl.auto");
        if (((String) properties.get("hibernate.connection.url")).contains("derby")) {
            properties.put("hibernate.connection.url", "jdbc:derby:src/test/resources/testDatabases/xmlvalidationresources");
        }

        try {
            hibernateManager = new HibernateManager(properties, PERSISTENCEUNITNAME);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        derivedGenotypicInformationGenerator = new DerivedGenotypicInformationGenerator();

        derivedGenotypicInformationGenerator.setHibernateManager(hibernateManager);

        try {
            PrivateAccessor.invoke(derivedGenotypicInformationGenerator, "setup", new Class[]{}, new Object[]{});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }

    }

    @Ignore
    @Test
    public void testmatchMGIIDs() {
        try {
            PrivateAccessor.invoke(derivedGenotypicInformationGenerator, "matchMGIIDs", new Class[]{}, new Object[]{});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }
        try {
            derivedGenotypicInformationGenerator.getHibernateManager().alter();
        } catch (TransactionRequiredException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (PersistenceException ex) {
            logger.error("", ex);
            Assert.fail();
        }

    }

    @Ignore
    @Test
    public void testGenerateDerivedGenotypicInformation() {
        try {
            PrivateAccessor.invoke(derivedGenotypicInformationGenerator, "generateDerivedGenotypicInformation", new Class[]{}, new Object[]{});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }
        try {
            derivedGenotypicInformationGenerator.getHibernateManager().alter();
        } catch (TransactionRequiredException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (PersistenceException ex) {
            logger.error("", ex);
            Assert.fail();
        }

    }
    
    @Test
    public void testAssignMGIIDs(){
        try {
            PrivateAccessor.invoke(derivedGenotypicInformationGenerator, "assignMGIIDs", new Class[]{}, new Object[]{});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @AfterClass
    public static void tearDown() {
        try {
            derivedGenotypicInformationGenerator.close();
        } catch (TransactionRequiredException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (PersistenceException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }
}
