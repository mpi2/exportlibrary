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

import com.google.common.collect.ImmutableMap;
import java.io.FileNotFoundException;
import java.util.Properties;
import javax.xml.bind.JAXBException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.SubmissionSet;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.DOCUMENTS;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class SubmissionSetIncarnator extends Connector {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(SubmissionSetIncarnator.class);
    private SubmissionSet submissionSet;

    public SubmissionSet getSubmissionSet() {
        if (this.submissionSet == null) {
            this.submissionSet = new SubmissionSet();
        }
        return this.submissionSet;
    }
    private IOParameters.DOCUMENTS doc = null;

    public SubmissionSetIncarnator(String contextPath, String xmlFilename) throws Exception {
        super(contextPath, xmlFilename);
    }

    public SubmissionSetIncarnator(HibernateManager hibernateManager) {
        super(hibernateManager);
    }

    public SubmissionSetIncarnator(String persistenceUnitName, Properties properties) throws HibernateException {
        super(persistenceUnitName, properties);
    }


    public void loadSubmissionFromTrackerID(Long submissionTrackerID) {
        String query = "from Submission s where s.trackerID  = :trackerID";
        this.getSubmissionSet().getSubmission().addAll(this.hibernateManager.query(query,
                ImmutableMap.<String, Object>builder().put("trackerID", submissionTrackerID).build(),
                Submission.class));
    }

    public void loadSubmission(Long hjid) {
        this.getSubmissionSet().getSubmission().add(this.hibernateManager.load(Submission.class, hjid));

    }

    public void setSubmissionType(Submission submission) {
        if (submission.getCentreProcedure() != null && submission.getCentreProcedure().size() > 0) {
            this.doc = IOParameters.DOCUMENTS.CENTREPROCEDURESET;
        }
        if (submission.getCentreSpecimen() != null && submission.getCentreSpecimen().size() > 0) {
            this.doc = IOParameters.DOCUMENTS.CENTRESPECIMENSET;
        }
    }

    public CentreProcedureSet getCentreProcedureSet() {
        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        for (Submission submission : this.getSubmissionSet().getSubmission()) {
            centreProcedureSet.getCentre().addAll(submission.getCentreProcedure());
        }
        return centreProcedureSet;
    }

    public CentreSpecimenSet getCentreSpecimenSet() {
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        for (Submission submission : this.getSubmissionSet().getSubmission()) {
            centreSpecimenSet.getCentre().addAll(submission.getCentreSpecimen());
        }
        return centreSpecimenSet;
    }

    public DOCUMENTS getDoc() {
        if(doc == null)
            this.setSubmissionType(this.getSubmissionSet().getSubmission().get(0));
        return doc;
    }

    public void loadFromSubmissionSet() throws JAXBException, FileNotFoundException, Exception {
        if (this.toXml) {//assumes there is a single submission within a submissionset
            logger.info("loading submissionset.class from {}", this.xmlFilename);
            this.submissionSet = XMLUtils.unmarshal(this.contextPath, SubmissionSet.class, this.xmlFilename);

        } else {//assumes there is a single submission within a submissionset
            logger.info("loading submissionset running HB query from {} single ", SubmissionSet.class.getName());
            this.submissionSet = this.hibernateManager.query("from " + SubmissionSet.class.getName(), SubmissionSet.class).get(0);
        }
    }
}
