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

SELECT fname,file_name, SUBMISSION.TRACKERID,  SPECIMEN.*
, VALIDATION.MESSAGE
FROM phenodcc_tracker.xml_file
JOIN phenodcc_tracker.zip_download        ON zip_download.id = xml_file.zip_id
JOIN phenodcc_tracker.file_source_has_zip ON zip_download.zf_id = file_source_has_zip.id
JOIN phenodcc_tracker.zip_action          ON file_source_has_zip.za_id = zip_action.id
JOIN phenodcc_tracker.zip_file            ON zip_action.zip_id = zip_file.id
JOIN phenodcc_raw.SUBMISSION              ON xml_file.id = SUBMISSION.TRACKERID
join phenodcc_raw.CENTRESPECIMEN on CENTRESPECIMEN.CENTRESPECIMEN_SUBMISSION_HJ_0 = SUBMISSION.HJID
join phenodcc_raw.SPECIMEN on SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0 = CENTRESPECIMEN.HJID
LEFT join phenodcc_raw.VALIDATION on SPECIMEN.HJID = VALIDATION.SPECIMEN_VALIDATION_HJID
where SPECIMEN.PRODUCTIONCENTRE = 'ICS'
and SPECIMEN.SPECIMENID = '61473'
AND xml_file.fname = 'Ics.2013-06-19.11.specimen.impc.xml';




SE



