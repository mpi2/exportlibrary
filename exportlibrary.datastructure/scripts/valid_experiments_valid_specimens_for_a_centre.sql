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

set @center :=  "H";
#set @center :=  "UCD";
#set @center :=  "BAY";
#set @center :=  "HMGU";
#set @center :=  "RBRC";
#set @center :=  "NING";
#set @center :=  "J";
#set @center :=  "GMC";
#set @center :=  "ICS";
#set @center :=  "NCOM";
#set @center :=  "WTSI";


select  CENTREPROCEDURE.CENTREID, EXPERIMENT.EXPERIMENTID, EXPERIMENT_SPECIMENID.HJVALUE
 
from phenodcc_raw.CENTREPROCEDURE JOIN phenodcc_raw.EXPERIMENT on CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
                                  JOIN phenodcc_raw.EXPERIMENT_SPECIMENID ON EXPERIMENT.HJID = EXPERIMENT_SPECIMENID.HJID
                                   JOIN phenodcc_context.context ON phenodcc_raw.EXPERIMENT.HJID = phenodcc_context.context.subject
where CENTREPROCEDURE.CENTREID = @center  AND 
phenodcc_context.context.isValid  AND

EXPERIMENT_SPECIMENID.HJVALUE IN (

SELECT distinct SPECIMEN.SPECIMENID
FROM CENTRESPECIMEN JOIN phenodcc_raw.SPECIMEN ON CENTRESPECIMEN.HJID =  SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0 
 JOIN phenodcc_context.context ON phenodcc_raw.SPECIMEN.HJID = phenodcc_context.context.subject
WHERE CENTRESPECIMEN.CENTREID = @center
AND SPECIMEN.ISBASELINE
and context.isValid)
