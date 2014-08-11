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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress.utils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.HashMap;
import junit.framework.Assert;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class InstantiatorTest {

    protected static final Logger logger = LoggerFactory.getLogger(InstantiatorTest.class);

    /*
     * <xs:attribute name="is_deprecated" type="xs:boolean" use="required"/>
     * <xs:attribute name="pipeline_name" type="xs:string" use="required"/>
     * <xs:attribute name="minor_version" type="xs:integer" use="required"/>
     * <xs:attribute name="pipeline_id" type="xs:integer" use="required"/>
     * <xs:attribute name="description" type="xs:string" use="required"/>
     * <xs:attribute name="major_version" type="xs:integer" use="required"/>
     * <xs:attribute name="pipeline_key" type="xs:string" use="required"/>
     */
    public static HashMap<String, String> getImpressPipelineMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("is_deprecated", "false");
        map.put("pipeline_name", "pipeline_name");
        map.put("minor_version", "1");
        map.put("pipeline_id", "456");
        //map.put("description", "description");
        map.put("major_version", "2");
        map.put("pipeline_key", "pipeline_key");
        return map;
    }

    @Test
    public void testImpressPipeline() {
        ImpressPipeline impressPipeline = new ImpressPipeline();
        HashMap<String, String> map =InstantiatorTest.getImpressPipelineMap();
        try {
           Instantiator.getInstance(ImpressPipeline.class, impressPipeline, map);
        } catch (NoSuchFieldException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IllegalAccessException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (NoSuchMethodException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (InvocationTargetException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        
        ImpressPipeline impressPipeline2 = new ImpressPipeline();
        
        impressPipeline2.setIsDeprecated(false);
        impressPipeline2.setPipelineName("pipeline_name");
        impressPipeline2.setMinorVersion(BigInteger.valueOf(1L));
        impressPipeline2.setPipelineId(BigInteger.valueOf(456L));
        //impressPipeline2.setDescription("description");
        impressPipeline2.setMajorVersion(BigInteger.valueOf(2L));
        impressPipeline2.setPipelineKey("pipeline_key");
        
        Assert.assertEquals(impressPipeline2,impressPipeline);
        

                
    }
}
