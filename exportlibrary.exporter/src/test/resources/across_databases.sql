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

--SELECT valid_experiments.*
--FROM  phenodcc_raw_valid.valid_experiments
--JOIN phenodcc_raw.EXPERIMENT_SPECIMENID on valid_experiments.HJID =  EXPERIMENT_SPECIMENID.HJID
--join valid_specimens on EXPERIMENT_SPECIMENID.HJVALUE = valid_specimens.SPECIMENID;

SELECT SPECIMEN.*, EMBRYO.*, MOUSE.*,
CASE 
    WHEN EMBRYO.HJID IS NOT NULL THEN 1
    WHEN MOUSE.HJID  IS NOT NULL THEN 2
END AS clazz_
 
FROM  SPECIMEN SPECIMEN LEFT OUTER JOIN  MOUSE   MOUSE ON SPECIMEN.HJID= MOUSE.HJID
                        LEFT OUTER JOIN  EMBRYO EMBRYO ON SPECIMEN.HJID= EMBRYO.HJID
join phenodcc_raw_valid.valid_specimens validspecimens on SPECIMEN.HJID = validspecimens.HJID;

