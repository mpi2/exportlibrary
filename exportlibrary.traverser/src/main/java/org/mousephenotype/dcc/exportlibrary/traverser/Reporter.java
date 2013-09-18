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
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Housing;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Line;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ResourceVersion;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReport;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;
import org.mousephenotype.dcc.exportlibrary.traverser.aux.ExperimentComparator;
import org.mousephenotype.dcc.exportlibrary.traverser.aux.HousingComparator;
import org.mousephenotype.dcc.exportlibrary.traverser.aux.LineComparator;
import org.mousephenotype.dcc.exportlibrary.traverser.aux.SubmissionCounter;
//import org.mousephenotype.dcc.exportlibrary.traverser.aux.ValidatorReportValidatorGenerator;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reporter {

    private static final Logger logger = LoggerFactory.getLogger(Reporter.class);
    private final CommandImpl validationResultsExtractor;
    //
    //
    private final HibernateManager hibernateManager;
    private final Table<Submission, Boolean, List<Validation>> submissions;

    public Reporter(CommandImpl validationResultsExtractor, HibernateManager hibernateManager) {
        this.validationResultsExtractor = validationResultsExtractor;
        this.hibernateManager = hibernateManager;
        this.submissions = HashBasedTable.create();
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

    private Submission getSubmission(CentreProcedure centreProcedure) {
        Submission container = this.hibernateManager.getContainer(centreProcedure, Submission.class, "centreProcedure");
        return container;
    }

    public Table<Submission, Boolean, List<Validation>> getSubmissions() {
        return submissions;
    }

    private String getReportRow(Submission submission, CentreProcedure centreProcedure, Experiment experiment) {
        StringBuilder row = new StringBuilder();
        row.append(submission.getHjid());
        row.append(",");
        row.append(DatatypeConverter.printDateTimeForFilename(submission.getSubmissionDate()));
        row.append(",");
        row.append(submission.getTrackerID());
        row.append(",");
        row.append(centreProcedure.getCentreID().toString());
        row.append(",");
        row.append(centreProcedure.getHjid());
        row.append(",");
        row.append(experiment.getExperimentID());
        row.append(",");
        if (experiment.getSequenceID() != null && !experiment.getSequenceID().isEmpty()) {
            row.append(experiment.getSequenceID());
            row.append(",");
        }
        row.append(experiment.getHjid());
        row.append("\n");
        return row.toString();
    }

    public String getReportIdentifier(CentreILARcode centreILarCode, Line line) {
        StringBuilder sb = new StringBuilder();
        sb.append(centreILarCode);
        sb.append("_");
        sb.append(line.getColonyID());
        return sb.toString();
    }

    public String getReportIdentifier(CentreILARcode centreILarCode, Housing housing) {
        StringBuilder sb = new StringBuilder();
        sb.append(centreILarCode);
        sb.append("_");
        sb.append(housing.isFromLIMS());
        sb.append("_");
        sb.append(DatatypeConverter.printDate(housing.getLastUpdated()));
        return sb.toString();
    }

    /*
     * if (submission, centreProcedure, Experiment) : centreILarCode_experimentID_experimentSequenceID_specimenID*
     * 
     * not procedureID because it can be invalid as not registered in impress
     */
    public String getReportIdentifier(CentreILARcode centreILarCode, Experiment experiment) {
        StringBuilder sb = new StringBuilder();
        sb.append(centreILarCode);
        sb.append("_");
        sb.append(experiment.getExperimentID());
        sb.append("_");

        if (experiment.getSequenceID() != null && !experiment.getSequenceID().isEmpty()) {
            sb.append(experiment.getSequenceID());
            sb.append("_");
        }
        for (String specimenID : experiment.getSpecimenID()) {
            sb.append(specimenID);
            sb.append("_");
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    /*
     if (submission, centreProcedure) as this particular centreProcedure is wrong o
     */

    public String getReportIdentifier(Submission submission, CentreProcedure centreProcedure) {
        StringBuilder sb = new StringBuilder();
        sb.append(centreProcedure.getCentreID().toString());
        sb.append("_");
        sb.append(submission.getTrackerID());
        sb.append("_");
        sb.append(centreProcedure.getHjid());

        return sb.toString();
    }

    /*
     this method makes every previously submitted experiment superseeded
     * and not the latest, that are the list of paremeter ones
     * The experiments need to be sorted by hjid, as if the same experiment is 
     * submitted twice, only the last wont be superseeded
     * 
     * update phenodcc_raw.VALIDATIONREPORT
     set VALIDATIONREPORT.SUPERSEEDED = true
     where VALIDATIONREPORT.REPORTIDENTIFIER = 'GMC_102_21615_30253603_1_30253603';

     update phenodcc_raw.VALIDATIONREPORT
     set VALIDATIONREPORT.SUPERSEEDED = false
     where VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0 = 250620

     * 
     */
    private void updateValidationReport(String reportIdentifier) {
        String resetQuery = " update  VALIDATIONREPORT\n"
                + "set VALIDATIONREPORT.SUPERSEEDED = true\n"
                + "where VALIDATIONREPORT.REPORTIDENTIFIER =:reportIdentifier";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportIdentifier", reportIdentifier);
        this.hibernateManager.nativeExecution(resetQuery, parameters);

        /*
         String updateQuery = "update VALIDATIONREPORT\n"
         + "set VALIDATIONREPORT.SUPERSEEDED = false\n"
         + "where VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0 = :experimentHJID";*/
        String updateQuery = "update VALIDATIONREPORT, "
                + "(select max(hjid) as mxx from  VALIDATIONREPORT where  VALIDATIONREPORT.REPORTIDENTIFIER = :reportIdentifier) as inter\n"
                + "set VALIDATIONREPORT.SUPERSEEDED = false\n"
                + "where VALIDATIONREPORT.REPORTIDENTIFIER = :reportIdentifier\n"
                + "and VALIDATIONREPORT.HJID = inter.mxx";
        this.hibernateManager.nativeExecution(updateQuery, parameters);
    }
    /*
     If updateValidationReport, then don't need to sort the experiments?
     */

    public void updateHistory() {
        logger.info("updating superseed for {} no valid experiments", this.validationResultsExtractor.getExperimentValidations().size());
        String reportIdentifier = null;
        Experiment[] sortedExperiments;
        for (final CentreProcedure centreProcedure : this.validationResultsExtractor.getExperimentValidations().rowKeySet()) {
            sortedExperiments = this.validationResultsExtractor.getExperimentValidations().row(centreProcedure).keySet().toArray(new Experiment[1]);
            Arrays.sort(sortedExperiments, new ExperimentComparator());
            for (Experiment experiment : sortedExperiments) {
                reportIdentifier = this.getReportIdentifier(centreProcedure.getCentreID(), experiment);
                this.updateValidationReport(reportIdentifier);
            }
        }
        logger.info("updating superseed for {} valid experiments", this.validationResultsExtractor.getValidExperiments().size());

        for (CentreProcedure centreProcedure : this.validationResultsExtractor.getValidExperiments().keySet()) {
            sortedExperiments = this.validationResultsExtractor.getValidExperiments().get(centreProcedure).toArray(new Experiment[1]);
            Arrays.sort(sortedExperiments, new ExperimentComparator());
            //for (Experiment experiment : this.validationResultsExtractor.getValidExperiments().get(centreProcedure)) {
            for (Experiment experiment : sortedExperiments) {
                reportIdentifier = this.getReportIdentifier(centreProcedure.getCentreID(), experiment);
                this.updateValidationReport(reportIdentifier);
            }
        }
        logger.info("updating superseed for {} no valid lines",this.validationResultsExtractor.getLineValidations().size());
        Line[] sortedLines;
        for (final CentreProcedure centreProcedure : this.validationResultsExtractor.getLineValidations().rowKeySet()) {
            sortedLines = this.validationResultsExtractor.getLineValidations().row(centreProcedure).keySet().toArray(new Line[1]);
            Arrays.sort(sortedLines, new LineComparator());
            for (Line line : sortedLines) {
                reportIdentifier = this.getReportIdentifier(centreProcedure.getCentreID(), line);
                this.updateValidationReport(reportIdentifier);
            }
        }

        logger.info("updating superseed for {} valid  lines",this.validationResultsExtractor.getValidLines().size());
        for (CentreProcedure centreProcedure : this.validationResultsExtractor.getValidLines().keySet()) {
            sortedLines = this.validationResultsExtractor.getValidLines().get(centreProcedure).toArray(new Line[1]);
            Arrays.sort(sortedLines, new LineComparator());
            for (Line line : sortedLines) {
                reportIdentifier = this.getReportIdentifier(centreProcedure.getCentreID(), line);
                this.updateValidationReport(reportIdentifier);
            }
        }
        logger.info("updating superseed for {} no valid housing",this.validationResultsExtractor.getHousingValidations().size());
        Housing[] sortedHousings;
        for (CentreProcedure centreProcedure : this.validationResultsExtractor.getHousingValidations().rowKeySet()) {
            sortedHousings = this.validationResultsExtractor.getHousingValidations().row(centreProcedure).keySet().toArray(new Housing[1]);
            Arrays.sort(sortedHousings, new HousingComparator());
            for (Housing housing : sortedHousings) {
                reportIdentifier = this.getReportIdentifier(centreProcedure.getCentreID(), housing);
                this.updateValidationReport(reportIdentifier);
            }
        }
        logger.info("updating superseed for {} valid  housing",this.validationResultsExtractor.getValidHousings());
        for (CentreProcedure centreProcedure : this.validationResultsExtractor.getValidHousings().keySet()) {
            sortedHousings = this.validationResultsExtractor.getValidHousings().get(centreProcedure).toArray(new Housing[1]);
            Arrays.sort(sortedHousings, new HousingComparator());
            for (Housing housing : sortedHousings) {
                reportIdentifier = this.getReportIdentifier(centreProcedure.getCentreID(), housing);
                this.updateValidationReport(reportIdentifier);
            }
        }
    }

    public void compileResults(ResourceVersion resourceVersion, long duration) {
        ValidationReportSet validationReportSet = new ValidationReportSet();
        Submission submission = null;
        Submission mergedSubmission = null;
        ValidationReport validationReport = null;
        logger.info("generating results for {} no valid experiments", this.validationResultsExtractor.getExperimentValidations().size());
        Experiment[] sortedExperiments;
        for (final CentreProcedure centreProcedure : this.validationResultsExtractor.getExperimentValidations().rowKeySet()) {
            sortedExperiments = this.validationResultsExtractor.getExperimentValidations().row(centreProcedure).keySet().toArray(new Experiment[1]);
            Arrays.sort(sortedExperiments, new ExperimentComparator());
            for (Experiment experiment : sortedExperiments) {
                //for (Entry<Experiment, List<Validation>> row : this.validationResultsExtractor.getExperimentValidations().row(centreProcedure).entrySet()) {
                validationReport = new ValidationReport();
                submission = this.getSubmission(centreProcedure);
                mergedSubmission = this.hibernateManager.merge(submission);
                validationReport.setSubmission(mergedSubmission);
                //this.storeSubmission(mergedSubmission, false, row.getValue());
                this.storeSubmission(mergedSubmission, false, this.validationResultsExtractor.getExperimentValidations().get(centreProcedure, experiment));
                validationReport.setCentreProcedure(centreProcedure);
                validationReport.setExperiment(experiment);
                //validationReport.setValidation(this.validationResultsExtractor.getExperimentValidations().get(centreProcedure, experiment));
                validationReport.getValidation().addAll(this.validationResultsExtractor.getExperimentValidations().get(centreProcedure, experiment));
                //validationReport.setValidationReportValidation(ValidatorReportValidatorGenerator.getValidationReportValidation(validationReport,this.validationResultsExtractor.getExperimentValidations().get(centreProcedure, experiment) ));
                validationReport.setResourceVersion(resourceVersion);
                validationReport.setIsValid(false);
                //validationReport.setReportIdentifier(this.getReportIdentifier(centreProcedure.getCentreID(), row.getKey()));
                validationReport.setReportIdentifier(this.getReportIdentifier(centreProcedure.getCentreID(), experiment));
                validationReport.setSubmissionDate(submission.getSubmissionDate());
                validationReportSet.getValidationReport().add(validationReport);
            }
        }

        /**/
        logger.info("generating results for {} no valid lines", this.validationResultsExtractor.getLineValidations().size());
        Line[] sortedLines;
        for (final CentreProcedure centreProcedure : this.validationResultsExtractor.getLineValidations().rowKeySet()) {
            sortedLines = this.validationResultsExtractor.getLineValidations().row(centreProcedure).keySet().toArray(new Line[1]);
            Arrays.sort(sortedLines, new LineComparator());
            for (Line line : sortedLines) {
                validationReport = new ValidationReport();
                submission = this.getSubmission(centreProcedure);
                mergedSubmission = this.hibernateManager.merge(submission);
                validationReport.setSubmission(mergedSubmission);
                this.storeSubmission(mergedSubmission, false, this.validationResultsExtractor.getLineValidations().get(centreProcedure, line));
                validationReport.setCentreProcedure(centreProcedure);
                validationReport.setLine(line);
                validationReport.getValidation().addAll(this.validationResultsExtractor.getLineValidations().get(centreProcedure, line));
                validationReport.setResourceVersion(resourceVersion);
                validationReport.setIsValid(false);
                validationReport.setReportIdentifier(this.getReportIdentifier(centreProcedure.getCentreID(), line));
                validationReport.setSubmissionDate(submission.getSubmissionDate());
                validationReportSet.getValidationReport().add(validationReport);
            }
        }

        logger.info("generating results for {} no valid housing", this.validationResultsExtractor.getHousingValidations().size());
        Housing[] sortedHousings;
        for (final CentreProcedure centreProcedure : this.validationResultsExtractor.getHousingValidations().rowKeySet()) {
            sortedHousings = this.validationResultsExtractor.getHousingValidations().row(centreProcedure).keySet().toArray(new Housing[1]);
            Arrays.sort(sortedHousings, new HousingComparator());
            for (Housing housing : sortedHousings) {
                validationReport = new ValidationReport();
                submission = this.getSubmission(centreProcedure);
                mergedSubmission = this.hibernateManager.merge(submission);
                validationReport.setSubmission(mergedSubmission);
                this.storeSubmission(mergedSubmission, false, this.validationResultsExtractor.getHousingValidations().get(centreProcedure, housing));
                validationReport.setCentreProcedure(centreProcedure);
                validationReport.setHousing(housing);
                validationReport.getValidation().addAll(this.validationResultsExtractor.getHousingValidations().get(centreProcedure, housing));
                validationReport.setResourceVersion(resourceVersion);
                validationReport.setIsValid(false);
                validationReport.setReportIdentifier(this.getReportIdentifier(centreProcedure.getCentreID(), housing));
                validationReport.setSubmissionDate(submission.getSubmissionDate());
                validationReportSet.getValidationReport().add(validationReport);
            }
        }
        /**/


        logger.info("generating results for {} no valid centreProcedures", this.validationResultsExtractor.getCentreProcedureValidations().keySet().size());
        for (final CentreProcedure centreProcedure : this.validationResultsExtractor.getCentreProcedureValidations().keySet()) {
            validationReport = new ValidationReport();
            submission = this.getSubmission(centreProcedure);
            mergedSubmission = this.hibernateManager.merge(submission);
            validationReport.setSubmission(mergedSubmission);
            this.storeSubmission(mergedSubmission, false, (List<Validation>) this.validationResultsExtractor.getCentreProcedureValidations().get(centreProcedure));
            validationReport.setCentreProcedure(centreProcedure);
            //validationReport.setValidation((List<Validation>) this.validationResultsExtractor.getCentreProcedureValidations().get(centreProcedure));
            validationReport.getValidation().addAll(this.validationResultsExtractor.getCentreProcedureValidations().get(centreProcedure));
            //validationReport.setValidationReportValidation(ValidatorReportValidatorGenerator.getValidationReportValidation(validationReport,(List<Validation>) this.validationResultsExtractor.getCentreProcedureValidations().get(centreProcedure)));
            validationReport.setResourceVersion(resourceVersion);
            validationReport.setIsValid(false);
            validationReport.setReportIdentifier(this.getReportIdentifier(submission, centreProcedure));
            validationReport.setSubmissionDate(submission.getSubmissionDate());
            validationReportSet.getValidationReport().add(validationReport);
        }


        logger.info("generating results for {} valid centreProcedures", this.validationResultsExtractor.getValidCentreProcedures().size());
        for (final CentreProcedure centreProcedure : this.validationResultsExtractor.getValidCentreProcedures()) {
            validationReport = new ValidationReport();
            submission = this.getSubmission(centreProcedure);
            mergedSubmission = this.hibernateManager.merge(submission);
            validationReport.setSubmission(mergedSubmission);
            this.storeSubmission(mergedSubmission, true, null);
            validationReport.setCentreProcedure(centreProcedure);
            validationReport.setResourceVersion(resourceVersion);
            validationReport.setIsValid(true);
            validationReport.setReportIdentifier(this.getReportIdentifier(submission, centreProcedure));
            validationReport.setSubmissionDate(submission.getSubmissionDate());
            validationReportSet.getValidationReport().add(validationReport);
        }

        logger.info("generating results for {} valid experiments", this.validationResultsExtractor.getValidExperiments().size());
        //for (Entry<CentreProcedure, Experiment> entry : this.validationResultsExtractor.getValidExperiments().entries()) {
        for (CentreProcedure centreProcedure : this.validationResultsExtractor.getValidExperiments().keySet()) {
            sortedExperiments = this.validationResultsExtractor.getValidExperiments().get(centreProcedure).toArray(new Experiment[1]);
            Arrays.sort(sortedExperiments, new ExperimentComparator());
            for (Experiment experiment : this.validationResultsExtractor.getValidExperiments().get(centreProcedure)) {
                validationReport = new ValidationReport();
                //submission = this.getSubmission(entry.getKey());
                submission = this.getSubmission(centreProcedure);
                mergedSubmission = this.hibernateManager.merge(submission);
                validationReport.setSubmission(mergedSubmission);
                this.storeSubmission(mergedSubmission, true, null);
                //validationReport.setCentreProcedure(entry.getKey());
                validationReport.setCentreProcedure(centreProcedure);
                //validationReport.setExperiment(entry.getValue());
                validationReport.setExperiment(experiment);
                validationReport.setResourceVersion(resourceVersion);
                validationReport.setIsValid(true);
                //validationReport.setReportIdentifier(this.getReportIdentifier(entry.getKey().getCentreID(), entry.getValue()));
                validationReport.setReportIdentifier(this.getReportIdentifier(centreProcedure.getCentreID(), experiment));
                validationReport.setSubmissionDate(submission.getSubmissionDate());
                validationReportSet.getValidationReport().add(validationReport);
            }
        }

        /**
         * *
         */
        logger.info("generating results for {} valid lines", this.validationResultsExtractor.getValidLines().size());
        for (CentreProcedure centreProcedure : this.validationResultsExtractor.getValidLines().keySet()) {
            sortedLines = this.validationResultsExtractor.getValidLines().get(centreProcedure).toArray(new Line[1]);
            Arrays.sort(sortedLines, new LineComparator());
            for (Line line : this.validationResultsExtractor.getValidLines().get(centreProcedure)) {
                validationReport = new ValidationReport();
                submission = this.getSubmission(centreProcedure);
                mergedSubmission = this.hibernateManager.merge(submission);
                validationReport.setSubmission(mergedSubmission);
                this.storeSubmission(mergedSubmission, true, null);
                validationReport.setCentreProcedure(centreProcedure);
                validationReport.setLine(line);
                validationReport.setResourceVersion(resourceVersion);
                validationReport.setIsValid(true);
                validationReport.setReportIdentifier(this.getReportIdentifier(centreProcedure.getCentreID(), line));
                validationReport.setSubmissionDate(submission.getSubmissionDate());
                validationReportSet.getValidationReport().add(validationReport);
            }
        }

        logger.info("generating results for {} valid housings", this.validationResultsExtractor.getValidHousings().size());
        for (CentreProcedure centreProcedure : this.validationResultsExtractor.getValidHousings().keySet()) {
            sortedHousings = this.validationResultsExtractor.getValidHousings().get(centreProcedure).toArray(new Housing[1]);
            Arrays.sort(sortedHousings, new HousingComparator());
            for (Housing housing : this.validationResultsExtractor.getValidHousings().get(centreProcedure)) {
                validationReport = new ValidationReport();
                submission = this.getSubmission(centreProcedure);
                mergedSubmission = this.hibernateManager.merge(submission);
                validationReport.setSubmission(mergedSubmission);
                this.storeSubmission(mergedSubmission, true, null);
                validationReport.setCentreProcedure(centreProcedure);
                validationReport.setHousing(housing);
                validationReport.setResourceVersion(resourceVersion);
                validationReport.setIsValid(true);
                validationReport.setReportIdentifier(this.getReportIdentifier(centreProcedure.getCentreID(), housing));
                validationReport.setSubmissionDate(submission.getSubmissionDate());
                validationReportSet.getValidationReport().add(validationReport);
            }
        }


        /**
         * *
         */
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
                //validationReport.setValidationReportValidation(ValidatorReportValidatorGenerator.getValidationReportValidation(validationReport,next.getValue()));
            }
            validationReportSet.getValidationReport().add(validationReport);
        }

        logger.info("persisting {} results", validationReportSet.getValidationReport().size());
        this.hibernateManager.persist(validationReportSet);
    }

    public void persistExperiments(String validFilename, String inValidFilename) throws IOException {
        Path validFilePath = Paths.get(validFilename);
        Path invalidFilePath = Paths.get(inValidFilename);


        Files.deleteIfExists(validFilePath);
        Files.createFile(validFilePath);
        try (BufferedWriter validWriter = Files.newBufferedWriter(validFilePath, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
            validWriter.write("submission_HJID, submissionDate, submissionTrackerID, centreILarCode, centreProcedure_HJID, experimentID,experimentSequenceID, experiment_HJID\n");
        }

        Files.deleteIfExists(invalidFilePath);
        Files.createFile(invalidFilePath);
        try (BufferedWriter invalidWriter = Files.newBufferedWriter(invalidFilePath, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
            invalidWriter.write("submission_HJID, submissionDate, submissionTrackerID, centreILarCode, centreProcedure_HJID, experimentID,experimentSequenceID, experiment_HJID\n");
        }


        try (BufferedWriter invalidWriter = Files.newBufferedWriter(invalidFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            for (final CentreProcedure centreProcedure : this.validationResultsExtractor.getExperimentValidations().rowKeySet()) {
                for (Entry<Experiment, List<Validation>> row : this.validationResultsExtractor.getExperimentValidations().row(centreProcedure).entrySet()) {
                    if (row.getValue() != null && !row.getValue().isEmpty()) {
                        invalidWriter.write(this.getReportRow(this.getSubmission(centreProcedure), centreProcedure, row.getKey()));
                    } else {
                        logger.error("++experiment with no validations found");
                    }
                }
            }

        }

        try (BufferedWriter validWriter = Files.newBufferedWriter(validFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            for (Entry<CentreProcedure, Experiment> entry : this.validationResultsExtractor.getValidExperiments().entries()) {
                validWriter.write(this.getReportRow(this.getSubmission(entry.getKey()), entry.getKey(), entry.getValue()));
            }
        }
    }
}
