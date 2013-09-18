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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.consoleapps.support;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.CONNECTOR_NAMES;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ValidatorSupport {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(ValidatorSupport.class);
    public static final String PERSISTENCEUNITNAME_4_RESOURCES = "org.mousephenotype.dcc.exportlibrary.xmlvalidation.external";
    public static final String PERSISTENCEUNITNAME_4_DATA = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    public static final String CONTEXTPATH_4_DATA = PERSISTENCEUNITNAME_4_DATA;
    public static final String DEFAULT_HIBERNATE_CONFIGURATION_4_DATA_FILENAME = "hibernate.data.mysql.properties";
    public static final String DEFAULT_HIBERNATE_CONFIGURATION_4_RESOURCES_FILENAME = "xmlvalidationresources.derby.properties";
    public static final String CONTEXTPATH_4_RESOURCES = PERSISTENCEUNITNAME_4_RESOURCES;

    public static Map<CONNECTOR_NAMES, IOParameters> buidExecutionIOParameters(String specimenResultsFilename,
            String procedureResultsFilename,
            Long submissionID,
            Long submissionTrackerID,
            String submissionPersistenceFilename,
            String externalResourcesLocationPersistenceFilename,
            Boolean externalResourcesLocationOnLocalDatabase) {
        Map<CONNECTOR_NAMES, IOParameters> ioparameters = new EnumMap<>(CONNECTOR_NAMES.class);

        IOParameters input = null;
        IOParameters resource = null;
        IOParameters result = null;
        IOParameters result_report = null;

        if (specimenResultsFilename != null) {
            input = ValidatorSupport.getIOParametersForInput(
                    IOParameters.DOCUMENT_SRCS.CENTRESPECIMENSET, //IOParameters.DOCUMENT_SRCS documentSRC,
                    specimenResultsFilename, //String filename
                    null, //Long hjid
                    null, //Long submissionTrackerID
                    null); // String propertiesFilename
            ioparameters.put(CONNECTOR_NAMES.centreSpecimenSetIncarnator, input);
        }
        if (procedureResultsFilename != null) {
            input = ValidatorSupport.getIOParametersForInput(
                    IOParameters.DOCUMENT_SRCS.CENTREPROCEDURESET, //IOParameters.DOCUMENT_SRCS documentSRC,
                    procedureResultsFilename,//String filename
                    null,//Long hjid
                    null,//Long submissionTrackerID
                    null);// String propertiesFilename
            ioparameters.put(CONNECTOR_NAMES.centreProcedureSetIncarnator, input);
        }
        if (submissionID != null) {
            input = ValidatorSupport.getIOParametersForInput(
                    IOParameters.DOCUMENT_SRCS.SUBMISSIONSET_HJID, //IOParameters.DOCUMENT_SRCS documentSRC,
                    null,//String filename
                    submissionID,//Long hjid
                    null,//Long submissionTrackerID
                    submissionPersistenceFilename);// String propertiesFilename
            ioparameters.put(CONNECTOR_NAMES.submissionIncarnator, input);
        }
        if (submissionTrackerID != null) {
            input = ValidatorSupport.getIOParametersForInput(
                    IOParameters.DOCUMENT_SRCS.SUBMISSION_TRACKERID, //IOParameters.DOCUMENT_SRCS documentSRC,
                    null,//String filename
                    null,//Long hjid
                    submissionTrackerID,//Long submissionTrackerID
                    submissionPersistenceFilename);// String propertiesFilename
            ioparameters.put(CONNECTOR_NAMES.submissionIncarnator, input);
        }

        /*
         * resource
         *
         * All the browsers will browse the same data source.
         */

        resource = ValidatorSupport.getIOParametersForResource(externalResourcesLocationPersistenceFilename, externalResourcesLocationOnLocalDatabase);
        ioparameters.put(CONNECTOR_NAMES.resources, resource);

        /*
         * results
         */
        result = ValidatorSupport.getIOParametersForResult(specimenResultsFilename, procedureResultsFilename, submissionID == null ? submissionTrackerID : submissionID, submissionPersistenceFilename);
        ioparameters.put(CONNECTOR_NAMES.validationSetSerializer, result);

        result_report = ValidatorSupport.getIOParametersForResultReport(specimenResultsFilename, procedureResultsFilename, submissionID == null ? submissionTrackerID : submissionID, submissionPersistenceFilename);
        ioparameters.put(CONNECTOR_NAMES.validationReportSetSerializer, result_report);

        return ioparameters;
    }

    public static String getResultFilename(String filename) {
        return filename.substring(0, filename.lastIndexOf(".")) + "_validation.xml";
    }

    public static IOParameters getIOParametersForResult(String specimenResultsFilename,
            String procedureResultsFilename,
            Long submissionID,
            String submissionPersistenceFilename) {
        IOParameters iOParameters = new IOParameters();
        Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<>(IOParameters.IOS_PROPERTIES.class);
        iOParameters.setValues(values);


        if ((procedureResultsFilename != null && !procedureResultsFilename.equals(""))
                || (specimenResultsFilename != null && !specimenResultsFilename.equals(""))) {
            iOParameters.setIos(IOParameters.IOS.XML_FILE);
            values.put(IOParameters.IOS_PROPERTIES.CONTEXTPATH, ValidatorSupport.CONTEXTPATH_4_DATA);
            String filename = specimenResultsFilename != null && !specimenResultsFilename.equals("") ? specimenResultsFilename : procedureResultsFilename;
            values.put(IOParameters.IOS_PROPERTIES.XMLFILENAME, ValidatorSupport.getResultFilename(filename));
        }

        if (submissionID != null) {
            if (submissionPersistenceFilename != null) {
                iOParameters.setIos(IOParameters.IOS.EXTERNAL_DB_PROPERTIES);
            } else {
                iOParameters.setIos(IOParameters.IOS.DEFAULT_DB_PROPERTIES);
            }
            values.put(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME, ValidatorSupport.PERSISTENCEUNITNAME_4_DATA);
            values.put(IOParameters.IOS_PROPERTIES.PROPERTIES_FILENAME, submissionPersistenceFilename != null ? submissionPersistenceFilename : DEFAULT_HIBERNATE_CONFIGURATION_4_DATA_FILENAME);
        }
        return iOParameters;
    }

    public static String getResultReportFilename(String filename) {
        return filename.substring(0, filename.lastIndexOf(".")) + "_validation_report.xml";
    }

    public static IOParameters getIOParametersForResultReport(String specimenResultsFilename,
            String procedureResultsFilename,
            Long submissionID,
            String submissionPersistenceFilename) {
        IOParameters iOParameters = new IOParameters();
        Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<>(IOParameters.IOS_PROPERTIES.class);
        iOParameters.setValues(values);


        if ((procedureResultsFilename != null && !procedureResultsFilename.equals(""))
                || (specimenResultsFilename != null && !specimenResultsFilename.equals(""))) {
            iOParameters.setIos(IOParameters.IOS.XML_FILE);
            values.put(IOParameters.IOS_PROPERTIES.CONTEXTPATH, ValidatorSupport.CONTEXTPATH_4_DATA);
            String filename = specimenResultsFilename != null && !specimenResultsFilename.equals("") ? specimenResultsFilename : procedureResultsFilename;
            values.put(IOParameters.IOS_PROPERTIES.XMLFILENAME, getResultReportFilename(filename));
        }

        if (submissionID != null) {
            if (submissionPersistenceFilename != null) {
                iOParameters.setIos(IOParameters.IOS.EXTERNAL_DB_PROPERTIES);
            } else {
                iOParameters.setIos(IOParameters.IOS.DEFAULT_DB_PROPERTIES);
            }
            values.put(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME, ValidatorSupport.PERSISTENCEUNITNAME_4_DATA);

            values.put(IOParameters.IOS_PROPERTIES.PROPERTIES_FILENAME, submissionPersistenceFilename != null ? submissionPersistenceFilename : DEFAULT_HIBERNATE_CONFIGURATION_4_DATA_FILENAME);
        }

        return iOParameters;
    }

    private static void setupDerby() {

        String derbySystemHome = ValidatorSupport.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.setProperty("derby.system.home", derbySystemHome.substring(0, derbySystemHome.lastIndexOf(File.separator)));
        //
        System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");

    }

    /*
     * only supports EXTERNAL_DB_PROPERTIES (needs an external
     * hibernate.properties) and DEFAULT_DB_PROPERTIES that should trigger the
     * download of the resources if database not present
     */
    public static IOParameters getIOParametersForResource(String externalResourcesLocationPersistenceFilename,
            Boolean externalResourcesLocationOnLocalDatabase) {
        IOParameters iOParameters = new IOParameters();
        Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<>(IOParameters.IOS_PROPERTIES.class);
        iOParameters.setValues(values);

        if (externalResourcesLocationPersistenceFilename != null && !externalResourcesLocationPersistenceFilename.equals("")) {
            iOParameters.setIos(IOParameters.IOS.EXTERNAL_DB_PROPERTIES);
        }
        if (externalResourcesLocationOnLocalDatabase != null && externalResourcesLocationOnLocalDatabase) {
            iOParameters.setIos(IOParameters.IOS.DEFAULT_DB_PROPERTIES);
            //ValidatorSupport.setupDerby();
            externalResourcesLocationPersistenceFilename = ValidatorSupport.DEFAULT_HIBERNATE_CONFIGURATION_4_RESOURCES_FILENAME;
        }
        values.put(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME, ValidatorSupport.PERSISTENCEUNITNAME_4_RESOURCES);
        values.put(IOParameters.IOS_PROPERTIES.PROPERTIES_FILENAME, externalResourcesLocationPersistenceFilename);
        return iOParameters;
    }

    /*
     * either filename or (hjid and propertiesFilename) need to be different
     * from null
     *
     */
    public static IOParameters getIOParametersForInput(
            IOParameters.DOCUMENT_SRCS documentSRC,
            String filename,
            Long hjid,
            Long submissionTrackerID,
            String propertiesFilename) {
        //
        IOParameters iOParameters = new IOParameters();
        Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<>(IOParameters.IOS_PROPERTIES.class);
        iOParameters.setValues(values);
        iOParameters.setDoc_srcs(documentSRC);
        switch (documentSRC) {
            case CENTREPROCEDURESET:
            case CENTRESPECIMENSET:
            case SUBMISSIONSET:
                iOParameters.setIos(IOParameters.IOS.XML_FILE);
                values.put(IOParameters.IOS_PROPERTIES.CONTEXTPATH, ValidatorSupport.CONTEXTPATH_4_DATA);
                values.put(IOParameters.IOS_PROPERTIES.XMLFILENAME, filename);
                break;
            case CENTREPROCEDURESET_HJID:
            case CENTRESPECIMENSET_HJID:
                iOParameters.setIos(IOParameters.IOS.XML_FILE);
                iOParameters.setHjid(hjid);
                if (propertiesFilename != null) {
                    iOParameters.setIos(IOParameters.IOS.EXTERNAL_DB_PROPERTIES);
                } else {
                    iOParameters.setIos(IOParameters.IOS.DEFAULT_DB_PROPERTIES);
                    propertiesFilename = ValidatorSupport.DEFAULT_HIBERNATE_CONFIGURATION_4_DATA_FILENAME;
                }
                values.put(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME, ValidatorSupport.PERSISTENCEUNITNAME_4_DATA);
                values.put(IOParameters.IOS_PROPERTIES.PROPERTIES_FILENAME,propertiesFilename != null ? propertiesFilename : DEFAULT_HIBERNATE_CONFIGURATION_4_DATA_FILENAME);
                break;
            case SUBMISSIONSET_HJID:
                iOParameters.setHjid(hjid);
            case SUBMISSION_TRACKERID:
                iOParameters.setHjid(submissionTrackerID);
                if (propertiesFilename != null) {
                    iOParameters.setIos(IOParameters.IOS.EXTERNAL_DB_PROPERTIES);
                } else {
                    iOParameters.setIos(IOParameters.IOS.DEFAULT_DB_PROPERTIES);
                }
                values.put(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME, PERSISTENCEUNITNAME_4_DATA);
                values.put(IOParameters.IOS_PROPERTIES.PROPERTIES_FILENAME, propertiesFilename != null ? propertiesFilename : DEFAULT_HIBERNATE_CONFIGURATION_4_DATA_FILENAME);
        }
        return iOParameters;
    }
}
