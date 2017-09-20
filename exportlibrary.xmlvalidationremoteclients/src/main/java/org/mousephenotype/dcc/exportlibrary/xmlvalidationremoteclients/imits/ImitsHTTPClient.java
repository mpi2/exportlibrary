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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import org.apache.http.client.utils.URIBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.parser.ParseException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.ImitsProductionCentre;
import org.slf4j.LoggerFactory;

public class ImitsHTTPClient {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImitsHTTPClient.class);
    private static final String scheme = "https";
    private static final String host = "www.i-dcc.org";
    private static final String phenotype_attempts_path = "/imits/phenotyping_productions.json";
    private static final String microinjection_attempts_path = "/imits/mi_attempts.json";
    private static final String microinjection_plans_path = "/imits/mi_plans.json";
    private static final String attempts = "10000";
    private static final String attempts2 = "1000";
    private static final String date = "2011-08-01";
    private static final String JSON_FOLDER = "data/json/";
    private final Client client;

    static {
        if (!(new File(ImitsHTTPClient.JSON_FOLDER).exists())) {
            new File(ImitsHTTPClient.JSON_FOLDER).mkdirs();
        }
    }

    public static String getPhenotypeAttemptCentreRowDataFilename(ImitsProductionCentre imitsProductionCentre) {
        return ImitsHTTPClient.JSON_FOLDER + imitsProductionCentre.value() + "phenotype_attempt.json";
    }

    public static String getPhenotypeAttemptDataFilename(String imitsID) {
        return ImitsHTTPClient.JSON_FOLDER + imitsID + "_phenotype_attempt.json";
    }

    public static String getMicroInjectionAttemptCentreRowDataFilename(ImitsProductionCentre imitsProductionCentre) {
        return ImitsHTTPClient.JSON_FOLDER + imitsProductionCentre.value() + "microinjection_attempt.json";
    }

    public static String getMiPlanCentreRowDataFilename(ImitsProductionCentre imitsProductionCentre) {
        return ImitsHTTPClient.JSON_FOLDER + imitsProductionCentre.value() + "miplan.json";
    }

    public ImitsHTTPClient(String username, String password) {
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.universal(username, password);
        client = ClientBuilder.newClient();
        client.register(feature);

    }

    protected URI getPhenotypeAttemptURIForProducionCentreAndColonyID(ImitsProductionCentre imitsProductionCentre, String colonyID) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(phenotype_attempts_path);
        if (imitsProductionCentre != null) {
            builder.setParameter("production_centre_name_eq", imitsProductionCentre.value());
        }
        builder.setParameter("parent_colony_genotype_confirmed_eq", "true");
        builder.setParameter("status_name_neq", "Rederivation Started");

        builder.addParameter("colony_name_eq", colonyID);

        return builder.build();
    }

    protected URI getPhenotypeAttemptURIForColonyIDOnly(String colonyID) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(phenotype_attempts_path);

        builder.addParameter("colony_name_eq", colonyID);

        return builder.build();
    }

    private URI getImitsURI_forPhenotypeAttemptsForProductionCentre(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(phenotype_attempts_path);
        builder.setParameter("production_centre_name_eq", imitsProductionCentre.value());
        if (isSecondAttempt) {
            builder.setParameter("per_page", attempts2);
        } else {
            builder.setParameter("per_page", attempts);
        }

        builder.setParameter("parent_colony_genotype_confirmed_eq", "true");
        builder.setParameter("status_name_neq", "Rederivation Started");

        return builder.build();
    }

    private URI getPhenotypeAttemptURIForImitsID(String imitsID) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(phenotype_attempts_path);
        builder.addParameter("id_eq", imitsID);
        return builder.build();
    }

    protected URI getMIPlanURIForMIPlanID(String miPlanID) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(microinjection_plans_path);
        builder.addParameter("id_eq", miPlanID);
        return builder.build();
    }

    private URI getPhenotypeAttemptURIForColonyID(String colonyID, ImitsProductionCentre imitsProductionCentre) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(phenotype_attempts_path);
        builder.addParameter("colony_name_eq", colonyID);
        builder.addParameter("production_centre_eq", imitsProductionCentre.value());
        return builder.build();
    }

    private URI getImitsURI_forMicroinjectionAttemptsForProductionCentre(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(microinjection_attempts_path);
        builder.setParameter("production_centre_name_eq", imitsProductionCentre.value());
        if (isSecondAttempt) {
            builder.setParameter("per_page", attempts2);
        } else {
            builder.setParameter("per_page", attempts);
        }
        builder.setParameter("mi_date_gteq", date);
        return builder.build();
    }

    private URI getImitsURI_forMicroinjectionPlansForProductionCentre(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(microinjection_plans_path);
        builder.setParameter("production_centre_name_eq", imitsProductionCentre.value());
        if (isSecondAttempt) {
            builder.setParameter("per_page", attempts2);
        } else {
            builder.setParameter("per_page", attempts);
        }

        return builder.build();
    }

    protected boolean getData(URI uri, String filename, boolean append) throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        WebTarget webTarget = client.target(uri);
        logger.info("uri {} to file {}", uri.toASCIIString(), filename);
        String execute = null;
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response response = invocationBuilder.get();
        execute = response.readEntity(String.class);

        if (execute != null) {
            File file = new File(filename);
            FileWriter fileWriter = new FileWriter(file, append);
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(execute);
            out.close();
            return true;
        }
        return false;
    }

    public void downloadPhenotypeAttempts(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws URISyntaxException, UnrecoverableKeyException, KeyStoreException, KeyManagementException, NoSuchAlgorithmException, IOException {
        URI centreUri = getImitsURI_forPhenotypeAttemptsForProductionCentre(imitsProductionCentre, isSecondAttempt);
        this.getData(centreUri, getPhenotypeAttemptCentreRowDataFilename(imitsProductionCentre), false);
    }

    public void downloadPhenotypeAttempt(String imitsID) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        URI centreUri = this.getPhenotypeAttemptURIForImitsID(imitsID);
        this.getData(centreUri, getPhenotypeAttemptDataFilename(imitsID), false);
    }

    public void downloadPhenotypeAttemptByColonyID(String colonyID, ImitsProductionCentre imitsProductionCentre) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        URI centreUri = this.getPhenotypeAttemptURIForProducionCentreAndColonyID(imitsProductionCentre, colonyID);
        this.getData(centreUri, getPhenotypeAttemptDataFilename(colonyID), false);
    }

    public void downloadMicroinjectionAttempts(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, URISyntaxException, FileNotFoundException, ParseException {
        URI centreUri = getImitsURI_forMicroinjectionAttemptsForProductionCentre(imitsProductionCentre, isSecondAttempt);
        this.getData(centreUri, getMicroInjectionAttemptCentreRowDataFilename(imitsProductionCentre), false);
    }

    public void downloadMicroinjectionPlans(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, URISyntaxException, FileNotFoundException, ParseException {
        URI centreUri = getImitsURI_forMicroinjectionPlansForProductionCentre(imitsProductionCentre, isSecondAttempt);
        this.getData(centreUri, getMiPlanCentreRowDataFilename(imitsProductionCentre), false);
    }
}
