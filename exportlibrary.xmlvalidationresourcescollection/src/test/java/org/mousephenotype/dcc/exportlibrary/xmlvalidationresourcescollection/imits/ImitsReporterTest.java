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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.imits;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;

import org.hibernate.HibernateException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ImitsReporterTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImitsReporterTest.class);
    private static HibernateManager hibernateManager;
    private static Reader reader;
    private static final String PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external";
    //private static final String propertiesFilename = "/home/julian/executions/xmlvalidationresourcescollection/20130222/xmlvalidationresources.prince.mysql.properties";
    private static final String propertiesFilename = "xmlvalidationresources.derby.properties";
    //private static final String propertiesFilename = "xmlvalidationresources.mysql.properties";
    private static Long hjid;
    private static Long phenotype_hjid;

    @BeforeClass
    public static void setup() {
        //String derbySystemHome = ImitsReporterTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        //System.setProperty("derby.system.home", derbySystemHome.substring(0, derbySystemHome.lastIndexOf(File.separator)));
        
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
            String hibernateConnectionUrl = (String) properties.get("hibernate.connection.url");
            properties.put("hibernate.connection.url", hibernateConnectionUrl.substring(0, hibernateConnectionUrl.lastIndexOf(";")));
        }

        try {
            hibernateManager = new HibernateManager(properties, PERSISTENCEUNITNAME);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        phenotype_hjid = hibernateManager.count("select max(hjid) from org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempts", null).longValue();
        hjid = hibernateManager.count("select max(hjid) from org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MicroinjectionAttempts", null).longValue();
    }

     

    @Test
    public void runHjid() {

        ImitsReporter imitsReporter = null;
        try {
            imitsReporter = new ImitsReporter(hibernateManager);
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        try {
            imitsReporter.run(hjid, phenotype_hjid);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @AfterClass
    public static void tearDown() {
        ImitsReporterTest.hibernateManager.close();
    }
}
