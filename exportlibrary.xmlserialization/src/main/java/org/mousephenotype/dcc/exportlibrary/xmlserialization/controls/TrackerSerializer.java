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
package org.mousephenotype.dcc.exportlibrary.xmlserialization.controls;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.xml.bind.JAXBException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.SubmissionSet;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.consoleapps.XMLSerializer;

import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 *
 * TrackerSerializer constructor initialize all the resources needed to connect
 * to the database, unless a persistenceManager is passed as a parameter.
 *
 * So it is required to do a close or a fullClose of the trackerSerializer once
 * the app using trackerSerializer finishes sending submissions to the database.
 *
 *
 * TrackerSerializer does not perform any xsd validation on the data
 *
 *
 *
 */
public class TrackerSerializer {

    private static final Logger logger = LoggerFactory.getLogger(TrackerSerializer.class);
    //
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    //
    private HibernateManager hibernateManager;
    //
    private SubmissionSet submissionset;
    private Submission submission;

    public TrackerSerializer(String persistenceUnitname, Properties serializeProperties) throws PersistenceException {
        this.hibernateManager = new HibernateManager(serializeProperties, persistenceUnitname);
    }

    public TrackerSerializer(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    public void close() throws IllegalArgumentException {
        this.hibernateManager.close();
    }

    public void fullClose() throws IllegalStateException {
        this.hibernateManager.close();
    }

    /*
     * the following to functions (1) create a submission set, (2) load the
     * procedures or the specimens (3) attach the latter to the submissionset
     * (4) serialize the submission
     */
    public Long serializeProcedureSubmissionSet(long trackerID, Calendar submissionDate, String submissionFilename) throws JAXBException, FileNotFoundException, IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, XMLloadingException, IOException {
        this.submissionset = new SubmissionSet();
        this.submission = new Submission();
        this.submissionset.getSubmission().add(this.submission);
        this.submission.setTrackerID(trackerID);
        this.submission.setSubmissionDate(submissionDate);
        this.loadProcedureResults(submissionFilename);
        return this.serializeSubmissionSet();
    }

    public Long serializeSpecimenSubmissionSet(long trackerID, Calendar submissionDate, String submissionFilename) throws JAXBException, FileNotFoundException, IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, XMLloadingException, IOException {
        this.submissionset = new SubmissionSet();
        this.submission = new Submission();
        this.submissionset.getSubmission().add(this.submission);
        this.submission.setTrackerID(trackerID);
        this.submission.setSubmissionDate(submissionDate);
        this.loadSpecimens(submissionFilename);
        return this.serializeSubmissionSet();
    }

    private Long serializeSubmissionSet() throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException {
        logger.info("serializing submission");
        this.hibernateManager.persist(this.submissionset);
        logger.info("submission(trackerID, HJID):=({},{}) serialized successfully", this.submission.getTrackerID(), this.submission.getHjid());
        return this.submission.getHjid();
    }

    private void loadSpecimens(String filename) throws JAXBException, FileNotFoundException, XMLloadingException, IOException {
        logger.info("loading specimens file {}", filename);
        this.submission.setCentreSpecimen(XMLUtils.unmarshal(TrackerSerializer.CONTEXT_PATH, CentreSpecimenSet.class, filename).getCentre());
        if (this.submission.getCentreSpecimen().size() > 0) {
             if (XMLSerializer.maxEntries > 0) {
                int count = 0;
                for (CentreSpecimen c : this.submission.getCentreSpecimen()) {
                    count += c.getMouseOrEmbryo().size();
                }
                if (count > XMLSerializer.maxEntries) {
                    throw new XMLloadingException(filename + " contains more than " + XMLSerializer.maxEntries + " entries.", XMLSerializer.XML_ERROR_MAX_ENTRIES);
                }
            }
            logger.info("{} loaded successfully", filename);
        } else {
            throw new XMLloadingException(filename + " is probably not a specimens file.");
        }
    }

    private void loadProcedureResults(String filename) throws JAXBException, FileNotFoundException, XMLloadingException, IOException {
        logger.info("loading procedures file {}", filename);
        this.submission.setCentreProcedure(XMLUtils.unmarshal(TrackerSerializer.CONTEXT_PATH, CentreProcedureSet.class, filename).getCentre());
        if (this.submission.getCentreProcedure().size() > 0) {
            if (XMLSerializer.maxEntries > 0) {
                int count = 0;
                for (CentreProcedure c : this.submission.getCentreProcedure()) {
                    count += c.getExperiment().size();
                    count += c.getLine().size();
                }
                if (count > XMLSerializer.maxEntries) {
                    throw new XMLloadingException(filename + " contains more than " + XMLSerializer.maxEntries + " entries.", XMLSerializer.XML_ERROR_MAX_ENTRIES);
                }
            }
            logger.info("{} loaded successfully", filename);
        } else {
            throw new XMLloadingException(filename + " is probably not a procedure file.");
        }
    }
}
