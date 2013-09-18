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
package org.mousephenotype.dcc.exportlibrary.xsdvalidation.controls;

import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author julian
 */
public class ProcedureResultsXMLValidityCheckerTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcedureResultsXMLValidityCheckerTest.class);

    @Test(expected = SAXParseException.class)
    public void testWrongAttribute() throws IOException, SAXException {
        String xmlFilename = "data/procedure_results_wrong_attribute.xml";
        logger.info("running testWrongAttribute against {}", xmlFilename);
        ProcedureResultsXMLValidityChecker checker = new ProcedureResultsXMLValidityChecker();
        checker.check(xmlFilename);
    }

    @Test(expected = SAXParseException.class)
    public void testWrongTag() throws IOException, SAXException {
        String xmlFilename = "data/procedure_results_wrong_tag.xml";
        logger.info("running testWrongTag against {}", xmlFilename);
        ProcedureResultsXMLValidityChecker checker = new ProcedureResultsXMLValidityChecker();
        checker.check(xmlFilename);
    }

    @Test
    public void testWrongWithHandler() {
        String xmlFilename = "data/procedure_results_wrong_tag.xml";
        logger.info("running testWrongWithHandler against {}", xmlFilename);
        ProcedureResultsXMLValidityChecker checker = null;
        try {
            checker = new ProcedureResultsXMLValidityChecker();
        } catch (SAXException ex) {
            logger.error("***************", ex);
            Assert.fail();
        }
        Assert.assertNotNull(checker);
        checker.attachErrorHandler();
        logger.info("after attaching error handler");
        try {
            checker.check(xmlFilename);
        } catch (org.xml.sax.SAXParseException ex) {
            logger.error("***********************", ex);
            Assert.fail();
        } catch (IOException ex) {
            logger.error("{} not found", xmlFilename, ex);
            Assert.fail();
        } catch (SAXException ex) {
            logger.error("{} should not raise errors if error handler attached", xmlFilename, ex);
            Assert.fail();
        }

        Assert.assertNotNull(checker.getHandler().getExceptionMessages());
        StringBuilder sb = new StringBuilder();
        for (SAXParseException exception : checker.getHandler().getExceptionMessages()) {
            sb.append(exception.getSystemId());
            sb.append(" [");
            sb.append(exception.getLineNumber());
            sb.append(":");
            sb.append(exception.getColumnNumber());
            sb.append("] -- ");
            sb.append(exception.getPublicId());
            sb.append(" -- ||");
            sb.append(exception.getMessage());
            sb.append("||");
            logger.error("Formatted output {}", sb.toString(), exception);
            sb = new StringBuilder();
        }
    }

    @Test
    public void testValidWithHandler() {
        String xmlFilename = "data/procedures.xml";
        logger.info("running testValidWithHandler against {}", xmlFilename);
        ProcedureResultsXMLValidityChecker checker = null;
        try {
            checker = new ProcedureResultsXMLValidityChecker();
        } catch (SAXException ex) {
            logger.error("", ex);
        }
        Assert.assertNotNull(checker);
        checker.attachErrorHandler();
        try {
            checker.check(xmlFilename);
        } catch (SAXParseException ex) {
            logger.error("error reading procedure xsd", ex);

        } catch (IOException ex) {
            logger.error("{} not found", xmlFilename, ex);
            Assert.fail();
        } catch (SAXException ex) {
            logger.error("{} should not raise errors if error handler attached", xmlFilename, ex);
            Assert.fail();
        }


        for (SAXParseException exception : checker.getHandler().getExceptionMessages()) {
            logger.error("SAXParseExceptions ", exception);
        }
        Assert.assertEquals(0, checker.getHandler().getExceptionMessages().size());


    }
}
