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
JOIN phenodcc_raw.SUBMISSION      ON xml_file.id = SUBMISSION.TRACKERID
JOIN phenodcc_context.context     ON SUBMISSION.HJID = context.id
JOIN phenodcc_raw.CENTREPROCEDURE ON SUBMISSION.HJID = CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0
JOIN phenodcc_raw.EXPERIMENT      ON EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0 = CENTREPROCEDURE.HJID
JOIN phenodcc_raw.STATUSCODE      ON STATUSCODE.STATUSCODE_EXPERIMENT_HJID = EXPERIMENT.HJID

where 

STATUSCODE.VALUE_ not like 'IMPC_PSC_015:%' and 

STATUSCODE.VALUE_ not in (

select CODE
from phenodcc_xmlvalidationresources.STATUSCODE
)


