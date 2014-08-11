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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress;

import org.mousephenotype.impress.soap.server.ArrayOfString;
import org.mousephenotype.impress.soap.server.ImpressSoapPort;
import org.mousephenotype.impress.soap.server.ImpressSoapService;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class Resource {

    private static ImpressSoapService service;
    private static ImpressSoapPort port;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Resource.class);

    //initializing impress ws connection
    static {
        try {
            service = new ImpressSoapService();
            port = service.getImpressSoapPort();
        } catch (Exception ex) {
            logger.error("cannot connect to Impress", ex);
        }
    }

    public static boolean validParameter(String parameterID) {
        return port.isValidParameter(parameterID);
    }

    public static boolean validParameter(String procedureID, String parameterID) {
        return port.procedureHasParameter(procedureID, parameterID);
    }

    public static boolean validProcedure(String procedureID) {
        return port.isValidProcedure(procedureID);
    }

    public static boolean validProcedure(String pipelineID, String procedureID) {
        return port.pipelineHasProcedure(pipelineID, procedureID);
    }

    public static boolean validPipeline(String pipeline) {
        return port.isValidPipeline(pipeline);
    }

    public static ArrayOfString getPipelineKeys() {
        return port.getPipelineKeys();
    }

    public static Object getPipeline(String pipelineKey) {
        return port.getPipeline(pipelineKey);
    }

    public static Object getProcedures(String pipelineKey) {
        return port.getProcedures(pipelineKey);
    }

    public static Object getParameters(String procedureKey) {
        return port.getParameters(procedureKey);
    }

    public static Object getParameterOptions(String parameterKey) {
        return port.getParameterOptions(parameterKey);
    }

    public static Object getParameterOntologyOptions(String parameterKey) {
        return port.getParameterOntologyOptions(parameterKey);
    }

    public static Object getParameterIncrements(String parameterKey) {
        return port.getParameterIncrements(parameterKey);
    }

    public static Object getProcedure(String procedureKey, String pipelineKey) {
        return port.getProcedure(procedureKey, pipelineKey);
    }

    public static Object getParameter(String parameterKey) {
        return port.getParameter(parameterKey);
    }

    public static boolean validParameter(String pipelineID, String procedureID, String parameterID) {
        return port.pipelineHasProcedure(pipelineID, procedureID)
                && port.procedureHasParameter(procedureID, parameterID);
    }

    public static String getWhenLastModified() {
        return port.getWhenLastModified();
    }

    public static void kk() {

        port.getParameterEQTerms("ESLIM_022_001_001"); // returns Entity Quality 
        port.getParameterMPTerms("ESLIM_022_001_001");// return a number of MP terms based on the selection outcome

        //port.getWhenLastModified(); for the whole database
        //date_modified as an attribute for pipeline, procedure, parameter...
    }
}
