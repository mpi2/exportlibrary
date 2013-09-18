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
package org.mousephenotype.dcc.exportlibrary.rawReader;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.hibernate.type.StringType;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColonyIDsExtractor {

    private static final Logger logger = LoggerFactory.getLogger(ColonyIDsExtractor.class);
    
    private HibernateManager hibernateManager;
    private List<String> colonyIDs = null;
    private boolean loaded = false;
    private static final String DISTINCT_COLONYIDS_QUERY= "SELECT DISTINCT (COLONYID) as colonyID FROM SPECIMEN WHERE COLONYID IS NOT NULL AND COLONYID != 'baseline'";

    public ColonyIDsExtractor(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    public List<String> getColonyIDs() {
        if (!loaded) {
            this.loadColonyIDs();
            loaded = true;
        }

        return colonyIDs;
    }

    private void loadColonyIDs() {
        
        Map<String, org.hibernate.type.Type> scalars = ImmutableMap.<String, org.hibernate.type.Type>builder().put("colonyID", StringType.INSTANCE).build();
        this.colonyIDs = this.hibernateManager.nativeQuery(DISTINCT_COLONYIDS_QUERY, scalars);
        logger.info("retrieved {} colonyIDs",this.colonyIDs.size());
    }
}
