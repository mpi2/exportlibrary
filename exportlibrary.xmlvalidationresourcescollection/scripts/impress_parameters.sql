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
FROM phenodcc_xmlvalidationresources.IMPRESSPIPELINE join phenodcc_xmlvalidationresources.IMPRESSPROCEDURE on IMPRESSPIPELINE.HJID = IMPRESSPROCEDURE.IMPRESSPROCEDURE_IMPRESSPIPE_0
                     join phenodcc_xmlvalidationresources.IMPRESSPARAMETER on IMPRESSPROCEDURE.HJID = IMPRESSPARAMETER.IMPRESSPARAMETER_IMPRESSPROC_0
                left join phenodcc_xmlvalidationresources.IMPRESSPARAMETEROPTION on IMPRESSPARAMETER.HJID = IMPRESSPARAMETEROPTION.IMPRESSPARAMETEROPTION_IMPRE_0
                left join phenodcc_xmlvalidationresources.IMPRESSPARAMETERINCREMENT on IMPRESSPARAMETER.HJID = IMPRESSPARAMETERINCREMENT.IMPRESSPARAMETERINCREMENT_IM_0
                
                
 where IMPRESSPROCEDURE.PROCEDUREKEY = 'IMPC_PAT_001'
 
 
 
 --IMPRESSPARAMETER.TYPE_ = 'SERIES_PARAMETER'
 
 --and
 --IMPRESSPARAMETER.PARAMETERKEY = 'IMPC_CAL_002_001'
 
 