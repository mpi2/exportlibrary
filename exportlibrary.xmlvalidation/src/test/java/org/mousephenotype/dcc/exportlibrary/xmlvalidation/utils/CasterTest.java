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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.utils;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress.Caster;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class CasterTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CasterTest.class);

    @Test
    public void testFloat() {

        String parameterValue = "2,643.85";
        String parameterType = "FLOAT";
        Object casted = null;
        try {
            casted = Caster.cast(parameterValue, parameterType);
        } catch (ParseException ex) {
            logger.error("parse error", ex);
            Assert.fail();
        }

        Assert.assertNotNull(casted);
        Assert.assertEquals(-1, -1);
        logger.error("value {}", casted);
        Assert.assertEquals(2, 643.85, ((Float) casted).floatValue());
        Assert.assertEquals(2643.85, ((Float) casted).floatValue(), 1);

        parameterValue = "8";
        casted = null;
        try {
            casted = Caster.cast(parameterValue, parameterType);
        } catch (ParseException ex) {
            logger.error("parse error", ex);
            Assert.fail();
        }
        
        Assert.assertEquals(8.0, ((Float) casted).floatValue(),1);
        Assert.assertEquals(8, ((Float) casted).floatValue(),1);
        
        
        parameterValue = "8.0";
        casted = null;
        try {
            casted = Caster.cast(parameterValue, parameterType);
        } catch (ParseException ex) {
            logger.error("parse error", ex);
            Assert.fail();
        }
        
        Assert.assertEquals(8.0, ((Float) casted).floatValue(),1);
        Assert.assertEquals(8, ((Float) casted).floatValue(),1);
        

    }

    @Test
    public void testInteger() {
        String parameterValue = "2,643";
        String parameterType = "INT";
        Object casted = null;
        try {
            casted = Caster.cast(parameterValue, parameterType);
        } catch (ParseException ex) {
            logger.error("parse error", ex);
            Assert.fail();
        }

        Assert.assertNotNull(casted);
        Assert.assertEquals(-1, -1);
        logger.error("value {}", casted);
        Assert.assertEquals(2, 643, ((Integer) casted).intValue());
        Assert.assertEquals(2643, ((Integer) casted).intValue());

    }
}
