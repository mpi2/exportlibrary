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

SELECT
    internal.procedureID,
    COUNT(1) AS distinct_specimens
FROM
    (
        SELECT
            PROCEDURE_.PROCEDUREID        AS procedureID,
            EXPERIMENT_SPECIMENID.HJVALUE AS specimenID
        FROM
            phenodcc_raw.EXPERIMENT
        JOIN
            phenodcc_raw.EXPERIMENT_SPECIMENID
        ON
            EXPERIMENT.HJID = EXPERIMENT_SPECIMENID.HJID
        JOIN
            phenodcc_raw.PROCEDURE_
        ON
            EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
        JOIN
            phenodcc_context.context
        ON
            phenodcc_raw.EXPERIMENT.HJID = phenodcc_context.context.subject
        WHERE
            
         phenodcc_context.context.isValid
         
        AND phenodcc_context.context.isActive
        
        GROUP BY
            PROCEDURE_.PROCEDUREID,
            EXPERIMENT_SPECIMENID.HJVALUE
        ORDER BY
            PROCEDURE_.PROCEDUREID,
            EXPERIMENT_SPECIMENID.HJVALUE ) AS internal
GROUP BY
    internal.procedureID
ORDER BY
    internal.procedureID
 // IMPROVEMENT: added centre, project, submission date to valid+active entries
/*
select internal.procedureID, internal.centreID, internal.project, internal.submissionDATE, count(1) as distinct_specimens
from
    (select PROCEDURE_.PROCEDUREID as procedureID, EXPERIMENT_SPECIMENID.HJVALUE as specimenID, CENTREPROCEDURE.CENTREID as centreID, CENTREPROCEDURE.PROJECT as project,
    SUBMISSION.SUBMISSIONDATE as submissionDATE
	from phenodcc_raw.EXPERIMENT
	join phenodcc_raw.EXPERIMENT_SPECIMENID on EXPERIMENT.HJID = EXPERIMENT_SPECIMENID.HJID
	join phenodcc_raw.PROCEDURE_ on EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
	join phenodcc_raw.CENTREPROCEDURE on EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0 = CENTREPROCEDURE.HJID
	join phenodcc_raw.SUBMISSION on CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID
	join phenodcc_context.context on phenodcc_raw.EXPERIMENT.HJID = phenodcc_context.context.subject
	where phenodcc_context.context.isValid
	and phenodcc_context.context.isActive
	group by PROCEDURE_.PROCEDUREID, EXPERIMENT_SPECIMENID.HJVALUE, CENTREPROCEDURE.CENTREID, CENTREPROCEDURE.PROJECT, SUBMISSION.SUBMISSIONDATE
	order by PROCEDURE_.PROCEDUREID, EXPERIMENT_SPECIMENID.HJVALUE
	) as internal
group by internal.procedureID, internal.centreID, internal.project, internal.submissionDATE
order by internal.procedureID, internal.centreID, internal.project, internal.submissionDATE desc
*/