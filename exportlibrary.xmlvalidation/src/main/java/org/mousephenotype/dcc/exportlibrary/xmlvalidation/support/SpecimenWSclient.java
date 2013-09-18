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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.support;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.CentreProcedureSetValidator;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class SpecimenWSclient {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(CentreProcedureSetValidator.class);
    
    public static final String SPECIMEN_WS_URL_PROPERTY_PREFIX = "specimen.ws.url";
    public static final String SPECIMEN_WS_URL_OPTION_PROPERTY = SPECIMEN_WS_URL_PROPERTY_PREFIX + ".option";
    private final Client client;
    private WebResource webResource;
    private ClientResponse response;
    private MultivaluedMap parammap;

    public SpecimenWSclient(String url) {
        parammap = new MultivaluedMapImpl();
        client = Client.create();
        webResource = client.resource(url);
    }

    public boolean exists(CentreILARcode centreILARcode, String specimenID) throws UniformInterfaceException, ClientHandlerException{
        parammap.clear();
        parammap.add("specimenName", specimenID);
        parammap.add("centreILAR", centreILARcode.value());
        logger.info(" specimen ws client query {}",webResource.queryParams(parammap).getURI().toASCIIString());
        response = webResource.queryParams(parammap).accept("application/json").get(ClientResponse.class);
        if (response.getStatus() != 200) {
            logger.error("The webservice failed to respond correctly");
            return false;
        }
        if (response.hasEntity()) {
            if (response.getEntity(String.class).contains("specimenName")) {

                return true;
            } else {
                logger.info(" centre {} specimen {} not been found", centreILARcode.value(), specimenID);
            }
        }
        return false;
    }
}
