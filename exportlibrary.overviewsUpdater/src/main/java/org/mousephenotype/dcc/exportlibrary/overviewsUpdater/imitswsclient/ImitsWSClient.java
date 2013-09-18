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
package org.mousephenotype.dcc.exportlibrary.overviewsUpdater.imitswsclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.parser.ParseException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MIPlan;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempt;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationremoteclients.imits.ImitsOracle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImitsWSClient {

    private static final Logger logger = LoggerFactory.getLogger(ImitsWSClient.class);
    private ImitsOracle imitsOracle;
    private boolean loaded = false;
    private List<PhenotypeAttempt> phenotypeAttempts;
    private List<MIPlan> mIPlans;

    public ImitsWSClient(String username, String password) {
        this.imitsOracle = new ImitsOracle(username, password);
    }

    public List<PhenotypeAttempt> getPhenotypeAttemtps() {
        return this.phenotypeAttempts;
    }

    public List<MIPlan> getmIPlans() {
        return mIPlans;
    }

    public boolean isSetPhenotypeAttempts() {
        return loaded && this.getPhenotypeAttemtps() != null && this.getPhenotypeAttemtps().size() > 0;
    }

    public void loadPhenotypeAttemtps(List<String> colonyIDs) {
        if (!loaded) {
            this.phenotypeAttempts = new ArrayList<>();
            this.mIPlans = new ArrayList<>();
            this.downloadPhenotypeAttempts(colonyIDs);
            this.loaded = true;
        }
    }

    private void downloadPhenotypeAttempts(List<String> colonyIDs) {
        PhenotypeAttempt phenotypeAttempt = null;
        MIPlan mIPlan = null;
        for (String colonyID : colonyIDs) {
            try {
                phenotypeAttempt = this.imitsOracle.isInPhenotypeAttemptColonyName(null, colonyID);
            } catch (URISyntaxException | IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException | ParseException ex) {
                logger.error("error connecting to imits", ex);
            }
            if (phenotypeAttempt != null) {
                this.phenotypeAttempts.add(phenotypeAttempt);
                try {
                    mIPlan = this.imitsOracle.loadMIPlan(phenotypeAttempt.getMiPlanID().toString());
                } catch (FileNotFoundException ex) {
                    logger.error("error loading miplan file", ex);
                } catch (IOException | ParseException ex) {
                    logger.error("error loading miplan file", ex);
                }
                if (mIPlan != null) {
                    this.mIPlans.add(mIPlan);
                }
            }
            phenotypeAttempt = null;
            mIPlan = null;
        }

    }
}
