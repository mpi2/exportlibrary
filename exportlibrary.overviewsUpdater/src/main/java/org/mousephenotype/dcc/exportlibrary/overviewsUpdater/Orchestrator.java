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
package org.mousephenotype.dcc.exportlibrary.overviewsUpdater;

import java.util.List;
import javax.mail.MessagingException;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.overviewsUpdater.GenotypePopulator.GenotypePopulator;
import org.mousephenotype.dcc.exportlibrary.overviewsUpdater.imitswsclient.ImitsWSClient;
import org.mousephenotype.dcc.exportlibrary.overviewsUpdater.xmlvalidationresources.Updater;
import org.mousephenotype.dcc.exportlibrary.rawReader.ColonyIDsExtractor;
import org.mousephenotype.dcc.utils.net.email.EMAILUtils;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;

public class Orchestrator {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(Orchestrator.class);
    private final HibernateManager hibernateManager;
    private final HibernateManager validationHibernateManager;
    private final ColonyIDsExtractor colonyIDsExtractor;
    private final ImitsWSClient imitsWSClient;
    private final Updater updater;
    private GenotypePopulator genotypePopulator;
    private static final String TRUNCATE_GENOTYPE = "TRUNCATE TABLE phenodcc_overviews.genotype ;";
    private static final String ZERO_ROW = "INSERT INTO phenodcc_overviews.genotype  (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (0, 'baseline', '', -1, -1, '', '');";

    public Orchestrator(HibernateManager hibernateManager, HibernateManager validationHibernateManager, String username, String password) {
        this.hibernateManager = hibernateManager;
        this.validationHibernateManager = validationHibernateManager;
        this.colonyIDsExtractor = new ColonyIDsExtractor(hibernateManager);
        this.updater = new Updater(validationHibernateManager);

        imitsWSClient = new ImitsWSClient(username, password);

    }

    private void sendMessage(String content, String subject) {
        EMAILUtils eMAILUtils = EMAILUtils.getEMAILUtils();
        try {
            eMAILUtils.sendEmail(new String[]{"developers@har.mrc.ac.uk"}, null, null, "dcc.logging@har.mrc.ac.uk", subject, content, null);
        } catch (MailException | MessagingException ex) {
            logger.error("error sending email", ex);

        }
    }

    private String getMessage(long genotypeCount) {
        StringBuilder content = new StringBuilder();
        content.append("run on ");
        content.append(DatatypeConverter.printDateTime(DatatypeConverter.now()));
        content.append(" returned ");
        if (!this.imitsWSClient.getPhenotypeAttemtps().isEmpty()) {
            content.append(this.imitsWSClient.getPhenotypeAttemtps().size());
            content.append(" phenotype attempts");
        } else {
            content.append("no phenotype attempts loaded");
        }

        content.append(genotypeCount);
        content.append(" existing genotype entries");
        //can do this with logback instead of the library...
        return content.toString();
    }

    public void run() {
        logger.info("quering colonyIDs");
        List<String> colonyIDs = this.colonyIDsExtractor.getColonyIDs();
        logger.info("connecting to imits");
        this.imitsWSClient.loadPhenotypeAttemtps(colonyIDs);
        this.genotypePopulator = new GenotypePopulator(this.hibernateManager, this.imitsWSClient.getPhenotypeAttemtps());
        long genotypeCount = this.genotypePopulator.getGenotypeCount();
        genotypeCount--;



        if (this.imitsWSClient.isSetPhenotypeAttempts()
                && genotypeCount <= this.imitsWSClient.getPhenotypeAttemtps().size()) {
            //
            logger.info("loaded {} phenotypeattempts", this.imitsWSClient.getPhenotypeAttemtps().size());
            logger.info("generating inserts");
            //
            List<String> updates = this.genotypePopulator.getUpdates();
            this.hibernateManager.nativeExecution(TRUNCATE_GENOTYPE);
            this.hibernateManager.nativeExecution(ZERO_ROW);
            for (String update : updates) {
                logger.info("executing {}", update);
                this.hibernateManager.nativeExecution(update);
            }
            this.updater.run(this.imitsWSClient.getPhenotypeAttemtps(),this.imitsWSClient.getmIPlans());
            this.sendMessage(this.getMessage(genotypeCount), "valid genotype update");


        } else {
            if (!this.imitsWSClient.isSetPhenotypeAttempts()) {
                logger.error("empty set retrieved from imits");
            } else {
                logger.error("{}phenotype attempts retrieved. {} existing genotype entries", this.imitsWSClient.getPhenotypeAttemtps().size(), genotypeCount);
            }

            this.sendMessage(this.getMessage(genotypeCount), "error populating genotype update");

        }

    }
}
