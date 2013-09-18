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
 
 
SELECT phenotype_attempts.id as phenotypeAttemptID,
phenotype_attempts.colony_name as colonyID, 
genes.marker_symbol as markerSymbol,
 genes.mgi_accession_id as mgiID,

phenotype_attempt_statuses.name as imitsPhenotypeStatus,  
concat(genes.marker_symbol,"<sup>",replace (es_cells.allele_symbol_superscript_template,"@",phenotype_attempts.mouse_allele_type),"</sup>") as `mouseAlleleSymbol`
 
 FROM phenotype_attempts join phenodcc_imits.phenotype_attempt_statuses on phenotype_attempts.status_id  = phenotype_attempt_statuses.id
                         join phenodcc_imits.mi_plans                   on phenotype_attempts.mi_plan_id = mi_plans.id
                         join phenodcc_imits.genes                      on mi_plans.gene_id              = genes.id
                         join phenodcc_imits.es_cells                   on genes.id                      = es_cells.gene_id
                         join phenodcc_imits.centres                    on mi_plans.production_centre_id = centres.id
 where 
        phenotype_attempt_statuses.name             in ( "Cre Excision Complete", "Phenotype started", "Phenotyping finished")
    and phenotype_attempts.mouse_allele_type        is not null
    and es_cells.allele_symbol_superscript_template is not null


union


 
SELECT phenotype_attempts.id as phenotypeAttemptID,
phenotype_attempts.colony_name as colonyID, 
genes.marker_symbol as markerSymbol,
 genes.mgi_accession_id as mgiID,

phenotype_attempt_statuses.name as imitsPhenotypeStatus, 
 
concat(genes.marker_symbol,"<sup>",replace (es_cells.allele_symbol_superscript_template,"@",es_cells.allele_type),"</sup>")as `mouseAlleleSymbol`

 FROM phenotype_attempts join phenodcc_imits.phenotype_attempt_statuses on phenotype_attempts.status_id  = phenotype_attempt_statuses.id
                         join phenodcc_imits.mi_plans                   on phenotype_attempts.mi_plan_id = mi_plans.id
                         join phenodcc_imits.genes                      on mi_plans.gene_id              = genes.id
                         join phenodcc_imits.es_cells                   on genes.id                      = es_cells.gene_id
 where 
        phenotype_attempt_statuses.name             not in ( "Cre Excision Complete", "Phenotype started", "Phenotyping finished")
    and phenotype_attempts.mouse_allele_type        is  null and es_cells.allele_type is not null
    and es_cells.allele_symbol_superscript_template is not null
    
 
 union

 
SELECT phenotype_attempts.id as phenotypeAttemptID,

phenotype_attempts.colony_name as colonyID, 
genes.marker_symbol  as markerSymbol,
 genes.mgi_accession_id as mgiID,

phenotype_attempt_statuses.name as imitsPhenotypeStatus, 
 
concat(genes.marker_symbol,"<sup>",replace (es_cells.allele_symbol_superscript_template,"@",es_cells.allele_type),"</sup>") as `mouseAlleleSymbol`

 FROM phenotype_attempts join phenodcc_imits.phenotype_attempt_statuses on phenotype_attempts.status_id  = phenotype_attempt_statuses.id
                         join phenodcc_imits.mi_plans                   on phenotype_attempts.mi_plan_id = mi_plans.id
                         join phenodcc_imits.genes                      on mi_plans.gene_id              = genes.id
                         join phenodcc_imits.es_cells                   on genes.id                      = es_cells.gene_id
 where 
     phenotype_attempt_statuses.name             in ( "Cre Excision Complete", "Phenotype started", "Phenotyping finished")
     and phenotype_attempts.cre_excision_required
    and phenotype_attempts.mouse_allele_type        is  null and es_cells.allele_type is not null
    and es_cells.allele_symbol_superscript_template is not null