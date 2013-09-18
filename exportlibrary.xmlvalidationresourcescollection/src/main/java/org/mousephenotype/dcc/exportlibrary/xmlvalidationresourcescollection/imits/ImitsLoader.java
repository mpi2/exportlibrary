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

import java.io.*;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.*;
import javax.xml.bind.JAXBException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
 
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.*;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationremoteclients.imits.ImitsHTTPClient;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationremoteclients.imits.ImitsParser;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ImitsLoader {

    private ImitsHTTPClient imits;
    private ImitsParser imitsParser;
    //https://www.mousephenotype.org/imits/mi_plans.json?production_centre_name_eq=MARC
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImitsLoader.class);
    //
    private PhenotypeAttempts PhenotypeAttempts;
    private MicroinjectionAttempts MicroinjectionAttempts;
    private MiPlans MiPlans;

    public MiPlans getMiPlans() {
        return MiPlans;
    }

    public ImitsLoader(String username, String password) {
        this.imits = new ImitsHTTPClient(username, password);
        this.imitsParser = new ImitsParser();
    }

    private void runPhenotypeAttempts(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, URISyntaxException, FileNotFoundException, ParseException, NoHttpResponseException {
        this.imits.downloadPhenotypeAttempts(imitsProductionCentre, isSecondAttempt);
        PhenotypeAttempts centrePhenotypeAttempts = this.imitsParser.getPhenotypeAttempts(ImitsHTTPClient.getPhenotypeAttemptCentreRowDataFilename(imitsProductionCentre));

        getPhenotypeAttempts().getPhenotypeAttempt().addAll(centrePhenotypeAttempts.getPhenotypeAttempt());
    }

    private void runMicroinjectionAttempts(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, URISyntaxException, FileNotFoundException, ParseException, NoHttpResponseException {
        this.imits.downloadMicroinjectionAttempts(imitsProductionCentre, isSecondAttempt);
        MicroinjectionAttempts microinjectionAttempts = this.imitsParser.getMicroinjectionAttempts(ImitsHTTPClient.getMicroInjectionAttemptCentreRowDataFilename(imitsProductionCentre));
        getMicroinjectionAttempts().getMicroinjectionAttempt().addAll(microinjectionAttempts.getMicroinjectionAttempt());
    }

    private void runMiPlans(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, URISyntaxException, FileNotFoundException, ParseException, NoHttpResponseException {
        this.imits.downloadMicroinjectionPlans(imitsProductionCentre, isSecondAttempt);
        MiPlans miPlans = this.imitsParser.getMiPlans(ImitsHTTPClient.getMiPlanCentreRowDataFilename(imitsProductionCentre));
        getMiPlans().getMiPlan().addAll(miPlans.getMiPlan());
    }

    public void run(Calendar now) {

        PhenotypeAttempts = new PhenotypeAttempts();
        MicroinjectionAttempts = new MicroinjectionAttempts();
        MiPlans = new MiPlans();


        MicroinjectionAttempts.setRetrieved(now);
        PhenotypeAttempts.setRetrieved(now);
        MiPlans.setRetrieved(now);

        Set<ImitsProductionCentre> retryPhenotypeAttemptsDownload = new HashSet<ImitsProductionCentre>();
        Set<ImitsProductionCentre> retryMicroinjectionAttemptsDownload = new HashSet<ImitsProductionCentre>();
        Set<ImitsProductionCentre> retryMiPlansDownload = new HashSet<ImitsProductionCentre>();

        for (ImitsProductionCentre imitsProductionCentre : ImitsProductionCentre.values()) {
            try {
                this.runPhenotypeAttempts(imitsProductionCentre, false);
            } catch (ClientProtocolException ex) {
                logger.error("", ex);
            } catch (NoSuchAlgorithmException ex) {
                logger.error("", ex);
            } catch (KeyManagementException ex) {
                logger.error("", ex);
            } catch (KeyStoreException ex) {
                logger.error("", ex);
            } catch (UnrecoverableKeyException ex) {
                logger.error("", ex);
            } catch (URISyntaxException ex) {
                logger.error("", ex);
            } catch (FileNotFoundException ex) {
                logger.error("", ex);
            } catch (ParseException ex) {
                logger.error("", ex);
            } catch (NoHttpResponseException ex) {
                logger.error("", ex);
                retryPhenotypeAttemptsDownload.add(imitsProductionCentre);
            } catch (IOException ex) {
                logger.error("", ex);
            }

            try {
                this.runMicroinjectionAttempts(imitsProductionCentre, false);
            } catch (ClientProtocolException ex) {
                logger.error("", ex);
            } catch (NoSuchAlgorithmException ex) {
                logger.error("", ex);
            } catch (KeyManagementException ex) {
                logger.error("", ex);
            } catch (KeyStoreException ex) {
                logger.error("", ex);
            } catch (UnrecoverableKeyException ex) {
                logger.error("", ex);
            } catch (URISyntaxException ex) {
                logger.error("", ex);
            } catch (FileNotFoundException ex) {
                logger.error("", ex);
            } catch (ParseException ex) {
                logger.error("", ex);
            } catch (NoHttpResponseException ex) {
                logger.error("", ex);
                retryMicroinjectionAttemptsDownload.add(imitsProductionCentre);
            } catch (IOException ex) {
                logger.error("", ex);
            }

            try {
                this.runMiPlans(imitsProductionCentre, false);
            } catch (ClientProtocolException ex) {
                logger.error("", ex);
            } catch (NoSuchAlgorithmException ex) {
                logger.error("", ex);
            } catch (KeyManagementException ex) {
                logger.error("", ex);
            } catch (KeyStoreException ex) {
                logger.error("", ex);
            } catch (UnrecoverableKeyException ex) {
                logger.error("", ex);
            } catch (URISyntaxException ex) {
                logger.error("", ex);
            } catch (FileNotFoundException ex) {
                logger.error("", ex);
            } catch (ParseException ex) {
                logger.error("", ex);
            } catch (NoHttpResponseException ex) {
                logger.error("", ex);
                retryMiPlansDownload.add(imitsProductionCentre);
            } catch (IOException ex) {
                logger.error("", ex);
            }
        }
        //
        logger.info("retrying retryPhenotypeAttemptsDownload");
        Iterator<ImitsProductionCentre> iterator = retryPhenotypeAttemptsDownload.iterator();
        ImitsProductionCentre centre = null;
        while (iterator.hasNext()) {
            centre = iterator.next();
            logger.info("retrying phenotype attempts download for {}", centre);
            try {
                this.runPhenotypeAttempts(centre, true);
            } catch (ClientProtocolException ex) {
                logger.error("", ex);
            } catch (NoSuchAlgorithmException ex) {
                logger.error("", ex);
            } catch (KeyManagementException ex) {
                logger.error("", ex);
            } catch (KeyStoreException ex) {
                logger.error("", ex);
            } catch (UnrecoverableKeyException ex) {
                logger.error("", ex);
            } catch (URISyntaxException ex) {
                logger.error("", ex);
            } catch (FileNotFoundException ex) {
                logger.error("", ex);
            } catch (ParseException ex) {
                logger.error("", ex);
            } catch (NoHttpResponseException ex) {
                logger.error("", ex);
            } catch (IOException ex) {
                logger.error("", ex);
            }
        }
        //
        logger.info("retrying retryMicroinjectionAttemptsDownload");
        iterator = retryMicroinjectionAttemptsDownload.iterator();
        centre = null;
        while (iterator.hasNext()) {
            centre = iterator.next();
            logger.info("retrying microinjection attempts download for {}", centre);
            try {
                this.runMicroinjectionAttempts(centre, true);
            } catch (ClientProtocolException ex) {
                logger.error("", ex);
            } catch (NoSuchAlgorithmException ex) {
                logger.error("", ex);
            } catch (KeyManagementException ex) {
                logger.error("", ex);
            } catch (KeyStoreException ex) {
                logger.error("", ex);
            } catch (UnrecoverableKeyException ex) {
                logger.error("", ex);
            } catch (URISyntaxException ex) {
                logger.error("", ex);
            } catch (FileNotFoundException ex) {
                logger.error("", ex);
            } catch (ParseException ex) {
                logger.error("", ex);
            } catch (NoHttpResponseException ex) {
                logger.error("", ex);
            } catch (IOException ex) {
                logger.error("", ex);
            }
        }
        //
        logger.info("retrying retryMiPlansDownload");
        iterator = retryMiPlansDownload.iterator();
        centre = null;
        while (iterator.hasNext()) {
            centre = iterator.next();
            logger.info("retrying mi plansownload for {}", centre);
            try {
                this.runMiPlans(centre, true);
            } catch (ClientProtocolException ex) {
                logger.error("", ex);
            } catch (NoSuchAlgorithmException ex) {
                logger.error("", ex);
            } catch (KeyManagementException ex) {
                logger.error("", ex);
            } catch (KeyStoreException ex) {
                logger.error("", ex);
            } catch (UnrecoverableKeyException ex) {
                logger.error("", ex);
            } catch (URISyntaxException ex) {
                logger.error("", ex);
            } catch (FileNotFoundException ex) {
                logger.error("", ex);
            } catch (ParseException ex) {
                logger.error("", ex);
            } catch (NoHttpResponseException ex) {
                logger.error("", ex);
            } catch (IOException ex) {
                logger.error("", ex);
            }
        }

    }

    public void run(String filename, Calendar now) throws IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, URISyntaxException, FileNotFoundException, ParseException {
        this.run(now);
        try {
            
            XMLUtils.marshall("org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external", getPhenotypeAttempts(), filename + "phs.xml");
            XMLUtils.marshall("org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external", getMicroinjectionAttempts(), filename + "mis.xml");
            XMLUtils.marshall("org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external", getMicroinjectionAttempts(), filename + "miplans.xml");
        } catch (JAXBException ex) {
            logger.error("", ex);
        }
    }

    public void run(HibernateManager hibernateManager, Calendar now) throws IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, URISyntaxException, FileNotFoundException, ParseException {
        this.run(now);
        hibernateManager.persist(this.MicroinjectionAttempts);
        hibernateManager.persist(this.PhenotypeAttempts);
        hibernateManager.persist(this.MiPlans);
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
}
