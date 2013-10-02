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

/*You want to know everything that has happened to a specimen:
you get row for each validation for each specimen*/
SELECT SUBMISSION.TRACKERID,
    SUBMISSION.SUBMISSIONDATE,
    CENTRESPECIMEN.CENTREID,
    SPECIMEN.SPECIMENID,
    SPECIMEN.COLONYID,
    SPECIMEN.ISBASELINE,
    SPECIMEN.STRAINID,
    VALIDATIONREPORT.REPORTIDENTIFIER,
    VALIDATIONREPORT.ISVALID,
    VALIDATIONREPORT.SUPERSEEDED,
    VALIDATION.MESSAGE
FROM phenodcc_raw.SUBMISSION
JOIN phenodcc_raw.CENTRESPECIMEN        ON SUBMISSION.HJID = CENTRESPECIMEN.CENTRESPECIMEN_SUBMISSION_HJ_0
JOIN phenodcc_raw.SPECIMEN              ON CENTRESPECIMEN.HJID = SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0
LEFT JOIN phenodcc_raw.VALIDATIONREPORT ON SUBMISSION.HJID = VALIDATIONREPORT.SUBMISSION_VALIDATIONREPORT__0 AND CENTRESPECIMEN.HJID = VALIDATIONREPORT.CENTRESPECIMEN_VALIDATIONREP_0 AND
    SPECIMEN.HJID = VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0
LEFT JOIN phenodcc_raw.VALIDATION_REPORT_VALIDATION ON VALIDATIONREPORT.HJID = VALIDATION_REPORT_VALIDATION.validationreport
LEFT JOIN phenodcc_raw.VALIDATION                   ON VALIDATION.HJID = VALIDATION_REPORT_VALIDATION.validation
LEFT JOIN phenodcc_raw.MOUSE                        ON SPECIMEN.HJID = MOUSE.HJID
LEFT JOIN phenodcc_raw.EMBRYO                       ON SPECIMEN.HJID = EMBRYO.HJID
WHERE SPECIMEN.SPECIMENID = '58428'