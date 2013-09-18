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
package org.mousephenotype.dcc.exportlibrary.traverser;


import org.junit.Assert;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.slf4j.LoggerFactory;

public class OtherTests {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(OtherTests.class);

    @Test
    public void testCentreIlarCode() {
        for (CentreILARcode value : CentreILARcode.values()) {
            logger.info("{} value", value.value());
             Assert.assertEquals(value,CentreILARcode.fromValue(value.value()));
        }
        
        try{
            CentreILARcode valueOf = CentreILARcode.valueOf("GMC");
        }catch(Exception ex){
            logger.error("value of for enum names does not work",ex);
        }
        try{
            CentreILARcode valueOf = CentreILARcode.valueOf("Gmc");
        }catch(Exception ex){
            logger.error("value of for enum value does not work",ex);
        }
        
        try{
            CentreILARcode valueOf = CentreILARcode.fromValue("GMC");
        }catch(Exception ex){
            logger.error("value of for enum names does not work",ex);
        }
        try{
            CentreILARcode valueOf = CentreILARcode.fromValue("Gmc");
        }catch(Exception ex){
            logger.error("value of for enum value does not work",ex);
        }
        
    }
}
