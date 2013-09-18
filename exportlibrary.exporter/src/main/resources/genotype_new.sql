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



select
PHENOTYPEATTEMPT.IMITSID as genotype_id,
 PHENOTYPEATTEMPT.PHENOTYPECOLONYNAME as genotype,
  GENOTYPICINFORMATION.MOUSEALLELESYMBOL as allele_name,
 centre.centre_id as centre_id,
 strain.strain_id as strain_id,
GENEMGIID as gene_id,
MARKERSYMBOL as gene_symbol

FROM  PHENOTYPEATTEMPT JOIN  GENOTYPICINFORMATION ON PHENOTYPEATTEMPT.GENOTYPICINFORMATION_PHENOTY_0 = GENOTYPICINFORMATION.HJID
  left join  centre on PHENOTYPEATTEMPT.PRODUCTIONCENTRE = centre.imits_name
 left join  strain on  GENOTYPICINFORMATION.PHENOTYPECOLONYBACKGROUNDSTR_0 = strain.strain