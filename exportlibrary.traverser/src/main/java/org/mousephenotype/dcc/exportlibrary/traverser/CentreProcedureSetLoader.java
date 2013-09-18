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
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

public class CentreProcedureSetLoader {
    private String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private final HibernateManager hibernateManager;
    private Reader reader;
    
    public CentreProcedureSetLoader(String hibernateProperties) throws ConfigurationException, HibernateException{
        this.reader = new Reader(hibernateProperties);
        this.hibernateManager = new HibernateManager(this.reader.getProperties(), PERSISTENCE_UNITNAME);
    }
    
    public CentreProcedureSetLoader(HibernateManager hibernateManager){
        this.hibernateManager = hibernateManager;
    }
    
    public CentreProcedureSet load(CentreILARcode centreILARcode){
        String query = "from CentreProcedure where centreID = :centreID";        
        List<CentreProcedure> centreProcedures = this.hibernateManager.query(query,
                ImmutableMap.<String, Object>builder().put("centreID", centreILARcode).build(),
                CentreProcedure.class);
        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        centreProcedureSet.getCentre().addAll(centreProcedures);
        return centreProcedureSet;
    }
    
    public CentreProcedureSet load(CentreILARcode centreILARcode, Calendar submissionDate){
        String query = "select centreProcedure from Submission submission   join submission.centreProcedure centreProcedure where centreProcedure.centreID = :centreID "
                + "and submission.submissionDate >= :submissionDate  ";        
        List<CentreProcedure> centreProcedures = this.hibernateManager.query(query,
                ImmutableMap.<String, Object>builder().put("centreID", centreILARcode).put("submissionDate", submissionDate).build(),
                CentreProcedure.class);
        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        centreProcedureSet.getCentre().addAll(centreProcedures);
        return centreProcedureSet;
    }
    
    public CentreProcedureSet load(){
        List<CentreProcedure> centreProcedures = this.hibernateManager.query("from CentreProcedure", CentreProcedure.class);
        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        centreProcedureSet.getCentre().addAll(centreProcedures);
        return centreProcedureSet;
    }
    
    public CentreProcedureSet load(Calendar submissionDate){
        String query = "select centreProcedure from Submission submission   join submission.centreProcedure centreProcedure where submission.submissionDate >= :submissionDate";
                
        List<CentreProcedure> centreProcedures = this.hibernateManager.query(query,
                ImmutableMap.<String, Object>builder().put("submissionDate", submissionDate).build(),
                CentreProcedure.class);
        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        centreProcedureSet.getCentre().addAll(centreProcedures);
        return centreProcedureSet;
    }
}
