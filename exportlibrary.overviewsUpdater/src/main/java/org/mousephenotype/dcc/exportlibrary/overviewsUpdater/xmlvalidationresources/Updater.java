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
package org.mousephenotype.dcc.exportlibrary.overviewsUpdater.xmlvalidationresources;

import java.util.Calendar;
import java.util.List;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MIPlan;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MiPlans;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempt;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempts;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class Updater {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(Updater.class);
    private final HibernateManager hibernateManager;
    private final Calendar now;

    public Updater(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
        now = DatatypeConverter.now();
    }

    public void removePhenotypeAttempts() {
        List<PhenotypeAttempts> phenotypeAttemptsFromDB = this.hibernateManager.query("from PhenotypeAttempts", PhenotypeAttempts.class);
        for (PhenotypeAttempts phenotypeAttempts : phenotypeAttemptsFromDB) {
            this.hibernateManager.remove(phenotypeAttempts);
        }
        this.hibernateManager.getEntityManager().close();
    }

    public void removeMIPlans() {
        List<MiPlans> MiPlansFromDB = this.hibernateManager.query("from MiPlans", MiPlans.class);
        for (MiPlans MiPlans : MiPlansFromDB) {
            this.hibernateManager.remove(MiPlans);
        }
        this.hibernateManager.getEntityManager().close();
    }
    
    public void persistPhenotypeAttempts(List<PhenotypeAttempt> phenotypeAttemptsFromWS){
        PhenotypeAttempts phenotypeAttempts =new PhenotypeAttempts();
        phenotypeAttempts.setRetrieved(now);
        phenotypeAttempts.getPhenotypeAttempt().addAll(phenotypeAttemptsFromWS);
        this.hibernateManager.persist(phenotypeAttempts);
    }
    
    public void persistMiPlans(List<MIPlan> miPlanFromWS){
        MiPlans mIPlans =new MiPlans();
        mIPlans.setRetrieved(now);
        mIPlans.getMiPlan().addAll(miPlanFromWS);
        this.hibernateManager.persist(mIPlans);
    }
    
    public void run(List<PhenotypeAttempt> phenotypeAttemptsFromWS,List<MIPlan> miPlanFromWS){
        this.removeMIPlans();
        this.removePhenotypeAttempts();
        this.persistMiPlans(miPlanFromWS);
        this.persistPhenotypeAttempts(phenotypeAttemptsFromWS);
    }
}
