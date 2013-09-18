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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.imits;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.simple.parser.ParseException;

import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MiPlans;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MicroinjectionAttempts;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempts;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationremoteclients.imits.ImitsParser;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ImitsLocalLoader {

    private ImitsParser imitsParser;
    private final String jsonDocsRootFolder;
    private String microInjectionWildCard = "*microinjection_attempt.json";
    private String phenotypeAttemptWildCard = "*phenotype_attempt.json";
    private String miplanWildCard = "*miplan.json";
    private PhenotypeAttempts phenotypeAttempts = new PhenotypeAttempts();
    private MicroinjectionAttempts microinjectionAttempts = new MicroinjectionAttempts();
    private MiPlans miPlans = new MiPlans();
    private HibernateManager hibernateManager;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImitsLocalLoader.class);

    public String getMicroInjectionWildCard() {
        return microInjectionWildCard;
    }

    public void setMicroInjectionWildCard(String microInjectionWildCard) {
        this.microInjectionWildCard = microInjectionWildCard;
    }

    public String getPhenotypeAttemptWildCard() {
        return phenotypeAttemptWildCard;
    }

    public void setPhenotypeAttemptWildCard(String phenotypeAttemptWildCard) {
        this.phenotypeAttemptWildCard = phenotypeAttemptWildCard;
    }

    public String getMiplanWildCard() {
        return miplanWildCard;
    }

    public void setMiplanWildCard(String miplanWildCard) {
        this.miplanWildCard = miplanWildCard;
    }

    public ImitsLocalLoader(String jsonDocsRootFolder) throws NoSuchAlgorithmException, KeyManagementException {
        this.jsonDocsRootFolder = jsonDocsRootFolder;
        this.imitsParser = new ImitsParser();
    }

    private String[] loadFilesOnjsonDocsRootFolder(String wildcard) {
        File directory = new File(jsonDocsRootFolder);
        FilenameFilter fileFilter = new WildcardFileFilter(wildcard);
        return directory.list(fileFilter);
    }

    private String[] getMicroInjectionAttemptCentreRowDataFilenames() {
        return this.loadFilesOnjsonDocsRootFolder(microInjectionWildCard);
    }

    private String[] getPhenotypeAttemptCentreRowDataFilenames() {
        return this.loadFilesOnjsonDocsRootFolder(phenotypeAttemptWildCard);
    }

    private String[] getMiPlanCentreRowDataFilenames() {
        return this.loadFilesOnjsonDocsRootFolder(miplanWildCard);
    }

    private void serialize(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
        logger.info("persisting {} phenotype attempts", this.phenotypeAttempts.getPhenotypeAttempt().size());
        this.hibernateManager.persist(this.phenotypeAttempts);
        logger.info("persisting  {} microinjectionattempts", this.microinjectionAttempts.getMicroinjectionAttempt().size());
        this.hibernateManager.persist(this.microinjectionAttempts);
        logger.info("persisting {} miPlans", this.miPlans.getMiPlan().size());
        this.hibernateManager.persist(this.miPlans);
    }

    public void run(HibernateManager hibernateManager) {
        for (String filename : this.getMicroInjectionAttemptCentreRowDataFilenames()) {
            logger.info("parsing {}", jsonDocsRootFolder + File.separatorChar + filename);
            try {
                microinjectionAttempts.getMicroinjectionAttempt().addAll(this.imitsParser.getMicroinjectionAttempts(jsonDocsRootFolder + File.separatorChar + filename).getMicroinjectionAttempt());
            } catch (FileNotFoundException ex) {
                logger.error(" {}  not found", filename, ex);
            } catch (IOException ex) {
                logger.error("{} cannot be opened", filename, ex);
            } catch (ParseException ex) {
                logger.error("{} cannot be parsed", filename, ex);
            } catch (NullPointerException ex) {
                logger.error("{} cannot be parsed. Field required not found", filename, ex);
            }

        }
        for (String filename : this.getPhenotypeAttemptCentreRowDataFilenames()) {
            logger.info("parsing {}", jsonDocsRootFolder + File.separatorChar + filename);
            try {
                phenotypeAttempts.getPhenotypeAttempt().addAll(this.imitsParser.getPhenotypeAttempts(jsonDocsRootFolder + File.separatorChar + filename).getPhenotypeAttempt());
            } catch (FileNotFoundException ex) {
                logger.error(" {}  not found", filename, ex);
            } catch (IOException ex) {
                logger.error("{} cannot be opened", filename, ex);
            } catch (ParseException ex) {
                logger.error("{} cannot be parsed", filename, ex);
            } catch (NullPointerException ex) {
                logger.error("{} cannot be parsed. Field required not found", filename, ex);
            }
        }
        for (String filename : this.getMiPlanCentreRowDataFilenames()) {
            logger.info("parsing {}", jsonDocsRootFolder + File.separatorChar + filename);
            try {
                miPlans.getMiPlan().addAll(this.imitsParser.getMiPlans(jsonDocsRootFolder + File.separatorChar + filename).getMiPlan());
            } catch (FileNotFoundException ex) {
                logger.error(" {}  not found", filename, ex);
            } catch (IOException ex) {
                logger.error("{} cannot be opened", filename, ex);
            } catch (ParseException ex) {
                logger.error("{} cannot be parsed", filename, ex);
            } catch (NullPointerException ex) {
                logger.error("{} cannot be parsed. Field required not found", filename, ex);
            }
        }
        this.serialize(hibernateManager);
    }
}
