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

import java.io.FileNotFoundException;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.junit.Assert;

import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.CONNECTOR_NAMES;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.consoleapps.support.ValidatorSupport;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ValidatorTest {

    static {
        System.setProperty("derby.system.home", System.getProperty("test.databases.folder"));
    }
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(ValidatorTest.class);
    public static Validator validator = new Validator(null);

    private static Map<CONNECTOR_NAMES, IOParameters> getIOParametersforDB2DBplusDerby() {
        //"-t","1", "-f", "procedures.derby.properties","-h", "xmlvalidationresources.derby.properties"}));


        Map<CONNECTOR_NAMES, IOParameters> ioparameters = ValidatorSupport.buidExecutionIOParameters(
                null, //specimenResultsFilename
                null,//procedureResultsFilename
                null, //submissionID
                1L, //submissionTrackerID
                System.getProperty("test.databases.conf") + "/procedures.derby.properties", //submissionPersistenceFilename
                System.getProperty("test.databases.conf") + "/xmlvalidationresources.derby.properties", //externalResourcesLocationPersistenceFilename
                false);//externalResourcesLocationOnLocalDatabase

        return ioparameters;
    }

    @Test
    public void foo() {
    }

    @Test
    public void testvalidateDB2DBplusDerby() {
        try {
            ValidatorTest.validator.validate(getIOParametersforDB2DBplusDerby(), "conf/external.properties");
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
    }
}
