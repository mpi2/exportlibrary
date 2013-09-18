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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.testSupport;

import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Gender;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Zygosity;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Mouse;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;

import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class SpecimenDatabaseGenerator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SpecimenDatabaseGenerator.class);
    private static HibernateManager hibernateManager = null;

    public static Submission getSubmission() {
        Submission submission = new Submission();
        submission.setSubmissionDate(DatatypeConverter.now());
        submission.setTrackerID(1);
        submission.getCentreSpecimen().addAll(getCentreSpecimenSet().getCentre());
        return submission;
    }

    public static CentreSpecimenSet getCentreSpecimenSet() {
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        CentreSpecimen centreSpecimen = new CentreSpecimen();
        centreSpecimen.setCentreID(CentreILARcode.J);
        centreSpecimenSet.getCentre().add(centreSpecimen);

        Mouse baseline = new Mouse();
        centreSpecimen.getMouseOrEmbryo().add(baseline);

        baseline.setStrainID("C57BL/6N");
        baseline.setDOB(DatatypeConverter.now());
        baseline.setGender(Gender.MALE);
        baseline.setIsBaseline(true);
        baseline.setLitterId("litterID");
        baseline.setPhenotypingCentre(CentreILARcode.J);
        baseline.setPipeline("IMPC_001");
        baseline.setProductionCentre(CentreILARcode.WTSI);
        baseline.setProject("BaSH");
        baseline.setSpecimenID("specimenID");
        baseline.setZygosity(Zygosity.WILD_TYPE);

        Mouse mouse = new Mouse();
        centreSpecimen.getMouseOrEmbryo().add(mouse);
        mouse.setColonyID("JR18557");
        mouse.setDOB(DatatypeConverter.now());
        mouse.setGender(Gender.MALE);
        mouse.setIsBaseline(false);
        mouse.setLitterId("litterID");
        mouse.setPhenotypingCentre(CentreILARcode.J);
        mouse.setPipeline("IMPC");
        mouse.setProductionCentre(CentreILARcode.J);
        mouse.setProject("BaSH");
        mouse.setSpecimenID("specimenID2");
        mouse.setZygosity(Zygosity.HETEROZYGOUS);
        return centreSpecimenSet;
    }

    private static void startHibernateManager() {
        Properties properties = null;
        Reader reader = null;
        try {
            reader = new Reader(System.getProperty("test.databases.conf") + "/xmlvalidationresources.derby.properties");
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        properties = reader.getProperties();
        try {
            hibernateManager = new HibernateManager(properties, "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation");
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @BeforeClass
    public static void setup() {
        startHibernateManager();
    }

    @AfterClass
    public static void tearDown() {
        if (hibernateManager != null) {
            hibernateManager.close();
        }
    }

    @Test
    public void generateDatabase() {
        hibernateManager.persist(getSubmission());
    }
}
