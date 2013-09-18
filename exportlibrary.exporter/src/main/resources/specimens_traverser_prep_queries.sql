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

SELECT SUBMISSION.TRACKERID,
    VALIDATIONREPORT.*
FROM phenodcc_raw.SUBMISSION
JOIN phenodcc_raw.CENTREPROCEDURE  ON SUBMISSION.HJID = CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0
JOIN phenodcc_raw.EXPERIMENT       ON EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0 = CENTREPROCEDURE.HJID
JOIN phenodcc_raw.VALIDATIONREPORT ON EXPERIMENT_VALIDATIONREPORT__0 = EXPERIMENT.HJID
WHERE VALIDATIONREPORT.REPORTIDENTIFIER= 'GMC_102_21615_30253603_1_30253603';
--
--
SELECT VALIDATIONREPORT.REPORTIDENTIFIER,
    COUNT(1)
FROM phenodcc_raw.SUBMISSION
JOIN phenodcc_raw.CENTRESPECIMEN   ON SUBMISSION.HJID = CENTRESPECIMEN.CENTRESPECIMEN_SUBMISSION_HJ_0
JOIN phenodcc_raw.SPECIMEN         ON CENTRESPECIMEN.HJID = SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0
JOIN phenodcc_raw.VALIDATIONREPORT ON SPECIMEN.HJID = VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0
GROUP BY VALIDATIONREPORT.REPORTIDENTIFIER
HAVING COUNT(1) >1
ORDER BY COUNT(1) DESC';

SELECT fname,
    file_name,
    SUBMISSION.TRACKERID,
    VALIDATIONREPORT.*
FROM phenodcc_tracker.xml_file
JOIN phenodcc_tracker.zip_download        ON zip_download.id = xml_file.zip_id
JOIN phenodcc_tracker.file_source_has_zip ON zip_download.zf_id = file_source_has_zip.id
JOIN phenodcc_tracker.zip_action          ON file_source_has_zip.za_id = zip_action.id
JOIN phenodcc_tracker.zip_file            ON zip_action.zip_id = zip_file.id
JOIN phenodcc_raw.SUBMISSION              ON xml_file.id = SUBMISSION.TRACKERID
JOIN phenodcc_raw.CENTRESPECIMEN          ON SUBMISSION.HJID = CENTRESPECIMEN.CENTRESPECIMEN_SUBMISSION_HJ_0
JOIN phenodcc_raw.SPECIMEN                ON CENTRESPECIMEN.HJID = SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0
JOIN phenodcc_raw.VALIDATIONREPORT        ON SPECIMEN.HJID = VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0
WHERE VALIDATIONREPORT.REPORTIDENTIFIER = 'J_C2188';
