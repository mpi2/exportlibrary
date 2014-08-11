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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation;

import java.util.Map;

import junitx.util.PrivateAccessor;
import org.hibernate.HibernateException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.CONNECTOR_NAMES;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class XMLValidationControlTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLValidationControlTest.class);
    ;
    private static IOParameters.DOCUMENTS doc = null;
    private static XMLValidationControl xmlValidationControl;

    @BeforeClass
    public static void setup() {
        XMLValidationControlTest.xmlValidationControl = new XMLValidationControl();

    }

    @Test
    @Ignore
    public void testRunXML2XMLDerby() {
        Map<CONNECTOR_NAMES, IOParameters> ioparametersMap = XML2XMLDerbyParametersGenerator.getXML2XMLDerbyParameters();

        Assert.assertNotNull(ioparametersMap);
        try {
            doc = XMLValidationControlTest.xmlValidationControl.getIoController().setup(ioparametersMap);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(doc);

        IOParameters dataToValidate = null;
        try {
            dataToValidate = (IOParameters) PrivateAccessor.invoke(XMLValidationControlTest.xmlValidationControl, "getDataToValidate", new Class[]{Map.class}, new Object[]{ioparametersMap});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(dataToValidate);


        try {

            PrivateAccessor.invoke(XMLValidationControlTest.xmlValidationControl,
                    "loadDataToValidate",
                    new Class[]{IOParameters.DOCUMENTS.class,
                        IOParameters.DOCUMENT_SRCS.class, Long.class, Long.class, Long.class},
                    new Object[]{doc, dataToValidate.getDoc_srcs(),
                        dataToValidate.getHjid(), dataToValidate.getHjid(),
                        dataToValidate.getHjid()});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }


        try {

            PrivateAccessor.invoke(XMLValidationControlTest.xmlValidationControl,
                    "validate",
                    new Class[]{IOParameters.DOCUMENTS.class},
                    new Object[]{doc});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }

        try {

            PrivateAccessor.invoke(XMLValidationControlTest.xmlValidationControl,
                    "serializeResults",
                    new Class[]{IOParameters.DOCUMENTS.class},
                    new Object[]{doc});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }

        XMLValidationControlTest.xmlValidationControl.getIoController().closeHiberanateManagers();


    }

    @AfterClass
    public static void teardown() {
        
    }
}
