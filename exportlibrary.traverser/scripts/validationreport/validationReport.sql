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
how many times an instance has been submitted
*/
SELECT IF(VALIDATIONREPORT.CENTREPROCEDURE_VALIDATIONRE_0 IS NULL
AND VALIDATIONREPORT.CENTRESPECIMEN_VALIDATIONREP_0 IS NULL, "submission", IF (VALIDATIONREPORT.CENTREPROCEDURE_VALIDATIONRE_0 IS NOT NULL
AND VALIDATIONREPORT.LINE_VALIDATIONREPORT__0 IS NULL
AND HOUSING_VALIDATIONREPORT__0 IS NULL
AND VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0 IS NULL, "centreprocedure", IF (VALIDATIONREPORT.CENTRESPECIMEN_VALIDATIONREP_0 IS NOT NULL
AND VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0 IS NULL,"centrespecimen", IF(VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0 IS NOT NULL,"specimen", IF(HOUSING_VALIDATIONREPORT__0 IS NOT NULL
AND VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0 IS NULL,"housing", IF(LINE_VALIDATIONREPORT__0 IS NOT NULL
AND VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0 IS NULL,"line", IF(VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0 IS NOT NULL,"experiment",""))))))) AS type,
    VALIDATIONREPORT.REPORTIDENTIFIER,
    MIN(VALIDATIONREPORT.SUBMISSIONDATE) AS firstTimeSubmitted,
    MAX(VALIDATIONREPORT.SUBMISSIONDATE) AS lastTimeSubmitted,
    COUNT(1)                             AS timesSubmitted
FROM phenodcc_raw.VALIDATIONREPORT
GROUP BY VALIDATIONREPORT.REPORTIDENTIFIER
ORDER BY COUNT(1) DESC;
/*
see which files they came from. It shows how many times the entity was present in each file
*/
SELECT fname,
    xml_file.created,
    xml_file.last_update,
    xml_file.id AS trackerID,
    DATE(VALIDATIONREPORT.SUBMISSIONDATE),
     VALIDATIONREPORT.REPORTIDENTIFIER,
     VALIDATIONREPORT.ISVALID,
     VALIDATIONREPORT.SUPERSEEDED
FROM phenodcc_tracker.xml_file
JOIN phenodcc_tracker.zip_download        ON zip_download.id = xml_file.zip_id
JOIN phenodcc_tracker.file_source_has_zip ON zip_download.zf_id = file_source_has_zip.id
JOIN phenodcc_tracker.zip_action          ON file_source_has_zip.za_id = zip_action.id
JOIN phenodcc_tracker.zip_file            ON zip_action.zip_id = zip_file.id
JOIN phenodcc_raw.SUBMISSION              ON phenodcc_tracker.xml_file.id = SUBMISSION.TRACKERID
JOIN phenodcc_raw.VALIDATIONREPORT        ON SUBMISSION.HJID = VALIDATIONREPORT.SUBMISSION_VALIDATIONREPORT__0
WHERE REPORTIDENTIFIER= 'J_20120515F-11'
ORDER BY trackerID ASC