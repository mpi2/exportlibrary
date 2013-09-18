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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.collectionmodifiers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;

import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.*;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.mgi.MGIStrain;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.mgi.MgiStrains;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class DerivedGenotypicInformationGenerator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DerivedGenotypicInformationGenerator.class);
    private static final String PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external";
    //
    public static final Pattern eucomPatern = Pattern.compile("(.*tm[0-9])[a-z](.*)");
    public static final Pattern kompPatern = Pattern.compile("(.*tm[0-9])(.*)");
    //
    private Reader reader;
    private HibernateManager hibernateManager;
    //
    private PhenotypeAttempts PhenotypeAttempts;
    private MicroinjectionAttempts MicroinjectionAttempts;
    private MiPlans miPlans;
    private MgiStrains mgiStrains;
    

    public MiPlans getMiPlans() {
        return miPlans;
    }

    public void setMiPlans(MiPlans miPlans) {
        this.miPlans = miPlans;
    }
    

    public HibernateManager getHibernateManager() {
        return this.hibernateManager;
    }

    private void alterDatabaseProperties(Properties properties) {
        properties.remove("hibernate.hbm2ddl.auto");
        if (((String) properties.get("hibernate.connection.url")).contains("derby")) {
            String hibernateConnectionUrl = (String) properties.get("hibernate.connection.url");
            properties.put("hibernate.connection.url", hibernateConnectionUrl.substring(0, hibernateConnectionUrl.lastIndexOf(";")));
            //
            String derbySystemHome = DerivedGenotypicInformationGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            System.setProperty("derby.system.home", derbySystemHome.substring(0, derbySystemHome.lastIndexOf(File.separator)));
        }
    }

    public DerivedGenotypicInformationGenerator(String propertiesFilename) throws FileNotFoundException, IOException, HibernateException, IndexOutOfBoundsException, ConfigurationException {
        reader = new Reader(propertiesFilename);
        Properties properties = reader.getProperties();
        this.alterDatabaseProperties(properties);
        hibernateManager = new HibernateManager(properties, DerivedGenotypicInformationGenerator.PERSISTENCEUNITNAME);
        this.setup();
    }

    public DerivedGenotypicInformationGenerator() {
    }

    private void setup() throws IndexOutOfBoundsException {
        //order matters, first load the phenotype, then just the microinjection attempts for this phenotype attempts
        this.loadPhenotypeAttempts();
        this.loadMicroinjectionAttempts();
        this.loadMIPlans();
        this.loadMGI();
    }

    public void run() throws FileNotFoundException, IOException {
        this.matchMGIIDs();
        this.assignMGIIDs();

    }

    /*
     * Hughs' ok
     */
    private void matchMGIIDs() {

        MGIStrain mGIStrain = null;
        logger.info("{} total phenotypic attempts", getPhenotypeAttempts().getPhenotypeAttempt().size());
        int i = 0;
        for (PhenotypeAttempt imitsPhenotypeAttempt : getPhenotypeAttempts().getPhenotypeAttempt()) {
            if ((mGIStrain = this.getMGIStrain(imitsPhenotypeAttempt.getGenotypicInformation().getPhenotypeColonyBackgroundStrainName())) != null) {
                imitsPhenotypeAttempt.getGenotypicInformation().setColonyBackgroundStrainMGIID(mGIStrain.getMGIID());
                i++;
            }
        }
        logger.info("{} matches", i);
    }

    public void close() throws TransactionRequiredException, PersistenceException, HibernateException {
        hibernateManager.alter();
        hibernateManager.close();
    }

    private void loadMIPlans() {
        List<MIPlan> query = hibernateManager.query("from MIPlan where id in (select miPlanID from PhenotypeAttempt) ", MIPlan.class);
        this.miPlans = new MiPlans();
        this.miPlans.getMiPlan().addAll(query);
    }

    private void loadPhenotypeAttempts() throws IndexOutOfBoundsException {
        setPhenotypeAttempts(hibernateManager.query("from PhenotypeAttempts", PhenotypeAttempts.class).get(0));
    }

    private void loadMGI() {
        setMgiStrains(hibernateManager.query("from MgiStrains", MgiStrains.class).get(0));
    }

    private void loadMicroinjectionAttempts() throws IndexOutOfBoundsException {
        String query = "from MicroinjectionAttempt where colonyName  in (:microInjectionAttemptColonyNames)";
        Map<String, Object> parameters = new HashMap<>();
        List<String> microInjectionAttemptColonyNames = new ArrayList<>();
        for (PhenotypeAttempt imitsPhenotypeAttempt : this.getPhenotypeAttempts().getPhenotypeAttempt()) {
            microInjectionAttemptColonyNames.add(imitsPhenotypeAttempt.getGenotypicInformation().getMicroInjectionAttemptColonyName());
        }

        parameters.put("microInjectionAttemptColonyNames", microInjectionAttemptColonyNames);

        List<MicroinjectionAttempt> registeredMicroinjectionAttempts = hibernateManager.query(query, parameters, MicroinjectionAttempt.class);

        this.setMicroinjectionAttempts(new MicroinjectionAttempts());
        this.MicroinjectionAttempts.getMicroinjectionAttempt().addAll(registeredMicroinjectionAttempts);
    }

    private MGIStrain getMGIStrain(String phenotypeColonyBackgroundStrainName) {
        for (MGIStrain mGIStrain : this.getMgiStrains().getMgiStrain()) {
            if (mGIStrain.getStrainID().equals(phenotypeColonyBackgroundStrainName)) {
                return mGIStrain;
            }
        }
        return null;
    }

    /**
     * @return the PhenotypeAttempts
     */
    public PhenotypeAttempts getPhenotypeAttempts() {
        return PhenotypeAttempts;
    }

    /**
     * @return the MicroinjectionAttempts
     */
    public MicroinjectionAttempts getMicroinjectionAttempts() {
        return MicroinjectionAttempts;
    }

    /**
     * @return the mgiStrains
     */
    public MgiStrains getMgiStrains() {
        return mgiStrains;
    }

    /**
     * @param PhenotypeAttempts the PhenotypeAttempts to set
     */
    public void setPhenotypeAttempts(PhenotypeAttempts PhenotypeAttempts) {
        this.PhenotypeAttempts = PhenotypeAttempts;
    }

    /**
     * @param MicroinjectionAttempts the MicroinjectionAttempts to set
     */
    public void setMicroinjectionAttempts(MicroinjectionAttempts MicroinjectionAttempts) {
        this.MicroinjectionAttempts = MicroinjectionAttempts;
    }

    /**
     * @param mgiStrains the mgiStrains to set
     */
    public void setMgiStrains(MgiStrains mgiStrains) {
        this.mgiStrains = mgiStrains;
    }

    /**
     * @param hibernateManager the hibernateManager to set
     */
    public void setHibernateManager(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    private String getGeneMGIID(PhenotypeAttempt p) {
        if (this.getMiPlans() != null && this.getMiPlans().getMiPlan() != null) {
            for (MIPlan miPlan : this.getMiPlans().getMiPlan()) {
                if (miPlan.getId().equals(p.getMiPlanID())) {
                    return miPlan.getMarkerMGIaccessionID();
                }
            }
        } else {
            logger.error("there is no miplan for the phenotype attempt  imitsID {}", p.getImitsID());
        }
        return null;
    }

    private void assignMGIIDs() throws FileNotFoundException, IOException {
        for (PhenotypeAttempt phenotypeAttempt : this.getPhenotypeAttempts().getPhenotypeAttempt()) {
            if (phenotypeAttempt.getGenotypicInformation() != null) {
                phenotypeAttempt.getGenotypicInformation().setGeneMGIID(this.getGeneMGIID(phenotypeAttempt));
            } else {
                logger.error("there is no genotypic information for the phenotype attempt  imitsID {}", phenotypeAttempt.getImitsID());
            }
        }
    }
}
