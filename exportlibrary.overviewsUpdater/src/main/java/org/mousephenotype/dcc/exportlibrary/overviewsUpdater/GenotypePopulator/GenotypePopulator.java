/**
 * Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
 *
 * MEDICAL RESEARCH COUNCIL UK MRC
 *
 * Harwell Mammalian Genetics Unit
 *
 * http://www.har.mrc.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.dcc.exportlibrary.overviewsUpdater.GenotypePopulator;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.type.IntegerType;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempt;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class GenotypePopulator {
private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GenotypePopulator.class);
    private final List<PhenotypeAttempt> phenotypeAttempts;
    private final HibernateManager hibernateManager;
    private static final String CENTRE_ID_QUERY = "SELECT centre.centre_id  as id FROM phenodcc_overviews.centre WHERE centre.imits_name = :productionCentre";
    private static final String STRAIN_ID_QUERY = "SELECT strain.strain_id  as id FROM phenodcc_overviews.strain WHERE     strain.strain = :colonyBackgroundStrain";
    private static final String GENOTYPE_COUNT_QUERY = "SELECT COUNT(*) FROM phenodcc_overviews.genotype";
    Table<String, Class, Object> parameters;
    Map<String, org.hibernate.type.Type> scalars = ImmutableMap.<String, org.hibernate.type.Type>builder().put("id", IntegerType.INSTANCE).build();

    public GenotypePopulator(HibernateManager hibernateManager, List<PhenotypeAttempt> phenotypeAttempts) {
        this.hibernateManager = hibernateManager;
        this.phenotypeAttempts = phenotypeAttempts;

        parameters = HashBasedTable.create();

    }

    
    public long getGenotypeCount(){
        return ((BigInteger)this.hibernateManager.nativeQuery(GENOTYPE_COUNT_QUERY).get(0)).longValue();
        
    }
    
    private String composeUpdate(PhenotypeAttempt phenotypeAttempt, int centre_id, int strain_id) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO phenodcc_overviews.genotype  (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (");
        sb.append(phenotypeAttempt.getImitsID());
        sb.append(",");
        sb.append("'");
        sb.append(phenotypeAttempt.getPhenotypeColonyName());
        sb.append("'");
        sb.append(",");
        sb.append("'");
        sb.append(phenotypeAttempt.getGenotypicInformation().getMouseAlleleSymbol());
        sb.append("'");
        sb.append(",");
        if (phenotypeAttempt.getProductionCentre() != null) {
            sb.append(this.getCentreID(phenotypeAttempt.getProductionCentre().toString()));
        } else {
            sb.append(-1);
        }
        sb.append(",");
        if (phenotypeAttempt.getGenotypicInformation().getPhenotypeColonyBackgroundStrainName() != null) {
            sb.append(this.getStrainID(phenotypeAttempt.getGenotypicInformation().getPhenotypeColonyBackgroundStrainName()));
        } else {
            sb.append(-1);
        }
        sb.append(",");
        sb.append("'");
        sb.append(phenotypeAttempt.getGenotypicInformation().getGeneMGIID());
        sb.append("'");
        sb.append(",");
        sb.append("'");
        sb.append(phenotypeAttempt.getGenotypicInformation().getMarkerSymbol());
        sb.append("'");
        sb.append(")");
        logger.info("query {}",sb.toString());
        return sb.toString();
    }

    public Integer getCentreID(String productionCentre) {
        parameters.clear();
        parameters.put("productionCentre", String.class, productionCentre);
        List<Integer> nativeQuery = this.hibernateManager.nativeQuery(CENTRE_ID_QUERY, scalars, parameters);
        if (nativeQuery != null && nativeQuery.size() > 0) {
            return nativeQuery.get(0);
        } else {
            logger.error("no centre_id found for productionCentre {}", productionCentre);
            return -1;
        }
    }

    public Integer getStrainID(String colonyBackgroundStrain) {
        parameters.clear();
        parameters.put("colonyBackgroundStrain", String.class, colonyBackgroundStrain);
        List<Integer> nativeQuery = this.hibernateManager.nativeQuery(STRAIN_ID_QUERY, scalars, parameters);
        if (nativeQuery != null && nativeQuery.size() > 0) {
            return nativeQuery.get(0);
        } else {
            logger.error("no strain_id found for colonyBackgroundStrain {}", colonyBackgroundStrain);
            return -1;
        }
    }

    public List<String> getUpdates() {
        List<String> updates = new ArrayList<>(this.phenotypeAttempts.size() + 1);
        String composeUpdate = null;
        for (PhenotypeAttempt phenotypeAttempt : this.phenotypeAttempts) {
            composeUpdate = this.composeUpdate(phenotypeAttempt, -1, -1);
            updates.add(composeUpdate);
        }
        return updates;
    }
}
