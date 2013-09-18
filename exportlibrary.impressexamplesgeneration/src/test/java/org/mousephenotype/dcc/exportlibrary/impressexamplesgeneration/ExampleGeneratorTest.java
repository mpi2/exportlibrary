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
package org.mousephenotype.dcc.exportlibrary.impressexamplesgeneration;

import java.util.Properties;
import java.util.Random;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ExampleGeneratorTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExampleGeneratorTest.class);
    private static HibernateManager impressHibernateManager;
    private static Reader reader;
    private static final String persistenceUnitname = "org.mousephenotype.dcc.exportlibrary.xmlvalidation.external";
    private static ExampleGenerator exampleGenerator;
    private static final CentreILARcode centreILARcode = CentreILARcode.H;
    private static final String pipeline = "IMPC_001";
    private static final String experimentID = "my_first_experiment";
    private static final String specimenID = "my_first_specimen";
    private static final String procedureID = "IMPC_BWT_001";

    @BeforeClass
    public static void setup() {

        System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");





        Properties properties = null;


        try {
            reader = new Reader("xmlvalidationresources.derby.properties");
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        properties = reader.getProperties();
        Assert.assertNotNull(properties);


        properties.remove("hibernate.hbm2ddl.auto");


        impressHibernateManager = new HibernateManager(properties, persistenceUnitname);

        exampleGenerator = new ExampleGenerator(impressHibernateManager, centreILARcode,
                ExampleGenerator.projects[new Random().nextInt(ExampleGenerator.projects.length)],
                pipeline, experimentID, specimenID);
    }

    @Test
    public void testRandom() {
        float maximum = 1.0f;
        float minimum = 0.0f;
        String temp = "";
        for (int i = 0; i < 10; i++) {
            temp = ExampleGenerator.getRandomFloatValue(maximum, minimum);
            logger.info("{}", temp);
            logger.info("{}", ExampleGenerator.getRandomIntValue(6, 1));
        }
    }
    /*
     @Test
     public void generateProcedureTest() {
     exampleGenerator.generateProcedure(procedureID);
     try {
     exampleGenerator.serialize("data/" + procedureID + ".xml");
     } catch (JAXBException ex) {
     logger.error("", ex);
     Assert.fail();
     }
     }*/

    @Test
    public void generateExamplesTest() {
        try {
            exampleGenerator.generateExamples(centreILARcode, experimentID, specimenID);
        } catch (JAXBException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @AfterClass
    public static void teardown() {
        impressHibernateManager.close();
    }
}
