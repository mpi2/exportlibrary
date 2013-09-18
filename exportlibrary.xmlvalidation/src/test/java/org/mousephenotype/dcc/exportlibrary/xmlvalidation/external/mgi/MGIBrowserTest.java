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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.mgi;

import java.io.FileNotFoundException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;
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
public class MGIBrowserTest {

    static {
        System.setProperty("derby.system.home", System.getProperty("test.databases.folder"));
    }
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MGIBrowserTest.class);
    private static final String PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.xmlvalidation.external";
    private static HibernateManager hibernateManager = null;
    private static MGIBrowser mgiBrowser;

    @BeforeClass
    public static void setUp() {
        Properties properties = null;


        Reader reader = null;
        try {
            reader = new Reader(System.getProperty("test.databases.conf") +"/xmlvalidationresources.derby.properties");
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        properties = reader.getProperties();

        Assert.assertNotNull(properties);

        try {
            hibernateManager = new HibernateManager(properties, PERSISTENCEUNITNAME);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }

        mgiBrowser = new MGIBrowser(hibernateManager);

    }

    @AfterClass
    public static void tearDown() {
        if (hibernateManager != null) {
            hibernateManager.close();
        }
    }

    @Test
    public void testExists() {
        try {
            Assert.assertTrue(mgiBrowser.exists("MGI:5448312"));
        } catch (JAXBException ex) {
              logger.error("", ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        //
        try {
            Assert.assertFalse(mgiBrowser.exists("ll"));
        } catch (JAXBException ex) {
              logger.error("", ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }
}
