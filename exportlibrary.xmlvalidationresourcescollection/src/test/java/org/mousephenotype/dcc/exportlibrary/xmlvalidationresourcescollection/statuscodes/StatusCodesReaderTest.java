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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.statuscodes;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.converters.DatatypeConverter;
import org.slf4j.LoggerFactory;

 
public class StatusCodesReaderTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StatusCodesReaderTest.class);
    private static final  String STATUS_CODES_CSV_FILENAME = "statusCodes.csv";
    
    @Test
    public void testRead(){
    StatusCodesReader statusCodesReader = new StatusCodesReader(STATUS_CODES_CSV_FILENAME);
        try {
            statusCodesReader.read(DatatypeConverter.now());
        } catch (ConfigurationException ex) {
            logger.error("",ex);
            Assert.fail();
        }
        Assert.assertTrue(statusCodesReader.getStatuscodes().getStatusCode().size()>1);
    }
}
