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

SELECT COUNT(1),
    CENTRESPECIMEN.CENTREID,
    SPECIMEN.COLONYID,
    SPECIMEN.STRAINID
FROM SUBMISSIONSET
JOIN phenodcc_raw.SUBMISSION       ON SUBMISSIONSET.HJID = SUBMISSION.SUBMISSION_SUBMISSIONSET_HJID
JOIN phenodcc_raw.CENTRESPECIMEN   ON SUBMISSION.HJID = CENTRESPECIMEN.CENTRESPECIMEN_SUBMISSION_HJ_0
JOIN phenodcc_raw.SPECIMEN         ON CENTRESPECIMEN.HJID = SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0
JOIN phenodcc_raw.VALIDATIONREPORT ON phenodcc_raw.SPECIMEN.HJID = phenodcc_raw.VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0
LEFT JOIN phenodcc_raw.MOUSE       ON SPECIMEN.HJID =MOUSE.HJID
LEFT JOIN phenodcc_raw.EMBRYO      ON SPECIMEN.HJID = EMBRYO.HJID
WHERE VALIDATIONREPORT.ISVALID
AND NOT VALIDATIONREPORT.SUPERSEEDED
GROUP BY CENTRESPECIMEN.CENTREID,
    SPECIMEN.COLONYID,
    SPECIMEN.STRAINID