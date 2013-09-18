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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.mgi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
 
import junitx.util.PrivateAccessor;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
 
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.mgi.MgiStrains;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 * 
 * This test downloads the mgi file, parses it, checks that there are mgistrains and closes the resourecs.
 */
public class MGIResourceTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MGIResourceTest.class);
    private static MGIResource mGIResource;
    private static Reader reader;

    private static final String connectionPropertiesFilename = "hsqldb.properties";


    @BeforeClass
    public static void init() {
        logger.info("==TEST== running init");
        Properties properties = null;
        
       
        try {
            reader = new Reader(connectionPropertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            org.junit.Assert.fail();
        }
        properties = reader.getProperties();
        org.junit.Assert.assertNotNull(properties);
        properties.put("hibernate.ejb.entitymanager_factory_name", "MGIStrains");
        properties.put("hibernate.connection.url", "jdbc:hsqldb:file:hsqldb/MGIStrains");

        mGIResource = new MGIResource(properties);
        try {
            mGIResource.init(true, false);
        } catch (FileNotFoundException ex) {
            logger.error("cannot find properties filename", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("cannot access ", ex);
            Assert.fail();
        }
    }

    @AfterClass
    public static void tearDown() {
        logger.info("==TEST== running teardown");
        try {
            mGIResource.close();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testGetAllStrains() {
        logger.info("==TEST== running testgetallstrains");
        MgiStrains strains = mGIResource.getStrains();
        Assert.assertNotNull(strains);
        Assert.assertTrue(strains.getMgiStrain().size() > 1);
        
        
    }

    @Test
    public void testExists() {
        logger.info("==TEST== running teststrainid exists");
        String strainID = "101";

        boolean findStrain = false;
        try {
            findStrain = mGIResource.findStrain(strainID);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertTrue(findStrain);

    }

    @Ignore
    @Test
    public void testFirstTime() {
        try {
            PrivateAccessor.invoke(mGIResource, "triggerDownload", new Class[]{}, new Object[]{});
        } catch (Throwable ex) {
            logger.error("error downloading the file", ex);
            Assert.fail();
        }
        try {
            PrivateAccessor.invoke(mGIResource, "loadPersistenceProperties", new Class[]{}, new Object[]{});
        } catch (Throwable ex) {
            logger.error("loading persistence properties", ex);
            Assert.fail();
        }
        try {
            PrivateAccessor.invoke(mGIResource, "loadCache", new Class[]{Boolean.class}, new Object[]{true});
        } catch (Throwable ex) {
            logger.error("error loading cache", ex);
            Assert.fail();
        }

        try {
            PrivateAccessor.invoke(mGIResource, "startResource", new Class[]{}, new Object[]{});
        } catch (Throwable ex) {
            logger.error("error loading cache", ex);
            Assert.fail();
        }
    }
}
