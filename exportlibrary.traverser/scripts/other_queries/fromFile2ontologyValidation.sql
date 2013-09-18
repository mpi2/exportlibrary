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


SELECT fname,file_name

--, ONTOLOGYPARAMETER.*, ONTOLOGYPARAMETER_TERM.*
, SUBMISSION.TRACKERID
,VALIDATIONREPORT.REPORTIDENTIFIER, VALIDATIONREPORT.HJID
FROM phenodcc_tracker.xml_file
JOIN phenodcc_tracker.zip_download        ON zip_download.id = xml_file.zip_id
JOIN phenodcc_tracker.file_source_has_zip ON zip_download.zf_id = file_source_has_zip.id
JOIN phenodcc_tracker.zip_action          ON file_source_has_zip.za_id = zip_action.id
JOIN phenodcc_tracker.zip_file            ON zip_action.zip_id = zip_file.id
JOIN phenodcc_raw.SUBMISSION      ON xml_file.id = SUBMISSION.TRACKERID
--JOIN phenodcc_context.context     ON SUBMISSION.HJID = context.id
JOIN phenodcc_raw.CENTREPROCEDURE ON SUBMISSION.HJID = CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0
JOIN phenodcc_raw.EXPERIMENT      ON EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0 = CENTREPROCEDURE.HJID
join phenodcc_raw.PROCEDURE_ on EXPERIMENT.PROCEDURE__EXPERIMENT_HJID =  PROCEDURE_.HJID
join phenodcc_raw.ONTOLOGYPARAMETER on ONTOLOGYPARAMETER.ONTOLOGYPARAMETER_PROCEDURE__0 = PROCEDURE_.HJID
join phenodcc_raw.ONTOLOGYPARAMETER_TERM on ONTOLOGYPARAMETER.HJID = ONTOLOGYPARAMETER_TERM.HJID
join phenodcc_raw.VALIDATIONREPORT on EXPERIMENT_VALIDATIONREPORT__0 = EXPERIMENT.HJID
JOIN phenodcc_raw.VALIDATION ON   VALIDATION.VALIDATION_VALIDATIONREPORT__0 = VALIDATIONREPORT.HJID
--join phenodcc_raw.VALIDATION on VALIDATIONREPORT.HJID = VALIDATION.VALIDATION_VALIDATIONREPORT__0

--where VALIDATIONREPORT.ISVALID

