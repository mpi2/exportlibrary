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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.imits;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import org.apache.http.client.ClientProtocolException;
import org.hibernate.HibernateException;
import org.json.simple.parser.ParseException;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.Incarnator;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.*;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationremoteclients.imits.ImitsOracle;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

/**
 *
 * @author julian
 */
public class IMITSBrowser extends Incarnator {

    private Properties connectionProperties;
    private Map<String, PhenotypeAttempt> liveAttempts = new HashMap<>();
    private PhenotypeAttempts phenotypeAttemptsFromDB;

    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(Properties properties) {
        this.connectionProperties = properties;
    }

    public IMITSBrowser(String contextPath, String xmlFilename) throws Exception {
        super(contextPath, xmlFilename);
        this.initPhenotypeAttemptsFromDB();
    }

    public IMITSBrowser(HibernateManager hibernateManager) {
        super(hibernateManager);
        this.initPhenotypeAttemptsFromDB();
    }

    public IMITSBrowser(String persistenceUnitName, Properties properties) throws HibernateException {
        super(persistenceUnitName, properties);
        this.initPhenotypeAttemptsFromDB();
    }

    public boolean isInPhenotypeAttemptColonyName(CentreILARcode centreILARcode, String colonyID) {
        boolean result = false;

        PhenotypeAttempt phenotypeAttempt = null;
        if (this.liveAttempts.containsKey(colonyID)) {
            logger.info("{} is cached", colonyID);
            return true;
        }

        try {
            phenotypeAttempt = getPhenotypeAttemptColonyNameOnline(centreILARcode, colonyID);
        } catch (URISyntaxException | IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException | ParseException ex) {
            logger.error("", ex);
        }
        if (phenotypeAttempt != null && !this.toXml) {
            try {
                this.updateDatabase(phenotypeAttempt);
            } catch (Exception ex) {
                logger.error("error updating phenotype attempt {}", phenotypeAttempt.getImitsID(), ex);
            }
            result = true;
        }

        return result;

    }

    private PhenotypeAttempt getPhenotypeAttemptColonyNameOnline(CentreILARcode centreILARcode, String colonyID) throws URISyntaxException, IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, FileNotFoundException, ParseException {
        if (this.connectionProperties == null
                || this.connectionProperties.getProperty(IOParameters.IMITS_PROPERTIES.imitsUsername.getPropertyName()) == null
                || this.connectionProperties.getProperty(IOParameters.IMITS_PROPERTIES.imitsPassword.getPropertyName()) == null) {
            logger.error("credentials for imits not submitted");
        } else {
            ImitsOracle imitsOracle = new ImitsOracle(this.connectionProperties.getProperty(IOParameters.IMITS_PROPERTIES.imitsUsername.getPropertyName()), this.connectionProperties.getProperty(IOParameters.IMITS_PROPERTIES.imitsPassword.getPropertyName()));
            PhenotypeAttempt inPhenotypeAttemptColonyName = imitsOracle.isInPhenotypeAttemptColonyName(CentreTranslator.get(centreILARcode), colonyID);
            if (inPhenotypeAttemptColonyName != null) {
                this.liveAttempts.put(colonyID, inPhenotypeAttemptColonyName);
            }
            return inPhenotypeAttemptColonyName;
        }
        return null;
    }

    private PhenotypeAttempt containsByImitsID(PhenotypeAttempts phenotypeAttempts, PhenotypeAttempt phenotypeAttempt) {
        for (PhenotypeAttempt pa : phenotypeAttempts.getPhenotypeAttempt()) {
            if (pa.getImitsID().equals(phenotypeAttempt.getImitsID())) {
                return pa;
            }
        }
        return null;
    }

    private void initPhenotypeAttemptsFromDB() {
        phenotypeAttemptsFromDB = null;
        try {
            phenotypeAttemptsFromDB = hibernateManager.uniqueResult(PhenotypeAttempts.class);
        } catch (NoResultException ex) {
            logger.error("no phenotype attempts found ", ex);
        }
        if (phenotypeAttemptsFromDB != null) {
            logger.info(" {} phenotypeAttempt instances found in the database", phenotypeAttemptsFromDB.getPhenotypeAttempt().size());
        }
        if (phenotypeAttemptsFromDB == null) {
            logger.info("phentoype attempts is empty.Creating a new one");
            phenotypeAttemptsFromDB = new PhenotypeAttempts();
            phenotypeAttemptsFromDB.setRetrieved(DatatypeConverter.now());
            hibernateManager.persist(phenotypeAttemptsFromDB);
            hibernateManager.alter();
        }
    }

    private void updateDatabase(PhenotypeAttempt phenotypeAttempt) throws TransactionRequiredException, PersistenceException {
        if (phenotypeAttemptsFromDB != null) {

            PhenotypeAttempt phenotypeAttemptFromDB = null;
            try {
                phenotypeAttemptFromDB = this.containsByImitsID(phenotypeAttemptsFromDB, phenotypeAttempt);
            } catch (Exception ex) {
                logger.error("error lazy loading phenotypeAttempt id {}from the database", phenotypeAttempt.getImitsID(), ex);
            }
            if (phenotypeAttemptFromDB != null) {
                logger.info("phenotype attempt ID {} exists in the database", phenotypeAttemptFromDB.getImitsID());
                if (!phenotypeAttempt.equals(phenotypeAttemptFromDB)) {
                    logger.info("phenotypeattempt ID {} exists in the database, and has changed online", phenotypeAttemptFromDB.getImitsID());

                    try {
                        logger.info("removing phenotypeattempt ID {} becauses it has changed", phenotypeAttemptFromDB.getImitsID());
                        phenotypeAttemptsFromDB.getPhenotypeAttempt().remove(phenotypeAttemptFromDB);
                        hibernateManager.alter();
                    } catch (Exception ex) {
                        logger.error("error altering phenotype attempt {} from imits", phenotypeAttempt.getImitsID(), ex);
                    }

                    try {
                        logger.info("adding the new version of phenotypeattempt ID {} to the database", phenotypeAttempt.getImitsID());
                        phenotypeAttemptsFromDB.getPhenotypeAttempt().add(phenotypeAttempt);
                        hibernateManager.alter();
                    } catch (Exception ex) {
                        logger.error("error adding phenotype attemptID {} from imits", phenotypeAttempt.getImitsID(), ex);
                    }

                } else {
                    logger.info("phenotype attemptID {} exists in the database, has not changed online", phenotypeAttemptFromDB.getImitsID());
                }
            } else {
                logger.info("phenotype attemptID {} does not exist in the database", phenotypeAttempt.getImitsID());
                try {
                    logger.info("adding a phenotypeID {} that was not in the database", phenotypeAttempt.getImitsID());
                    phenotypeAttemptsFromDB.getPhenotypeAttempt().add(phenotypeAttempt);
                    hibernateManager.alter();
                } catch (Exception ex) {
                    logger.error("error adding phenotype attempt that was not previously in the database {} from imits", phenotypeAttempt.getImitsID(), ex);
                }
            }
            logger.info("{} phenotype attempts on memmory", phenotypeAttemptsFromDB.getPhenotypeAttempt().size());
        } else {
            logger.error("there should be a phenotypeattempts entity in the db");
        }

    }
}
