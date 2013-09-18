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

set @date_range :=  "2013-04-17 19:25:37";
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

select CENTREPROCEDURE.CENTREID, MESSAGE, 'experimentID', isValid, context.isActive


FROM phenodcc_raw.SUBMISSION JOIN phenodcc_raw.CENTREPROCEDURE ON (CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID)
                           
                             join phenodcc_raw.VALIDATION       on CENTREPROCEDURE.HJID =  CENTREPROCEDURE_VALIDATION_H_0
                             join phenodcc_context.context      on CENTREPROCEDURE.HJID =  context.subject

where  submissiondate > @date_range
and CENTREPROCEDURE.CENTREID = @center
and context.isActive 

GROUP BY  MESSAGE

UNION

select CENTREPROCEDURE.CENTREID, MESSAGE, EXPERIMENT.EXPERIMENTID, isValid, context.isActive

FROM phenodcc_raw.SUBMISSION JOIN phenodcc_raw.CENTREPROCEDURE ON (CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID)
 join phenodcc_raw.EXPERIMENT                on CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
                                 
                               join phenodcc_raw.VALIDATION                 on EXPERIMENT.HJID = EXPERIMENT_VALIDATION_HJID
                               join phenodcc_context.context on EXPERIMENT.HJID  =  context.subject
where  submissiondate > @date_range
and CENTREPROCEDURE.CENTREID = @center
and context.isActive 
GROUP BY CENTREPROCEDURE.CENTREID, MESSAGE


UNION

select CENTREPROCEDURE.CENTREID, MESSAGE, EXPERIMENT.EXPERIMENTID, isValid, context.isActive


FROM phenodcc_raw.SUBMISSION JOIN phenodcc_raw.CENTREPROCEDURE ON (CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID)

join phenodcc_raw.EXPERIMENT                on CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
                                   join phenodcc_raw.PROCEDURE_                on EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
                            
                                 join phenodcc_raw.VALIDATION                 on PROCEDURE_.HJID = VALIDATION.PROCEDURE__VALIDATION_HJID
                                 join phenodcc_context.context on PROCEDURE_.HJID  =  context.subject
where  submissiondate > @date_range
and CENTREPROCEDURE.CENTREID = @center
and context.isActive 

GROUP BY CENTREPROCEDURE.CENTREID, MESSAGE

UNION

select CENTREPROCEDURE.CENTREID, MESSAGE, EXPERIMENT.EXPERIMENTID, isValid, context.isActive

FROM phenodcc_raw.SUBMISSION JOIN phenodcc_raw.CENTREPROCEDURE ON (CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID)
   join phenodcc_raw.EXPERIMENT                on CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
                                   join phenodcc_raw.PROCEDURE_                on EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
                                   join phenodcc_raw.SIMPLEPARAMETER           on PROCEDURE_.HJID = SIMPLEPARAMETER.SIMPLEPARAMETER_PROCEDURE__H_0
                            
                                   join phenodcc_raw.VALIDATION                 on SIMPLEPARAMETER.HJID = VALIDATION.SIMPLEPARAMETER_VALIDATION_H_0
                                   join phenodcc_context.context on SIMPLEPARAMETER.HJID  =  context.subject
where  submissiondate > @date_range
and CENTREPROCEDURE.CENTREID = @center
and context.isActive 

GROUP BY CENTREPROCEDURE.CENTREID, MESSAGE


UNION

select CENTREPROCEDURE.CENTREID, MESSAGE, EXPERIMENT.EXPERIMENTID, isValid, context.isActive

FROM phenodcc_raw.SUBMISSION JOIN phenodcc_raw.CENTREPROCEDURE ON (CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID)
  join phenodcc_raw.EXPERIMENT                on CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
                                   join phenodcc_raw.PROCEDURE_                on EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
                            
                                   join phenodcc_raw.ONTOLOGYPARAMETER         on PROCEDURE_.HJID = ONTOLOGYPARAMETER.ONTOLOGYPARAMETER_PROCEDURE__0
                            
                                   join phenodcc_raw.VALIDATION                 on ONTOLOGYPARAMETER.HJID = VALIDATION.ONTOLOGYPARAMETER_VALIDATION_0
                                   join phenodcc_context.context on ONTOLOGYPARAMETER.HJID  =  context.subject
where  submissiondate > @date_range
and CENTREPROCEDURE.CENTREID = @center
and context.isActive 

GROUP BY CENTREPROCEDURE.CENTREID, MESSAGE


UNION

select CENTREPROCEDURE.CENTREID, MESSAGE , EXPERIMENT.EXPERIMENTID, isValid, context.isActive

FROM phenodcc_raw.SUBMISSION JOIN phenodcc_raw.CENTREPROCEDURE ON (CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID)
  join phenodcc_raw.EXPERIMENT                on CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
                                   join phenodcc_raw.PROCEDURE_                on EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
                            
                                   join  phenodcc_raw.SERIESPARAMETER         on PROCEDURE_.HJID = SERIESPARAMETER.SERIESPARAMETER_PROCEDURE__H_0
                                                                             
                                   join phenodcc_raw.VALIDATION                 on  SERIESPARAMETER.HJID = VALIDATION.SERIESPARAMETER_VALIDATION_H_0
                                   join phenodcc_context.context on SERIESPARAMETER.HJID  =  context.subject
where  submissiondate > @date_range
and CENTREPROCEDURE.CENTREID = @center
and context.isActive 

GROUP BY CENTREPROCEDURE.CENTREID, MESSAGE


UNION

select CENTREPROCEDURE.CENTREID, MESSAGE, EXPERIMENT.EXPERIMENTID, isValid, context.isActive

FROM phenodcc_raw.SUBMISSION JOIN phenodcc_raw.CENTREPROCEDURE ON (CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID)
 join phenodcc_raw.EXPERIMENT                 on CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
                                   join phenodcc_raw.PROCEDURE_                 on EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID

                                   join  phenodcc_raw.SERIESPARAMETER            on PROCEDURE_.HJID = SERIESPARAMETER.SERIESPARAMETER_PROCEDURE__H_0
                                   join phenodcc_raw.SERIESPARAMETERVALUE        on   SERIESPARAMETER.HJID = SERIESPARAMETERVALUE.VALUE__SERIESPARAMETER_HJID
                           
                                   join phenodcc_raw.VALIDATION                 on SERIESPARAMETERVALUE.HJID = VALIDATION.SERIESPARAMETERVALUE_VALIDAT_0
                                   join phenodcc_context.context on SERIESPARAMETERVALUE.HJID  =  context.subject

where  submissiondate > @date_range
and CENTREPROCEDURE.CENTREID = @center
and context.isActive 

GROUP BY CENTREPROCEDURE.CENTREID, MESSAGE


union

select CENTREPROCEDURE.CENTREID, MESSAGE , EXPERIMENT.EXPERIMENTID, isValid, context.isActive

FROM phenodcc_raw.SUBMISSION JOIN phenodcc_raw.CENTREPROCEDURE ON (CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0 = SUBMISSION.HJID)
  join phenodcc_raw.EXPERIMENT                on CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
                                   join phenodcc_raw.PROCEDURE_                on EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
                            
                                                                             
                              join phenodcc_raw.PROCEDUREMETADATA          on PROCEDURE_.HJID = PROCEDUREMETADATA_PROCEDURE__0
                           
                           
                              join phenodcc_raw.VALIDATION                 on PROCEDUREMETADATA.HJID = VALIDATION.PROCEDUREMETADATA_VALIDATION_0
                              join phenodcc_context.context on PROCEDUREMETADATA.HJID =  context.subject

where  submissiondate > @date_range
and CENTREPROCEDURE.CENTREID = @center
and context.isActive 

GROUP BY CENTREPROCEDURE.CENTREID, MESSAGE