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

SELECT MICROINJECTIONATTEMPT.PRODUCTIONCENTRENAME, count(*) as  successful_microinjections
FROM MICROINJECTIONATTEMPT, ADMIN.PHENOTYPEATTEMPT, ADMIN.GENOTYPICINFORMATION
where MICROINJECTIONATTEMPT.STATUSNAME = 'Genotype confirmed'
 and  GENOTYPICINFORMATION.HJID = PHENOTYPEATTEMPT.GENOTYPICINFORMATION_PHENOTY_0 and
 MICROINJECTIONATTEMPT.COLONYNAME = GENOTYPICINFORMATION.MICROINJECTIONATTEMPTCOLONYN_0
group by MICROINJECTIONATTEMPT.PRODUCTIONCENTRENAME
order by MICROINJECTIONATTEMPT.PRODUCTIONCENTRENAME
 
