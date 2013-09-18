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
package org.mousephenotype.dcc.exportlibrary.validationRemover;

import java.io.File;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class ValidationRemoverTest {
    
    static {
        System.setProperty("derby.system.home", System.getProperty("test.databases.folder"));
    }
    private static final String contextPath = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ValidationRemoverTest.class);
    private static final String EXTERNAL_RESOURCES_FOLDER = "test_databases";
    private static final String propertiesFilename = "hibernate.test.common.properties";
    //private static final String propertiesFilename = "pipeline.mysql.properties";
    private static Properties properties;
    private static HibernateManager hibernateManager;
    private static ValidationRemover validationRemover;
    
    @BeforeClass
    public static void setup() {
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
        
        validationRemover = new ValidationRemover(hibernateManager);
    }
    
    
    @Test
    public void run() {
        try {
            validationRemover.run(0);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        List<ValidationSet> query = hibernateManager.query("from ValidationSet ", ValidationSet.class);
        Assert.assertTrue(query.isEmpty());
    }
    @Ignore
    @Test
    public void testpipeline() {
        try {
            validationRemover.run(3);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        ValidationReportSet load = hibernateManager.load(ValidationReportSet.class, 173);
        Assert.assertNull(load);
    }
    
    @AfterClass
    public static void teardown() {
        try {
            hibernateManager.close();
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }
}
