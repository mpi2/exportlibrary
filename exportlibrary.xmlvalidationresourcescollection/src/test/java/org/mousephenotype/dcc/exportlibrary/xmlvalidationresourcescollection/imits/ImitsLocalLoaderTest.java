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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.converters.DatatypeConverter;

import org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.mgi.MGIResource;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ImitsLocalLoaderTest {

    //private static final String jsonDocsRootFolder = "/home/julian/executions/xmlvalidationresourcescollection/20130222/data/json";
    //private static final String jsonDocsRootFolder = "/home/julian/executions/curl_imits/harwell_attempts";
    private static final String jsonDocsRootFolder = "/home/julian/executions/curl_imits/all_jsons";
    private static final String hibernatePropertiesConnectionFilename = jsonDocsRootFolder + "/imits.derby.properties";
    //private static final String hibernatePropertiesConnectionFilename = "/home/julian/executions/xmlvalidationresourcescollection/20130222/xmlvalidationresources.prince.mysql.properties";
    //
    private static final String PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external";
    private static HibernateManager hibernateManager;
    private static Reader reader;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImitsLocalLoaderTest.class);

    @BeforeClass
    public static void setup() {
        String derbySystemHome = ImitsReporterTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.setProperty("derby.system.home", derbySystemHome.substring(0, derbySystemHome.lastIndexOf(File.separator)));

         
        Properties properties = null;
        try {
            reader = new Reader(hibernatePropertiesConnectionFilename);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            org.junit.Assert.fail();
        }
        properties = reader.getProperties();
        org.junit.Assert.assertNotNull(properties);

        try {
            hibernateManager = new HibernateManager(properties, PERSISTENCEUNITNAME);
        } catch (HibernateException ex) {
            logger.error("", ex);
            org.junit.Assert.fail();
        }
        org.junit.Assert.assertNotNull(properties);

        try {
            ImitsLocalLoaderTest.hibernateManager = new HibernateManager(properties, PERSISTENCEUNITNAME);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testRun() {
        ImitsLocalLoader imitsLocalLoader = null;
        try {
            imitsLocalLoader = new ImitsLocalLoader(jsonDocsRootFolder);
        } catch (NoSuchAlgorithmException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (KeyManagementException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        imitsLocalLoader.setMicroInjectionWildCard("*ma.json");
        imitsLocalLoader.setMiplanWildCard("*miplans.json");
        imitsLocalLoader.setPhenotypeAttemptWildCard("*_pa_attempts.json");
        imitsLocalLoader.run(hibernateManager);


    }

    @Ignore
    @Test
    public void testMGI() {
        MGIResource mGIResource = new MGIResource(hibernateManager);
        try {
            mGIResource.run(DatatypeConverter.now());
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        hibernateManager.persist(mGIResource.getStrains());


    }

    @AfterClass
    public static void tearDown() {
        ImitsLocalLoaderTest.hibernateManager.close();
    }
}
