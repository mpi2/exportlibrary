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
    CENTREPROCEDURE.CENTREID,
    PROCEDURE_.PROCEDUREID,
    SPECIMEN.COLONYID,
    SPECIMEN.STRAINID
FROM phenodcc_raw.SUBMISSIONSET
    /*1*/
JOIN phenodcc_raw.SUBMISSION ON SUBMISSIONSET.HJID = SUBMISSION.SUBMISSION_SUBMISSIONSET_HJID
    /*2*/
JOIN phenodcc_raw.CENTREPROCEDURE ON SUBMISSION.HJID = CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0
    /*a1*/
JOIN phenodcc_raw.EXPERIMENT            ON CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
JOIN phenodcc_raw.EXPERIMENT_SPECIMENID ON EXPERIMENT.HJID = EXPERIMENT_SPECIMENID.HJID
JOIN phenodcc_raw.SPECIMEN              ON EXPERIMENT_SPECIMENID.HJVALUE = SPECIMEN.SPECIMENID
JOIN phenodcc_raw.PROCEDURE_            ON EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
    --
JOIN phenodcc_raw.VALIDATIONREPORT     ON EXPERIMENT.HJID = VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0
JOIN phenodcc_raw.VALIDATIONREPORT vr2 ON SPECIMEN.HJID = vr2.SPECIMEN_VALIDATIONREPORT_HJ_0
WHERE VALIDATIONREPORT.ISVALID
AND NOT VALIDATIONREPORT.SUPERSEEDED
AND vr2.ISVALID
AND NOT vr2.SUPERSEEDED


GROUP BY CENTREPROCEDURE.CENTREID,
    PROCEDURE_.PROCEDUREID, SPECIMEN.COLONYID,
    SPECIMEN.STRAINID
ORDER BY CENTREPROCEDURE.CENTREID,
    PROCEDURE_.PROCEDUREID, SPECIMEN.COLONYID,
    SPECIMEN.STRAINID