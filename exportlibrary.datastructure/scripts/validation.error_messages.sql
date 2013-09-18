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

select CENTREPROCEDURE.CENTREID, MESSAGE

from phenodcc_raw.CENTREPROCEDURE  join phenodcc_raw.EXPERIMENT                on CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
                                   join phenodcc_raw.PROCEDURE_                on EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
                              left join phenodcc_raw.SIMPLEPARAMETER           on PROCEDURE_.HJID = SIMPLEPARAMETER.SIMPLEPARAMETER_PROCEDURE__H_0
                              left join phenodcc_raw.ONTOLOGYPARAMETER         on PROCEDURE_.HJID = ONTOLOGYPARAMETER.ONTOLOGYPARAMETER_PROCEDURE__0
                              left join ( phenodcc_raw.SERIESPARAMETER   join phenodcc_raw.SERIESMEDIAPARAMETERVALUE 
                                                                               on SERIESPARAMETER.HJID = SERIESMEDIAPARAMETERVALUE.VALUE__SERIESMEDIAPARAMETER__0)
                                                                               on (PROCEDURE_.HJID = SERIESPARAMETER.SERIESPARAMETER_PROCEDURE__H_0)
                                                                               
                             left join phenodcc_raw.PROCEDUREMETADATA          on PROCEDURE_.HJID = PROCEDUREMETADATA_PROCEDURE__0
                             
                             
                             left join phenodcc_raw.VALIDATION                 on EXPERIMENT.HJID = EXPERIMENT_VALIDATION_HJID

GROUP BY CENTREPROCEDURE.CENTREID, MESSAGE