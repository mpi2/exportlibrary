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

select SUBMISSION.SUBMISSIONDATE, SUBMISSION.TRACKERID, ext.CENTREID, SPECIMEN.SPECIMENID, RELATEDSPECIMEN.SPECIMENID as related_specimenid, RELATEDSPECIMEN.RELATIONSHIP

from phenodcc_raw.SUBMISSION JOIN phenodcc_raw.CENTRESPECIMEN as ext on ext.CENTRESPECIMEN_SUBMISSION_HJ_0 = SUBMISSION.HJID
                             JOIN phenodcc_context.context on ext.HJID = context.subject
                             JOIN phenodcc_raw.SPECIMEN  ON SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0 = ext.HJID
                             JOIN phenodcc_raw.RELATEDSPECIMEN on RELATEDSPECIMEN.RELATEDSPECIMEN_SPECIMEN_HJID = SPECIMEN.HJID
                             
                             
                             
 where context.isActive  and 
        context.isValid  and
 not SPECIMEN.ISBASELINE and 
 
 RELATEDSPECIMEN.SPECIMENID in (
 
 SELECT  SPECIMEN.SPECIMENID
 
 FROM phenodcc_raw.CENTRESPECIMEN 
                             JOIN phenodcc_context.context on CENTRESPECIMEN.HJID = context.subject
                             JOIN phenodcc_raw.SPECIMEN  ON SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0 = CENTRESPECIMEN.HJID
 
 where context.isActive  and 
        context.isValid  and
  SPECIMEN.ISBASELINE    and
  CENTRESPECIMEN.CENTREID = ext.CENTREID  )
  
 
 

  
 
 
 



