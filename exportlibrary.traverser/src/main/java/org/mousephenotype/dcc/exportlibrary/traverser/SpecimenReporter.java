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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ResourceVersion;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReport;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;
import org.mousephenotype.dcc.exportlibrary.traverser.aux.SpecimenComparator;
import org.mousephenotype.dcc.exportlibrary.traverser.aux.SubmissionCounter;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecimenReporter {

    private static final Logger logger = LoggerFactory.getLogger(SpecimenReporter.class);
    private final SpecimenCommandImpl specimenCommandImpl;
    private final HibernateManager hibernateManager;
    private final Table<Submission, Boolean, List<Validation>> submissions;

    public SpecimenReporter(SpecimenCommandImpl specimenCommandImpl, HibernateManager hibernateManager) {
        this.specimenCommandImpl = specimenCommandImpl;
        this.hibernateManager = hibernateManager;
        this.submissions = HashBasedTable.create();
    }

    private Submission getSubmission(CentreSpecimen centreSpecimen) {
        Submission container = this.hibernateManager.getContainer(centreSpecimen, Submission.class, "centreSpecimen");

        return container;
    }

    private void storeSubmission(Submission submission, boolean isValid, List<Validation> validations) {

        Boolean previouslyInValid = this.submissions.contains(submission, false);
        if (previouslyInValid && !isValid) {
            this.submissions.get(submission, false).addAll(validations);
        }
        Boolean previouslyValid = this.submissions.contains(submission, true);
        if (previouslyValid && !isValid) {
            this.submissions.remove(submission, true);
            this.submissions.put(submission, false, validations);
        }
        if (!previouslyInValid && !previouslyValid) {
            if (validations == null) {
                validations = new ArrayList<>();
            }
            this.submissions.put(submission, isValid, validations);
        }
    }

    public String getReportIdentifier(CentreILARcode centreILarCode, Specimen specimen) {
        StringBuilder sb = new StringBuilder();
        sb.append(centreILarCode);
        sb.append("_");
        sb.append(specimen.getSpecimenID());
        return sb.toString();
    }

    public String getReportIdentifier(Submission submission, CentreSpecimen centreSpecimen) {
        StringBuilder sb = new StringBuilder();
        sb.append(centreSpecimen.getCentreID().toString());
        sb.append("_");
        sb.append(submission.getTrackerID());
        sb.append("_");
        sb.append(centreSpecimen.getHjid());

        return sb.toString();
    }

    private void updateValidationReport(String reportIdentifier) {
        String resetQuery = "update  VALIDATIONREPORT\n"
                + "set VALIDATIONREPORT.SUPERSEEDED = true\n"
                + "where VALIDATIONREPORT.REPORTIDENTIFIER =:reportIdentifier";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportIdentifier", reportIdentifier);
        this.hibernateManager.nativeExecution(resetQuery, parameters);

        String updateQuery = "update  VALIDATIONREPORT, "
                + "(select max(hjid) as mxx from   VALIDATIONREPORT where  VALIDATIONREPORT.REPORTIDENTIFIER = :reportIdentifier) as inter\n"
                + "set VALIDATIONREPORT.SUPERSEEDED = false\n"
                + "where VALIDATIONREPORT.REPORTIDENTIFIER = :reportIdentifier\n"
                + "and VALIDATIONREPORT.HJID = inter.mxx";
        this.hibernateManager.nativeExecution(updateQuery, parameters);
    }

    public void updateHistory() {
        logger.info("updating superseeded for {} no valid specimens", this.specimenCommandImpl.getSpecimenValidations().size());
        String reportIdentifier = null;
        Specimen[] sortedSpecimens;
        for (final CentreSpecimen centreSpecimen : this.specimenCommandImpl.getSpecimenValidations().rowKeySet()) {
            sortedSpecimens = this.specimenCommandImpl.getSpecimenValidations().row(centreSpecimen).keySet().toArray(new Specimen[1]);
            Arrays.sort(sortedSpecimens, new SpecimenComparator());
            for (Specimen specimen : sortedSpecimens) {
                reportIdentifier = this.getReportIdentifier(centreSpecimen.getCentreID(), specimen);
                this.updateValidationReport(reportIdentifier);
            }
        }
        logger.info("updating superseeded for {} valid specimens", this.specimenCommandImpl.getValidSpecimens().size());
        for (CentreSpecimen centreSpecimen : this.specimenCommandImpl.getValidSpecimens().keySet()) {
            sortedSpecimens = this.specimenCommandImpl.getValidSpecimens().get(centreSpecimen).toArray(new Specimen[1]);
            Arrays.sort(sortedSpecimens, new SpecimenComparator());
            for (Specimen specimen : sortedSpecimens) {
                reportIdentifier = this.getReportIdentifier(centreSpecimen.getCentreID(), specimen);
                this.updateValidationReport(reportIdentifier);
            }
        }
    }

    public void compileResults(ResourceVersion resourceVersion, long duration) {
        ValidationReportSet validationReportSet = new ValidationReportSet();
        Submission submission = null;
        Submission mergedSubmission = null;
        ValidationReport validationReport = null;
        logger.info("generating results for {} no valid specimens", this.specimenCommandImpl.getSpecimenValidations().size());
        Specimen[] sortedSpecimens;
        for (final CentreSpecimen centreSpecimen : this.specimenCommandImpl.getSpecimenValidations().rowKeySet()) {
            sortedSpecimens = this.specimenCommandImpl.getSpecimenValidations().row(centreSpecimen).keySet().toArray(new Specimen[1]);
            Arrays.sort(sortedSpecimens, new SpecimenComparator());
            for (Specimen specimen : sortedSpecimens) {
                //    for (Entry<Specimen, List<Validation>> row : this.specimenCommandImpl.getSpecimenValidations().row(centreSpecimen).entrySet()) {
                validationReport = new ValidationReport();
                submission = this.getSubmission(centreSpecimen);
                mergedSubmission = this.hibernateManager.merge(submission);
                //this.storeSubmission(mergedSubmission, false, row.getValue());
                this.storeSubmission(mergedSubmission, false, this.specimenCommandImpl.getSpecimenValidations().get(centreSpecimen, specimen));
                validationReport.setSubmission(mergedSubmission);
                validationReport.setCentreSpecimen(centreSpecimen);
                //validationReport.setValidation(row.getValue());
                //validationReport.setValidation(this.specimenCommandImpl.getSpecimenValidations().get(centreSpecimen, specimen));
                validationReport.getValidation().addAll(this.specimenCommandImpl.getSpecimenValidations().get(centreSpecimen, specimen));
                //validationReport.setValidationReportValidation(ValidatorReportValidatorGenerator.getValidationReportValidation(validationReport,this.specimenCommandImpl.getSpecimenValidations().get(centreSpecimen, specimen)));
                validationReport.setResourceVersion(resourceVersion);
                validationReport.setIsValid(false);
//                validationReport.setReportIdentifier(this.getReportIdentifier(centreSpecimen.getCentreID(), row.getKey()));
                validationReport.setReportIdentifier(this.getReportIdentifier(centreSpecimen.getCentreID(), specimen));
                validationReport.setSubmissionDate(submission.getSubmissionDate());
                validationReportSet.getValidationReport().add(validationReport);
            }
        }
        logger.info("generating results for {} valid specimens", this.specimenCommandImpl.getValidSpecimens().size());
        for (CentreSpecimen centreSpecimen : this.specimenCommandImpl.getValidSpecimens().keySet()) {
            sortedSpecimens = this.specimenCommandImpl.getValidSpecimens().get(centreSpecimen).toArray(new Specimen[1]);
            Arrays.sort(sortedSpecimens, new SpecimenComparator());
            for (Specimen specimen : sortedSpecimens) {
                //for (Entry<CentreSpecimen, Specimen> entry : this.specimenCommandImpl.getValidSpecimens().entries()) {
                validationReport = new ValidationReport();
                //submission = this.getSubmission(entry.getKey());
                submission = this.getSubmission(centreSpecimen);
                mergedSubmission = this.hibernateManager.merge(submission);
                this.storeSubmission(mergedSubmission, true, null);
                validationReport.setSubmission(mergedSubmission);
                //validationReport.setCentreSpecimen(entry.getKey());
                validationReport.setCentreSpecimen(centreSpecimen);
                //validationReport.setSpecimen(entry.getValue());
                validationReport.setSpecimen(specimen);
                validationReport.setResourceVersion(resourceVersion);
                validationReport.setIsValid(true);
                //validationReport.setReportIdentifier(this.getReportIdentifier(entry.getKey().getCentreID(), entry.getValue()));
                validationReport.setReportIdentifier(this.getReportIdentifier(centreSpecimen.getCentreID(), specimen));
                validationReport.setSubmissionDate(submission.getSubmissionDate());
                validationReportSet.getValidationReport().add(validationReport);
            }
        }
             
        
        //submissions need to be collected at the end as they are retrieved from the database

        SubmissionCounter submissionCounter = new SubmissionCounter();
        submissionCounter.countSubmissions(this.submissions);
        logger.info("generating results for {} valid submissions, {} no valid submissions", submissionCounter.getValids(), submissionCounter.getInvalids());
        Iterator<Table.Cell<Submission, Boolean, List<Validation>>> iterator = this.submissions.cellSet().iterator();
        while (iterator.hasNext()) {
            Table.Cell<Submission, Boolean, List<Validation>> next = iterator.next();
            validationReport = new ValidationReport();
            validationReport.setSubmission(next.getRowKey());
            validationReport.setReportIdentifier(Long.toString(next.getRowKey().getTrackerID()));
            validationReport.setIsValid(next.getColumnKey());
            validationReport.setDuration(duration);
            validationReport.setSubmissionDate(next.getRowKey().getSubmissionDate());
            if (next.getColumnKey()) {
                //validationReport.setValidation(null);
                //validationReport.setValidationReportValidation(null);
            } else {
                //validationReport.setValidation(next.getValue());
                validationReport.getValidation().addAll(next.getValue());
                //validationReport.setValidationReportValidation(ValidatorReportValidatorGenerator.getValidationReportValidation(validationReport, next.getValue()));
            }
            validationReportSet.getValidationReport().add(validationReport);
        }

        logger.info("persisting {} results", validationReportSet.getValidationReport().size());
        this.hibernateManager.persist(validationReportSet);

    }
}
