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

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ResourceVersion;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class OrchestratorTest {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(OrchestratorTest.class);
    private static Reader reader;
    private static HibernateManager hibernateManager;
    private static String HIBERNATE_PROPERTIES_FILENAME = "dbconf/procedures.derby.properties";
    //GMC_102_21615_30253603_1_30253603
    //private static String HIBERNATE_PROPERTIES_FILENAME = "/home/julian/executions/traverser/procedures.mysql.properties";
    //private static String HIBERNATE_PROPERTIES_FILENAME = "/home/julian/executions/pipeline/procedures.mysql.properties";
    private static String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private static Orchestrator orchestrator;
    private static SubmissionSetLoader submissionSetLoader;
    private static ResourceVersion resourceVersion = new ResourceVersion();

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
            OrchestratorTest.hibernateManager = new HibernateManager(reader.getProperties(), PERSISTENCE_UNITNAME);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        OrchestratorTest.submissionSetLoader = new SubmissionSetLoader(hibernateManager);
        OrchestratorTest.orchestrator = new Orchestrator(hibernateManager,submissionSetLoader);
        resourceVersion.setImpressVersion("latest");
        resourceVersion.setMgiDownload(DatatypeConverter.now());
    }

    @Ignore
    @Test
    public void runForOneCentre() {
        
        
        try {
            OrchestratorTest.orchestrator.run(CentreILARcode.H,
                    "test_data/" + OrchestratorTest.orchestrator.getValidFilename(CentreILARcode.H),
                    "test_data/" + OrchestratorTest.orchestrator.getInvalidFilename(CentreILARcode.H), resourceVersion,true);
        } catch (HibernateException | IOException ex) {
            logger.error("", ex);
            Assert.fail();

        }
    }
    
    @Ignore
    @Test
    public void testGmcDuplicate(){
        try {
            OrchestratorTest.orchestrator.run(714L,resourceVersion);
        } catch (HibernateException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @AfterClass
    public static void teardown() {
        try {
            OrchestratorTest.hibernateManager.close();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }
}