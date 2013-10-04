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
DROP TABLE IF EXISTS mutant_procedures;
CREATE TABLE mutant_procedures
    (
        colonyID VARCHAR(255) NOT NULL,
        PROCEDUREID VARCHAR(255) NOT NULL,
        mutants_per_procedureID bigint,
        PRIMARY KEY (colonyID, procedureID)
    )
    ENGINE=InnoDB DEFAULT CHARSET=latin1;
--
--
INSERT
INTO mutant_procedures
    (
        colonyID,
        PROCEDUREID,
        mutants_per_procedureID
    )
SELECT experiment_specimen.COLONYID,
    experiment_specimen.PROCEDUREID,
    COUNT(1) AS mutants_per_procedureID
FROM experiment_specimen
WHERE experiment_specimen.COLONYID IS NOT NULL
AND NOT experiment_specimen.ISBASELINE
GROUP BY experiment_specimen.COLONYID,
    PROCEDUREID