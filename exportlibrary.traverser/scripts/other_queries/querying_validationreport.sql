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

--
SELECT VALIDATIONREPORT.SUBMISSION_VALIDATIONREPORT__0 AS submission,
    VALIDATIONREPORT.CENTREPROCEDURE_VALIDATIONRE_0    AS centreProcedure,
    VALIDATIONREPORT.CENTRESPECIMEN_VALIDATIONREP_0    AS centreSpecimen,
    VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0    AS experiment,
    VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0    AS specimen,
    VALIDATIONREPORT.ISVALID,
    VALIDATIONREPORT.SUPERSEEDED,
    VALIDATIONREPORT.REPORTIDENTIFIER,
    VALIDATION.MESSAGE
FROM phenodcc_raw_m2m.CENTRESPECIMEN
left JOIN phenodcc_raw_m2m.VALIDATIONREPORT                     ON VALIDATIONREPORT.CENTRESPECIMEN_VALIDATIONREP_0 = CENTRESPECIMEN.HJID
left JOIN phenodcc_raw_m2m.VALIDATION_REPORT_VALIDATION_GROUPED ON VALIDATIONREPORT.HJID = VALIDATION_REPORT_VALIDATION_GROUPED.validationreport
left JOIN phenodcc_raw_m2m.VALIDATION                           ON VALIDATION_REPORT_VALIDATION_GROUPED.validation = VALIDATION.HJID
    --group by VALIDATION.MESSAGE
ORDER BY submission,
    centreProcedure,
    experiment,
    centreSpecimen,
    specimen;
