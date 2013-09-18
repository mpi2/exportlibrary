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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.consoleapps;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class ConfigurationTest {
private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigurationTest.class);
    @Test
    public void foo() {
        PropertiesConfiguration propertiesConfiguration = null;
        try {
            propertiesConfiguration = new PropertiesConfiguration("test.properties");
        } catch (ConfigurationException ex) {
            logger.error("",ex);
            Assert.fail();
        }
        Assert.assertNotNull(propertiesConfiguration);
        logger.info(propertiesConfiguration.toString());
        int int_ = propertiesConfiguration.getInt("int");
        Assert.assertEquals(1, int_);
    }
}
