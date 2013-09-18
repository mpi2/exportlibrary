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
package org.mousephenotype.dcc.exportlibrary.validationRemover;

import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.traverser.CentreProcedureSetTraverser;
import org.mousephenotype.dcc.exportlibrary.datastructure.traverser.CentreSpecimenSetTraverser;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class ValidationRemover {
private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ValidationRemover.class);
    private final HibernateManager hibernateManager;
    private SubmissionLoader submissionLoader;

    public ValidationRemover(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;

    }

    public void run(long trackerID) {
        this.submissionLoader = new SubmissionLoader(hibernateManager);
        this.submissionLoader.load(trackerID);
        for (Submission submission : this.submissionLoader.getSubmissions()) {
            this.removeValidationSets(submission);
        }
    }

    public void run(long[] trackerIDs) {
        for (long trackerID : trackerIDs) {
            this.run(trackerID);
        }
    }

    private void removeValidationSets(Submission submission) {
        CommandImpl ci = new CommandImpl(hibernateManager);
        if (submission.isSetCentreProcedure()) {
            CentreProcedureSetTraverser centreProcedureSetTraverser = new CentreProcedureSetTraverser();
            CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
            centreProcedureSet.setCentre(submission.getCentreProcedure());
            centreProcedureSetTraverser.run(ci, centreProcedureSet);
            if(!ci.loadValidationSets()){
                logger.info("no validation results found for submission trackerID {}", submission.getTrackerID());
            }
        }
        if (submission.isSetCentreSpecimen()) {
            CentreSpecimenSetTraverser centreSpecimenSetTraverser = new CentreSpecimenSetTraverser();
            CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
            centreSpecimenSet.setCentre(submission.getCentreSpecimen());
            centreSpecimenSetTraverser.run(ci, centreSpecimenSet);
            if(!ci.loadValidationSets()){
                logger.info("no validation results found for submission trackerID {}", submission.getTrackerID());
            }
        }
        ci.loadValidationReportSets();
        ci.removeValidationReportSets();
        ci.removeValidationSets();
        
    }
}
