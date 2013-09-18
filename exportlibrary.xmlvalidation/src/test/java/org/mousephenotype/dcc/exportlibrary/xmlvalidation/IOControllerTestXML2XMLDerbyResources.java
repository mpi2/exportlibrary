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

import java.io.FileNotFoundException;
import java.util.Map;
import javax.xml.bind.JAXBException;

import org.hibernate.HibernateException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.CONNECTOR_NAMES;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressPipelineContainer;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class IOControllerTestXML2XMLDerbyResources {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IOControllerTestXML2XMLDerbyResources.class);
    private static IOController ioController;

    @BeforeClass
    public static void setup() {


        IOControllerTestXML2XMLDerbyResources.ioController = new IOController();
        Map<CONNECTOR_NAMES, IOParameters> ioparametersMap = XML2XMLDerbyParametersGenerator.getXML2XMLDerbyParameters();

        try {
            IOControllerTestXML2XMLDerbyResources.ioController.setup(ioparametersMap);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testConnectorsInstaantiated() {
        Assert.assertNotNull(IOControllerTestXML2XMLDerbyResources.ioController.getCentreProcedureSetIncarnator());
        Assert.assertNotNull(IOControllerTestXML2XMLDerbyResources.ioController.getImpressBrowser());
        Assert.assertNotNull(IOControllerTestXML2XMLDerbyResources.ioController.getValidationReportSetSerializer());
        Assert.assertNotNull(IOControllerTestXML2XMLDerbyResources.ioController.getValidationSetSerializer());
        Assert.assertEquals(1, IOControllerTestXML2XMLDerbyResources.ioController.getHibernateManagers().size());
    }

    @Test
    public void testImpressBrowserWorking() {

        ImpressPipelineContainer impressPipelineContainer = null;
        try {
            impressPipelineContainer = IOControllerTestXML2XMLDerbyResources.ioController.getImpressBrowser().load(ImpressPipelineContainer.class);
        } catch (JAXBException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }

        Assert.assertNotNull(impressPipelineContainer);

    }

    @AfterClass
    public static void teardown() {
        IOControllerTestXML2XMLDerbyResources.ioController.closeHiberanateManagers();
    }
}
