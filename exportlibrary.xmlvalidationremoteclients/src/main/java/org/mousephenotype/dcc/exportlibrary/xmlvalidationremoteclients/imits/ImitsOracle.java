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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationremoteclients.imits;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.ImitsProductionCentre;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MIPlan;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MiPlans;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempt;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempts;
import org.slf4j.LoggerFactory;

public class ImitsOracle {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImitsHTTPClient.class);
    private static final String JSON_FOLDER = "data/json/";
    private final ImitsHTTPClient imitsHTTPClient;
    private final ImitsParser imitsParser;

    static {
        if (!(new File(ImitsOracle.JSON_FOLDER).exists())) {
            new File(ImitsOracle.JSON_FOLDER).mkdirs();
        }
    }

    public ImitsOracle(String username, String password) {
        this.imitsHTTPClient = new ImitsHTTPClient(username, password);
        this.imitsParser = new ImitsParser();
    }

    public static String getPhenotypeAttemptFilename(ImitsProductionCentre imitsProductionCentre, String colonyID) {
        StringBuilder sb = new StringBuilder(JSON_FOLDER);
        if (imitsProductionCentre != null) {
            sb.append(imitsProductionCentre.name());
            sb.append("_");
        }
        sb.append(colonyID);
        sb.append(".json");
        return sb.toString();
    }

    public static String getMIPlanFilename(String miPlanID) {
        StringBuilder sb = new StringBuilder(JSON_FOLDER);
        sb.append("miplan_");
        sb.append(miPlanID);
        sb.append(".json");
        return sb.toString();
    }

    private MIPlan getMIPlan(String miPlanID) throws URISyntaxException, IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, FileNotFoundException, ParseException {
        URI uri = imitsHTTPClient.getMIPlanURIForMIPlanID(miPlanID);
        logger.trace("contacting imits to download miPlan {}", miPlanID);
        imitsHTTPClient.getData(uri, getMIPlanFilename(miPlanID), false);
        return this.loadMIPlan(miPlanID);
    }

    public MIPlan loadMIPlan(String miPlanID) throws FileNotFoundException, IOException, ParseException {
        MiPlans miPlans = imitsParser.getMiPlans(getMIPlanFilename(miPlanID));
        logger.trace("{} miplans found", miPlans.getMiPlan().size());
        if (miPlans.getMiPlan().size() > 1) {
            logger.warn("It should only return one attempt.");
        }
        if (miPlans.getMiPlan() != null && miPlans.getMiPlan().size() > 0) {
            return miPlans.getMiPlan().get(0);
        }
        return null;
    }
    
    public PhenotypeAttempt isInPhenotypeAttemptColonyName(ImitsProductionCentre imitsProductionCentre, String colonyID) throws URISyntaxException, IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, FileNotFoundException, ParseException {
        URI centreUri = imitsHTTPClient.getPhenotypeAttemptURIForProducionCentreAndColonyID(null, colonyID);
        logger.warn("uri = " + centreUri.toString());
        logger.trace("contacting imits for phenotype on {} {}", null, colonyID);
        imitsHTTPClient.getData(centreUri, getPhenotypeAttemptFilename(null, colonyID), false);
        logger.trace("parsing response stored in {}", getPhenotypeAttemptFilename(null, colonyID));
        String phenotypeAttemptFilename = getPhenotypeAttemptFilename(null, colonyID);
        PhenotypeAttempts phenotypeAttempts = imitsParser.getPhenotypeAttempts(phenotypeAttemptFilename);
        
        logger.trace("{} phenotype attempts found", phenotypeAttempts.getPhenotypeAttempt().size());
        if (phenotypeAttempts.getPhenotypeAttempt().size() > 1) {
            logger.warn("It should only return one attempt.");
        }

        PhenotypeAttempt phenotypeAttempt = null;

        if (phenotypeAttempts.getPhenotypeAttempt() != null && phenotypeAttempts.getPhenotypeAttempt().size() > 0) {
            phenotypeAttempt = phenotypeAttempts.getPhenotypeAttempt().get(0);
            MIPlan miPlan = this.getMIPlan(phenotypeAttempt.getMiPlanID().toString());
            if (miPlan != null && miPlan.getMarkerMGIaccessionID() != null) {
                phenotypeAttempt.getGenotypicInformation().setGeneMGIID(miPlan.getMarkerMGIaccessionID());
            }
        }
        return phenotypeAttempt;
    }
        
    public PhenotypeAttempt isInPhenotypeAttemptColonyNameOnly(String colonyID) throws URISyntaxException, IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, FileNotFoundException, ParseException {
        URI centreUri = imitsHTTPClient.getPhenotypeAttemptURIForColonyIDOnly(colonyID);
        System.out.println("uri = " + centreUri.toString());
        logger.trace("contacting imits for phenotype on {} {}", null, colonyID);
        imitsHTTPClient.getData(centreUri, getPhenotypeAttemptFilename(null, colonyID), false);
        logger.trace("parsing response stored in {}", getPhenotypeAttemptFilename(null, colonyID));
        System.out.println("file = " + getPhenotypeAttemptFilename(null, colonyID));
        PhenotypeAttempts phenotypeAttempts = imitsParser.getPhenotypeAttempts(getPhenotypeAttemptFilename(null, colonyID));
       
        
        logger.trace("{} phenotype attempts found", phenotypeAttempts.getPhenotypeAttempt().size());
        if (phenotypeAttempts.getPhenotypeAttempt().size() > 1) {
            logger.warn("It should only return one attempt.");
        }

        PhenotypeAttempt phenotypeAttempt = null;

        if (phenotypeAttempts.getPhenotypeAttempt() != null && phenotypeAttempts.getPhenotypeAttempt().size() > 0) {
            phenotypeAttempt = phenotypeAttempts.getPhenotypeAttempt().get(0);
            MIPlan miPlan = this.getMIPlan(phenotypeAttempt.getMiPlanID().toString());
            if (miPlan != null && miPlan.getMarkerMGIaccessionID() != null) {
                phenotypeAttempt.getGenotypicInformation().setGeneMGIID(miPlan.getMarkerMGIaccessionID());
            }
        }
        return phenotypeAttempt;
    }
}
