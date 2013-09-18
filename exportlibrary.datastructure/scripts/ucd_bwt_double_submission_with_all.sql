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

SELECT EXPERIMENT.EXPERIMENTID,
    EXPERIMENT.SEQUENCEID,
    EXPERIMENT_SPECIMENID.HJVALUE
FROM phenodcc_raw.EXPERIMENT
JOIN phenodcc_raw.EXPERIMENT_SPECIMENID ON EXPERIMENT.HJID = EXPERIMENT_SPECIMENID.HJID,
    (SELECT --SUBMISSION.SUBMISSIONDATE,
            --SUBMISSION.TRACKERID,
            EXPERIMENT.EXPERIMENTID       AS innerExperimentID ,
            EXPERIMENT.SEQUENCEID         AS experiment_sequenceID,
            EXPERIMENT_SPECIMENID.HJVALUE AS mouse_id,
            COUNT(1)                      AS times
        FROM SUBMISSION
        JOIN phenodcc_raw.CENTREPROCEDURE       ON SUBMISSION.HJID = CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0
        JOIN phenodcc_raw.EXPERIMENT            ON CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
        JOIN phenodcc_raw.EXPERIMENT_SPECIMENID ON EXPERIMENT.HJID = EXPERIMENT_SPECIMENID.HJID
        JOIN phenodcc_raw.PROCEDURE_            ON EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
        JOIN phenodcc_raw.SIMPLEPARAMETER       ON PROCEDURE_.HJID = SIMPLEPARAMETER.SIMPLEPARAMETER_PROCEDURE__H_0
        WHERE CENTREPROCEDURE.CENTREID = 'UCD'
        AND PROCEDURE_.PROCEDUREID = 'IMPC_BWT_001'
        AND SIMPLEPARAMETER.PARAMETERID = 'IMPC_BWT_001_001'
        GROUP BY EXPERIMENT.EXPERIMENTID,
            EXPERIMENT.SEQUENCEID,
            mouse_id
        HAVING times>1 ) AS dataset
WHERE EXPERIMENT.EXPERIMENTID = dataset.innerExperimentID
AND EXPERIMENT_SPECIMENID.HJVALUE = mouse_id
    --and EXPERIMENT_SPECIMENID.HJVALUE = 'BL0009-7'
ORDER BY EXPERIMENT.EXPERIMENTID