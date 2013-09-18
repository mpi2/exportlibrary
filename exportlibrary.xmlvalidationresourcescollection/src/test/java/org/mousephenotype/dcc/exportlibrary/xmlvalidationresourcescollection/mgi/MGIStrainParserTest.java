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
import java.util.GregorianCalendar;
import org.junit.Assert;

import org.junit.Test;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 * 
 * This test makes use of a copy of an mgi file from src/test/resources to parse it
 */
public class MGIStrainParserTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MGIStrainParserTest.class);
    private static final String filename = "src/test/resources/MGI_Strain.rpt";
    @Test
    public void testRun(){
        MGIStrainParser mGIStrainParser = null;
        try {
            mGIStrainParser = new MGIStrainParser(filename, new GregorianCalendar());
        } catch (FileNotFoundException ex) {
            logger.error("file {} not found",ex,filename);
            Assert.fail();
        }
        Assert.assertNotNull(mGIStrainParser);
        
        mGIStrainParser.run();
        Assert.assertNotNull(mGIStrainParser.getMGIStrains());
    }
}
