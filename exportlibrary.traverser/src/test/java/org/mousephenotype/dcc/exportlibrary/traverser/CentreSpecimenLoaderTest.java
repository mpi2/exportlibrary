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

import java.util.Calendar;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;

import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class CentreSpecimenLoaderTest {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(CentreSpecimenLoaderTest.class);
    private static Reader reader;
    private static HibernateManager hibernateManager;
    private static CentreSpecimenSetLoader centreSpecimenSetLoader;
    private static String HIBERNATE_PROPERTIES_FILENAME = "dbconf/procedures.derby.properties";
    private static String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";

    @BeforeClass
    public static void setup() {
        try {
            reader = new Reader(HIBERNATE_PROPERTIES_FILENAME);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(reader);
        try {
            CentreSpecimenLoaderTest.hibernateManager = new HibernateManager(reader.getProperties(), PERSISTENCE_UNITNAME);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }

        CentreSpecimenLoaderTest.centreSpecimenSetLoader = new CentreSpecimenSetLoader(hibernateManager);

    }
    
   
    
    @Ignore
    @Test
    public void testLoadCentreSpecimenSet() {
        Calendar submissionDate = DatatypeConverter.parseDate("2013-05-24");
        CentreSpecimenSet load = null;
        try {
            load = centreSpecimenSetLoader.load(CentreILARcode.H, submissionDate);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(load);
       logger.info("{} centreSpecimen found, {} specimens", load.getCentre().size(), load.getCentre().get(0).getMouseOrEmbryo().size());
    }

    @AfterClass
    public static void teardown() {
        try {
            CentreSpecimenLoaderTest.hibernateManager.close();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }
}
