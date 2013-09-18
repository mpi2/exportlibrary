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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.CONNECTOR_NAMES;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.consoleapps.support.ValidatorSupport;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class XML2XMLDerbyParametersGenerator {

    private static final String TEST_RESULTS_FOLDER = "test_results";
    private static final String TEST_CENTREPROCEDURESET_XML_DOCUMENT_FILENAME = "src/test/resources/data/IMPC_XRY_001.xml";
    private static final String TEST_RESULT_XML_DOCUMENT_FILENAME = "/IMPC_XRY_001_validation.xml";
    private static final String TEST_RESULT_REPORT_XML_DOCUMENT_FILENAME = "/IMPC_XRY_001_validation_report.xml";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IOControllerTestXML2XMLDerbyResources.class);

    static {
        if (!(new File(TEST_RESULTS_FOLDER).exists())) {
            new File(TEST_RESULTS_FOLDER).mkdirs();
        }

        System.setProperty("derby.system.home", System.getProperty("test.databases.folder"));
        //
        System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");

    }

    private static IOParameters getInput() {
        IOParameters input = new IOParameters();
        input.setDoc_srcs(IOParameters.DOCUMENT_SRCS.CENTREPROCEDURESET);
        input.setIos(IOParameters.IOS.XML_FILE);
        Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<IOParameters.IOS_PROPERTIES, String>(IOParameters.IOS_PROPERTIES.class);
        input.setValues(values);
        values.put(IOParameters.IOS_PROPERTIES.CONTEXTPATH, ValidatorSupport.PERSISTENCEUNITNAME_4_DATA);
        values.put(IOParameters.IOS_PROPERTIES.XMLFILENAME, TEST_CENTREPROCEDURESET_XML_DOCUMENT_FILENAME);
        return input;
    }

    private static IOParameters getOutput() {
        IOParameters output = new IOParameters();
        output.setIos(IOParameters.IOS.XML_FILE);
        Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<IOParameters.IOS_PROPERTIES, String>(IOParameters.IOS_PROPERTIES.class);
        output.setValues(values);
        values.put(IOParameters.IOS_PROPERTIES.CONTEXTPATH, ValidatorSupport.PERSISTENCEUNITNAME_4_DATA);
        values.put(IOParameters.IOS_PROPERTIES.XMLFILENAME, TEST_RESULTS_FOLDER + TEST_RESULT_XML_DOCUMENT_FILENAME);
        return output;
    }

    private static IOParameters getOutputReport() {
        IOParameters outputReport = new IOParameters();
        outputReport.setIos(IOParameters.IOS.XML_FILE);
        Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<IOParameters.IOS_PROPERTIES, String>(IOParameters.IOS_PROPERTIES.class);
        outputReport.setValues(values);
        values.put(IOParameters.IOS_PROPERTIES.CONTEXTPATH, ValidatorSupport.PERSISTENCEUNITNAME_4_DATA);
        values.put(IOParameters.IOS_PROPERTIES.XMLFILENAME, TEST_RESULTS_FOLDER + TEST_RESULT_REPORT_XML_DOCUMENT_FILENAME);
        return outputReport;
    }

    
    private static IOParameters getResources() {
        IOParameters resources = new IOParameters();
        resources.setIos(IOParameters.IOS.TEST_DB_PROPERTIES);
        Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<IOParameters.IOS_PROPERTIES, String>(IOParameters.IOS_PROPERTIES.class);
        resources.setValues(values);
        values.put(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME, ValidatorSupport.PERSISTENCEUNITNAME_4_RESOURCES);
        values.put(IOParameters.IOS_PROPERTIES.PROPERTIES_FILENAME, System.getProperty("test.databases.conf") +"/xmlvalidationresources.derby.properties");
        return resources;
    }

    public static Map<CONNECTOR_NAMES, IOParameters> getXML2XMLDerbyParameters() {
        Map<CONNECTOR_NAMES, IOParameters> ioparametersMap = new EnumMap<CONNECTOR_NAMES, IOParameters>(CONNECTOR_NAMES.class);
        //
        IOParameters input = XML2XMLDerbyParametersGenerator.getInput();
        ioparametersMap.put(CONNECTOR_NAMES.centreProcedureSetIncarnator, input);
        //
        IOParameters output = XML2XMLDerbyParametersGenerator.getOutput();
        ioparametersMap.put(CONNECTOR_NAMES.validationSetSerializer, output);
        //
        IOParameters outputReport = XML2XMLDerbyParametersGenerator.getOutputReport();
        ioparametersMap.put(CONNECTOR_NAMES.validationReportSetSerializer, outputReport);
        //
        IOParameters resources = XML2XMLDerbyParametersGenerator.getResources();
        ioparametersMap.put(CONNECTOR_NAMES.resources, resources);
        return ioparametersMap;
    }
}
