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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.support;


import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class SpecimenWSclientTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SpecimenWSclientTest.class);
    private static SpecimenWSclient specimenWSclient;
    private static final String url = "http://mousephenotype.org/phenodcc-validation-ws/resources/mousesummary/byspecimenid";
    private static final String sandbox_url = "http://sandbox.mousephenotype.org/phenodcc-validation-ws/resources/mousesummary/byspecimenid";

    @BeforeClass
    public static void setup() {
        specimenWSclient = new SpecimenWSclient(url);
    }

    @Test
    public void exists() {
        boolean exists = false;

        
        try {
            exists = specimenWSclient.exists(CentreILARcode.H, "ererer");
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertFalse(exists);
        
    }
    @Ignore
    @Test
    public void test2(){
        specimenWSclient = new SpecimenWSclient(sandbox_url);
           boolean exists = false;
        try {
            exists = specimenWSclient.exists(CentreILARcode.TCP, "B6NC_13633_001501");
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertTrue(exists);
    }
}
