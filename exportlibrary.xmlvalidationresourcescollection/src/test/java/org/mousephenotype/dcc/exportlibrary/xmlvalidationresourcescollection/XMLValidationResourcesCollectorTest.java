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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import junitx.framework.Assert;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class XMLValidationResourcesCollectorTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLValidationResourcesCollectorTest.class);
    private static HibernateManager hibernateManager;
    private static Reader reader;
    private static final String PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.external";
    private static String propertiesFilename = "xmlvalidationresourcesIT.derby.properties";
    private static XMLValidationResourcesCollector xMLValidationResourcesCollector;

    @BeforeClass
    public static void setup() {
        Properties properties = null;

        try {
            reader = new Reader(propertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            org.junit.Assert.fail();
        }

        properties = reader.getProperties();

        org.junit.Assert.assertNotNull(properties);

        try {
            hibernateManager = new HibernateManager(properties, PERSISTENCEUNITNAME);
        } catch (Exception ex) {
            logger.error("", ex);
            org.junit.Assert.fail();
        }
        XMLValidationResourcesCollectorTest.xMLValidationResourcesCollector = new XMLValidationResourcesCollector(null);
        XMLValidationResourcesCollectorTest.xMLValidationResourcesCollector.setHibernateManager(hibernateManager);
    }

    @AfterClass
    public static void tearDown() {
        if (hibernateManager != null) {
            hibernateManager.close();
        }
    }

    @Test
    public void testRun() {
        try {
            XMLValidationResourcesCollectorTest.xMLValidationResourcesCollector.run();
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }
}
