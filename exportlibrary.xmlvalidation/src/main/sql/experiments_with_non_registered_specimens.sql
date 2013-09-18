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

select *
from  
phenodcc_raw.SUBMISSION, 
phenodcc_raw.CENTREPROCEDURE, phenodcc_raw.EXPERIMENT, phenodcc_raw.EXPERIMENT_SPECIMENID

where 
CENTREPROCEDURE_SUBMISSION_H_0 = phenodcc_raw.SUBMISSION.HJID 
and
CENTREPROCEDURE.HJID =  EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0 

and EXPERIMENT.HJID = EXPERIMENT_SPECIMENID.HJID
and EXPERIMENT_SPECIMENID.HJVALUE  in 
(
SELECT  HJVALUE
FROM EXPERIMENT_SPECIMENID
where EXPERIMENT_SPECIMENID.HJVALUE not in (select SPECIMENID  from phenodcc_raw.SPECIMEN)
)