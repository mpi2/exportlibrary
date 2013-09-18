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

SELECT *
FROM phenodcc_tracker.xml_file
JOIN phenodcc_raw.SUBMISSION     ON xml_file.id = SUBMISSION.TRACKERID
JOIN phenodcc_context.context    ON SUBMISSION.HJID = context.id
JOIN phenodcc_raw.CENTRESPECIMEN ON SUBMISSION.HJID = CENTRESPECIMEN.CENTRESPECIMEN_SUBMISSION_HJ_0
JOIN phenodcc_raw.SPECIMEN       ON CENTRESPECIMEN.HJID = SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0
JOIN phenodcc_raw.STATUSCODE     ON SPECIMEN.STATUSCODE_SPECIMEN_HJID = STATUSCODE.HJID
JOIN phenodcc_raw.VALIDATION     ON SPECIMEN.HJID = VALIDATION.SPECIMEN_VALIDATION_HJID
WHERE VALUE_ IS NOT NULL
AND LEFT(VALUE_, IF (LOCATE(":", VALUE_)>0, LOCATE(":", VALUE_)-1, LENGTH(VALUE_))) NOT IN
    ( SELECT CODE
        FROM phenodcc_xmlvalidationresources.STATUSCODE )