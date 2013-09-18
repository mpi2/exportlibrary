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

/*SELECT sub.experimentHJID,
    EXPERIMENT_SPECIMENID.HJVALUE as specimenID
    SPECIMEN.SPECIMENID
  */
  
  select SPECIMEN.SPECIMENID,  
    
    from phenodcc_raw_valid.valid_experiments join EXPERIMENT_SPECIMENID  on EXPERIMENT_SPECIMENID.HJID = phenodcc_raw_valid.valid_experiments.EXPERIMENT_VALIDATIONREPORT__0
    
/*FROM ( SELECT MAX(VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0) AS experimentHJID
        FROM VALIDATIONREPORT
        WHERE VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0 IS NOT NULL
        AND VALIDATIONREPORT.ISVALID
        GROUP BY VALIDATIONREPORT.REPORTIDENTIFIER) AS sub

JOIN EXPERIMENT_SPECIMENID ON sub.experimentHJID = EXPERIMENT_SPECIMENID.HJID
*/
JOIN SPECIMEN              ON EXPERIMENT_SPECIMENID.HJVALUE = SPECIMEN.SPECIMENID
JOIN
phenodcc_raw_valid.valid_specimens on SPECIMEN.HJID = phenodcc_raw_valid.valid_specimens.SPECIMEN_VALIDATIONREPORT_HJ_0


    /*( SELECT MAX(VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0) AS specimenHJID
        FROM VALIDATIONREPORT
        WHERE VALIDATIONREPORT.ISVALID
        AND VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0 IS NOT NULL
        GROUP BY VALIDATIONREPORT.REPORTIDENTIFIER ) SUB2 ON SUB2.specimenHJID = SPECIMEN.HJID
    */
    
    
    
    
    
    
    
    
    
    
    
    --WHERE SPECIMEN.COLONYID = 'GSF-HEPD0550_6_G09-1-1'