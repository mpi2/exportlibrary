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
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.simple.parser.ParseException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.ImitsProductionCentre;
import org.slf4j.LoggerFactory;

public class ImitsHTTPClient {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImitsHTTPClient.class);
    private static final String scheme = "http";
    private static final String host = "www.i-dcc.org";
    private static final String phenotype_attempts_path = "/imits/phenotyping_productions.json";
    private static final String microinjection_attempts_path = "/imits/mi_attempts.json";
    private static final String microinjection_plans_path = "/imits/mi_plans.json";
    private static final int TIMEOUT_SECS = 1000 * 60 * 4;
    private static final String attempts = "10000";
    private static final String attempts2 = "1000";
    private static final String date = "2011-08-01";
    private static final String JSON_FOLDER = "data/json/";
    private final String username;
    private final String password;
    private final HttpParams httpParams;
    private HttpGet httpget;
    private DefaultHttpClient httpclient;
    private ResponseHandler<String> responseHandler;

    static {
        if (!(new File(ImitsHTTPClient.JSON_FOLDER).exists())) {
            new File(ImitsHTTPClient.JSON_FOLDER).mkdirs();
        }
    }

    public class Strategy implements ConnectionKeepAliveStrategy {

        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            // Honor 'keep-alive' header
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return TIMEOUT_SECS;
                    } catch (NumberFormatException ignore) {
                    }
                }
            }

            return TIMEOUT_SECS;
        }
    };

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
        this.username = username;
        this.password = password;

        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_SECS);
        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_SECS);
        HttpConnectionParams.setLinger(httpParams, TIMEOUT_SECS);

        httpclient = new DefaultHttpClient(httpParams);
        HttpConnectionParams.setSoKeepalive(httpParams, true);
        httpclient.setKeepAliveStrategy(new ImitsHTTPClient.Strategy());
        responseHandler = new BasicResponseHandler();
    }

    protected URI getPhenotypeAttemptURIForProducionCentreAndColonyID(ImitsProductionCentre imitsProductionCentre, String colonyID) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(phenotype_attempts_path);
        if (imitsProductionCentre != null) {
            builder.setParameter("production_centre_name_eq", imitsProductionCentre.value());
        }
        builder.setUserInfo(username, password);
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
        builder.setUserInfo(username, password);

        builder.addParameter("colony_name_eq", colonyID);

        return builder.build();
    }

    private URI getImitsURI_forPhenotypeAttemptsForProductionCentre(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(phenotype_attempts_path);
        builder.setParameter("production_centre_name_eq", imitsProductionCentre.value());
        builder.setUserInfo(username, password);
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
        builder.setUserInfo(username, password);
        builder.addParameter("id_eq", imitsID);
        return builder.build();
    }

    protected URI getMIPlanURIForMIPlanID(String miPlanID) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(microinjection_plans_path);
        builder.setUserInfo(username, password);
        builder.addParameter("id_eq", miPlanID);
        return builder.build();
    }

    private URI getPhenotypeAttemptURIForColonyID(String colonyID, ImitsProductionCentre imitsProductionCentre) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(phenotype_attempts_path);
        builder.setUserInfo(username, password);
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
        builder.setUserInfo(username, password);
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
        builder.setUserInfo(username, password);
        if (isSecondAttempt) {
            builder.setParameter("per_page", attempts2);
        } else {
            builder.setParameter("per_page", attempts);
        }

        return builder.build();
    }

    protected boolean getData(URI uri, String filename, boolean append) throws IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, NoHttpResponseException {
        httpget = new HttpGet(uri);
        logger.info("uri {} to file {}", uri.toASCIIString(), filename);
        String execute = null;

        execute = httpclient.execute(httpget, responseHandler);

        if (execute != null) {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename, append));
            out.write(execute);
            out.close();
            return true;
        }
        return false;
    }

    public void downloadPhenotypeAttempts(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws URISyntaxException, NoHttpResponseException, UnrecoverableKeyException, KeyStoreException, KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException {
        URI centreUri = getImitsURI_forPhenotypeAttemptsForProductionCentre(imitsProductionCentre, isSecondAttempt);
        this.getData(centreUri, getPhenotypeAttemptCentreRowDataFilename(imitsProductionCentre), false);
    }

    public void downloadPhenotypeAttempt(String imitsID) throws URISyntaxException, IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        URI centreUri = this.getPhenotypeAttemptURIForImitsID(imitsID);
        this.getData(centreUri, getPhenotypeAttemptDataFilename(imitsID), false);
    }

    public void downloadPhenotypeAttemptByColonyID(String colonyID, ImitsProductionCentre imitsProductionCentre) throws URISyntaxException, IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        URI centreUri = this.getPhenotypeAttemptURIForProducionCentreAndColonyID(imitsProductionCentre, colonyID);
        this.getData(centreUri, getPhenotypeAttemptDataFilename(colonyID), false);
    }

    public void downloadMicroinjectionAttempts(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, URISyntaxException, FileNotFoundException, ParseException, NoHttpResponseException {
        URI centreUri = getImitsURI_forMicroinjectionAttemptsForProductionCentre(imitsProductionCentre, isSecondAttempt);
        this.getData(centreUri, getMicroInjectionAttemptCentreRowDataFilename(imitsProductionCentre), false);
    }

    public void downloadMicroinjectionPlans(ImitsProductionCentre imitsProductionCentre, boolean isSecondAttempt) throws IOException, ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, URISyntaxException, FileNotFoundException, ParseException, NoHttpResponseException {
        URI centreUri = getImitsURI_forMicroinjectionPlansForProductionCentre(imitsProductionCentre, isSecondAttempt);
        this.getData(centreUri, getMiPlanCentreRowDataFilename(imitsProductionCentre), false);
    }
}
