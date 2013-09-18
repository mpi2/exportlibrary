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
package org.mousephenotype.dcc.exportlibrary.fullTraverser;

import java.io.IOException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ResourceVersion;
import org.mousephenotype.dcc.exportlibrary.traverser.Orchestrator;
import org.mousephenotype.dcc.exportlibrary.traverser.SpecimenOrchestrator;
import org.mousephenotype.dcc.exportlibrary.traverser.SubmissionSetLoader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Traverser {

    private static final Logger logger = LoggerFactory.getLogger(Traverser.class);
    private final HibernateManager hibernateManager;
    private Orchestrator orchestrator;
    private SpecimenOrchestrator specimenOrchestrator;
    private SubmissionSetLoader submissionSetLoader;

    public Traverser(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
        this.submissionSetLoader = new SubmissionSetLoader(hibernateManager);
        this.orchestrator = new Orchestrator(hibernateManager, submissionSetLoader);
        this.specimenOrchestrator = new SpecimenOrchestrator(hibernateManager, submissionSetLoader);
    }

    public void run(Long trackerID, ResourceVersion resourceVersion) {
        boolean isSpecimen = false;
        try {
            isSpecimen = specimenOrchestrator.run(trackerID, resourceVersion);
        } catch (HibernateException | IOException ex) {
            logger.error("", ex);
        }
        if (!isSpecimen) {
            try {
                orchestrator.run(trackerID, resourceVersion);
            } catch (HibernateException | IOException ex) {
                logger.error("", ex);
            }
        }
    }
}
