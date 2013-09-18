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
package org.mousephenotype.dcc.exportlibrary.traverser;

import com.google.common.collect.ImmutableMap;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

public class CentreSpecimenSetLoader {

    private String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private final HibernateManager hibernateManager;
    private Reader reader;

    public CentreSpecimenSetLoader(String hibernateProperties) throws ConfigurationException, HibernateException {
        this.reader = new Reader(hibernateProperties);
        this.hibernateManager = new HibernateManager(this.reader.getProperties(), PERSISTENCE_UNITNAME);
    }

    public CentreSpecimenSetLoader(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    public CentreSpecimenSet load(CentreILARcode centreILARcode) {
        String query = "from CentreSpecimen where centreID = :centreID";
        List<CentreSpecimen> centreSpecimens = this.hibernateManager.query(query,
                ImmutableMap.<String, Object>builder().put("centreID", centreILARcode).build(),
                CentreSpecimen.class);
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        centreSpecimenSet.getCentre().addAll(centreSpecimens);
        return centreSpecimenSet;
    }
    
    public CentreSpecimenSet load(CentreILARcode centreILARcode, Calendar submissionDate){
        String query = "select centreSpecimen from Submission submission join submission.centreSpecimen centreSpecimen where centreSpecimen.centreID = :centreID "
                + "and submission.submissionDate >= :submissionDate  ";        
        List<CentreSpecimen> centreSpecimens = this.hibernateManager.query(query,
                ImmutableMap.<String, Object>builder().put("centreID", centreILARcode).put("submissionDate", submissionDate).build(),
                CentreSpecimen.class);
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        centreSpecimenSet.getCentre().addAll(centreSpecimens);
        return centreSpecimenSet;
    }
    
     public CentreSpecimenSet load(){
        List<CentreSpecimen> centreSpecimens = this.hibernateManager.query("from CentreSpecimen", CentreSpecimen.class);
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        centreSpecimenSet.getCentre().addAll(centreSpecimens);
        return centreSpecimenSet;
    }
    
     
     public CentreSpecimenSet load(Calendar submissionDate){
        String query = "select centreSpecimen from Submission submission  join submission.centreSpecimen centreSpecimen where submission.submissionDate >= :submissionDate";
                
        List<CentreSpecimen> centreSpecimens = this.hibernateManager.query(query,
                ImmutableMap.<String, Object>builder().put("submissionDate", submissionDate).build(),
                CentreSpecimen.class);
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        centreSpecimenSet.getCentre().addAll(centreSpecimens);
        return centreSpecimenSet;
    }
     
}
