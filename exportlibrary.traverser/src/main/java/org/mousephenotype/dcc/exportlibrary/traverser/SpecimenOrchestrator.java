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
import java.util.Calendar;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ResourceVersion;
import org.mousephenotype.dcc.exportlibrary.datastructure.traverser.CentreSpecimenSetTraverser;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class SpecimenOrchestrator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SpecimenOrchestrator.class);
    private final HibernateManager hibernateManager;
    private final CentreSpecimenSetLoader centreSpecimenSetLoader;
    private final SubmissionSetLoader submissionSetLoader;
    private final CentreSpecimenSetTraverser centreSpecimenSetTraverser;

    public SpecimenOrchestrator(HibernateManager hibernateManager, SubmissionSetLoader submissionSetLoader) {
        this.hibernateManager = hibernateManager;
        this.centreSpecimenSetLoader = new CentreSpecimenSetLoader(hibernateManager);
        this.centreSpecimenSetTraverser = new CentreSpecimenSetTraverser();
        this.submissionSetLoader = submissionSetLoader;
        

    }

    public static void run(HibernateManager hibernateManager, SubmissionSetLoader submissionSetLoader)  throws HibernateException, IOException {
        ResourceVersion resourceVersion = new ResourceVersion();
        for (CentreILARcode centreILARcode : CentreILARcode.values()) {
            SpecimenOrchestrator orchestrator = new SpecimenOrchestrator(hibernateManager,submissionSetLoader);
            orchestrator.run(centreILARcode, resourceVersion);
        }
    }

    public static void run(HibernateManager hibernateManager,SubmissionSetLoader submissionSetLoader, Calendar submissionDate) throws HibernateException, IOException {
        ResourceVersion resourceVersion = new ResourceVersion();
        for (CentreILARcode centreILARcode : CentreILARcode.values()) {
            SpecimenOrchestrator orchestrator = new SpecimenOrchestrator(hibernateManager,submissionSetLoader);
            orchestrator.run(centreILARcode, resourceVersion, submissionDate);
        }
    }

    public boolean run(Long trackerID, ResourceVersion resourceVersion) throws HibernateException, IOException {
        this.submissionSetLoader.load(trackerID);
        CentreSpecimenSet centreSpecimenSet = this.submissionSetLoader.getCentreSpecimenSet();
        if (centreSpecimenSet.isSetCentre()) {
            logger.info("running traverser for {} centrespecimens", centreSpecimenSet.getCentre().size());
            for(CentreSpecimen centreSpecimen : centreSpecimenSet.getCentre()){
                logger.info("{} specimens for centre {}", centreSpecimen.getMouseOrEmbryo().size(), centreSpecimen.getCentreID());
            }
            this.orchestrate(centreSpecimenSet, resourceVersion);
            return true;
        }
        return false;
    }

    public void run(CentreILARcode centreILARcode, ResourceVersion resourceVersion, Calendar submissionDate) throws HibernateException, IOException {
        CentreSpecimenSet centreSpecimenSet = this.centreSpecimenSetLoader.load(centreILARcode, submissionDate);
        if (centreSpecimenSet.isSetCentre()) {
            logger.info("{} centreSpecimens loaded", centreSpecimenSet.getCentre().size());
            this.orchestrate(centreSpecimenSet, resourceVersion);
        }
    }

    public void run(CentreILARcode centreILARcode, ResourceVersion resourceVersion) throws HibernateException, IOException {
        CentreSpecimenSet centreSpecimenSet = this.centreSpecimenSetLoader.load(centreILARcode);
        if (centreSpecimenSet.isSetCentre()) {
            logger.info("{} centreSpecimens loaded", centreSpecimenSet.getCentre().size());
            this.orchestrate(centreSpecimenSet, resourceVersion);
        }
    }

    private void orchestrate(CentreSpecimenSet centreSpecimenSet, ResourceVersion resourceVersion) throws HibernateException, IOException {
        SpecimenCommandImpl specimenCommandImpl = new SpecimenCommandImpl(hibernateManager);
        Calendar start =  DatatypeConverter.now();
        logger.info("traversing the structure. Started at {} ", DatatypeConverter.printDateTime(start));
        this.centreSpecimenSetTraverser.run(specimenCommandImpl, centreSpecimenSet);
        Calendar finish = DatatypeConverter.now();
        logger.info("traversing the structure. Finished at {}", DatatypeConverter.printDateTime(finish));
        logger.info("traverse lasted for {}", DatatypeConverter.getDuration(start, finish));

        SpecimenReporter specimenReporter = new SpecimenReporter(specimenCommandImpl, hibernateManager);
        logger.info("compiling results");
        specimenReporter.compileResults(resourceVersion,DatatypeConverter.getMilisDuration(start, finish));
        logger.info("updating history");
        specimenReporter.updateHistory();
    }
}
