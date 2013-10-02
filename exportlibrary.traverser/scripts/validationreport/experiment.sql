--
-- Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
--
-- MEDICAL RESEARCH COUNCIL UK MRC
--
-- Harwell Mammalian Genetics Unit
--
-- http://www.har.mrc.ac.uk
--
-- Licensed under the Apache License, Version 2.0 (the "License"); you may not
-- use this file except in compliance with the License. You may obtain a copy of
-- the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
-- WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
-- License for the specific language governing permissions and limitations under
-- the License.
--
/*
it contains a line per validation error per experiment
*/
SELECT SUBMISSION.TRACKERID,
    SUBMISSION.SUBMISSIONDATE,
    CENTREPROCEDURE.CENTREID,
    EXPERIMENT.EXPERIMENTID,
    VALIDATIONREPORT.REPORTIDENTIFIER,
    VALIDATIONREPORT.ISVALID,
    VALIDATIONREPORT.SUPERSEEDED,
    VALIDATION.MESSAGE
FROM phenodcc_raw.SUBMISSION
JOIN phenodcc_raw.CENTREPROCEDURE       ON CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID
JOIN phenodcc_raw.EXPERIMENT            ON CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
JOIN phenodcc_raw.VALIDATIONREPORT ON SUBMISSION.HJID = VALIDATIONREPORT.SUBMISSION_VALIDATIONREPORT__0 AND CENTREPROCEDURE.HJID = VALIDATIONREPORT.CENTREPROCEDURE_VALIDATIONRE_0 AND
    EXPERIMENT.HJID = VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0
LEFT JOIN phenodcc_raw.VALIDATION_REPORT_VALIDATION ON VALIDATIONREPORT.HJID = VALIDATION_REPORT_VALIDATION.validationreport
LEFT JOIN phenodcc_raw.VALIDATION                   ON VALIDATION.HJID = VALIDATION_REPORT_VALIDATION.validation
ORDER BY DATE(SUBMISSION.SUBMISSIONDATE) DESC, VALIDATIONREPORT.REPORTIDENTIFIER;



--
--
/*
and see what happens for a particular one
*/
SELECT fname,
    xml_file.created,
    xml_file.last_update,
    xml_file.id                           AS trackerID,
    DATE(VALIDATIONREPORT.SUBMISSIONDATE) AS submissionDate,
    VALIDATIONREPORT.REPORTIDENTIFIER,
    VALIDATIONREPORT.ISVALID,
    VALIDATIONREPORT.SUPERSEEDED,
    VALIDATION.MESSAGE
FROM phenodcc_tracker.xml_file
JOIN phenodcc_tracker.zip_download                  ON zip_download.id = xml_file.zip_id
JOIN phenodcc_tracker.file_source_has_zip           ON zip_download.zf_id = file_source_has_zip.id
JOIN phenodcc_tracker.zip_action                    ON file_source_has_zip.za_id = zip_action.id
JOIN phenodcc_tracker.zip_file                      ON zip_action.zip_id = zip_file.id
JOIN phenodcc_raw.SUBMISSION                        ON phenodcc_tracker.xml_file.id = SUBMISSION.TRACKERID
JOIN phenodcc_raw.VALIDATIONREPORT                  ON SUBMISSION.HJID = VALIDATIONREPORT.SUBMISSION_VALIDATIONREPORT__0
LEFT JOIN phenodcc_raw.VALIDATION_REPORT_VALIDATION ON VALIDATIONREPORT.HJID = VALIDATION_REPORT_VALIDATION.validationreport
LEFT JOIN phenodcc_raw.VALIDATION                   ON VALIDATION.HJID = VALIDATION_REPORT_VALIDATION.validation
WHERE VALIDATIONREPORT.REPORTIDENTIFIER = 'GMC_166_22649_30262142_1_30262142'
ORDER BY trackerID ASC