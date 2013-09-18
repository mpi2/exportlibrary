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

import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.SubmissionSet;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class SubmissionSetLoader {

    private String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private final HibernateManager hibernateManager;
    private Reader reader;
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(SubmissionSetLoader.class);
    private SubmissionSet submissionSet;
    private boolean loaded = false;

    // assumed submission is either specimen or procedure
    public boolean isSpecimen() {
        if (this.submissionSet != null && this.submissionSet.isSetSubmission() && this.submissionSet.getSubmission().get(0).isSetCentreSpecimen()) {
            return true;
        }
        return false;
    }

    public boolean isProcedure() {
        if (this.submissionSet != null && this.submissionSet.isSetSubmission() && this.submissionSet.getSubmission().get(0).isSetCentreProcedure()) {
            return true;
        }
        return false;
    }

    public CentreProcedureSet getCentreProcedureSet() {

        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        for (Submission submission : this.submissionSet.getSubmission()) {
            if (submission.isSetCentreProcedure()) {
                centreProcedureSet.getCentre().addAll(submission.getCentreProcedure());
            }
        }
        return centreProcedureSet;
    }

    public CentreSpecimenSet getCentreSpecimenSet() {
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        for (Submission submission : this.submissionSet.getSubmission()) {
            if (submission.isSetCentreSpecimen()) {
                centreSpecimenSet.getCentre().addAll(submission.getCentreSpecimen());
            }
        }
        return centreSpecimenSet;
    }

    public SubmissionSet getSubmissionSet() {
        if (this.submissionSet == null) {
            this.submissionSet = new SubmissionSet();
        }
        return this.submissionSet;
    }

    public SubmissionSetLoader(String hibernateProperties) throws ConfigurationException, HibernateException {
        this.reader = new Reader(hibernateProperties);
        this.hibernateManager = new HibernateManager(this.reader.getProperties(), PERSISTENCE_UNITNAME);
    }

    public SubmissionSetLoader(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    public void load(Long submissionTrackerID) {
        if (!loaded) {
            String query = "from Submission s where s.trackerID  = :trackerID";
            List<Submission> submissions = this.hibernateManager.query(query,
                    ImmutableMap.<String, Object>builder().put("trackerID", submissionTrackerID).build(),
                    Submission.class);
            if (submissions.size() > 1) {
                logger.error("{} submissions for trackerID {}. Should be a 1 to 1 relation", submissions.size(), submissionTrackerID);
            }
            this.getSubmissionSet().getSubmission().addAll(submissions);
            loaded = true;
        }
    }
}
