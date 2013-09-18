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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.CONNECTOR_NAMES;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.IOS;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.consoleapps.support.ValidatorSupport;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.imits.IMITSBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress.ImpressBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.mgi.MGIBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.statuscodes.StatusCodesBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.support.SpecimenWSclient;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class IOController {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(IOController.class);
    private static final String EXTERNAL_PROPERTIES_FILENAME = "external.properties";
    private String externalPropertiesFilename;
    //
    private PropertiesConfiguration propertiesConfiguration = null;
    //
    private Incarnator<CentreProcedureSet> centreProcedureSetIncarnator;
    private Incarnator<CentreSpecimenSet> centreSpecimenSetIncarnator;
    private SubmissionSetIncarnator submissionSetIncarnator;
    //
    private Serializer<ValidationSet> validationSetSerializer;
    private Serializer<ValidationReportSet> validationReportSetSerializer;
    //
    private ImpressBrowser impressBrowser;
    private MGIBrowser mgiBrowser;
    private IMITSBrowser imitsBrowser;
    private StatusCodesBrowser statusCodesBrowser;
    //
    //
    private Table<String, Integer, HibernateManager> hibernateManagers = HashBasedTable.create();

    public IOController() {
    }

    public Properties getProperties(String code) {
        if (this.propertiesConfiguration == null) {
            this.initializeExternalProperties();
        }
        if (this.propertiesConfiguration != null) {
            Properties properties = new Properties();
            Iterator<String> keys = propertiesConfiguration.getKeys(code);
            String key = null;
            while (keys.hasNext()) {
                key = keys.next();
                properties.put(key, propertiesConfiguration.getProperty(key));
            }
            return properties;
        }
        return null;
    }

    private void initializeExternalProperties() {
        logger.info("initializing external properties from {}", this.externalPropertiesFilename == null ? EXTERNAL_PROPERTIES_FILENAME : this.externalPropertiesFilename);
        try {
            propertiesConfiguration = new PropertiesConfiguration(this.externalPropertiesFilename == null ? EXTERNAL_PROPERTIES_FILENAME : this.externalPropertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("error loading external configuration file {}", this.externalPropertiesFilename == null ? EXTERNAL_PROPERTIES_FILENAME : this.externalPropertiesFilename, ex);
        }
    }

    public Collection<HibernateManager> getHibernateManagers() {
        return Collections.unmodifiableCollection(this.hibernateManagers.values());
    }

    public void clearHibernateManagers() {
        this.hibernateManagers.clear();
    }

    public void closeHiberanateManagers() throws HibernateException {
        logger.info("closing {} hibernate managers", hibernateManagers.values().size());
        for (HibernateManager hibernateManager : hibernateManagers.values()) {
            logger.info("closing persistence unitname {} ",hibernateManager.getPersistencename());
            hibernateManager.close();
        }
    }

    /*
     * instantiates all the incarnators and serializers needed for validation
     */
    public IOParameters.DOCUMENTS setup(Map<CONNECTOR_NAMES, IOParameters> ioparametersMap) throws HibernateException, Exception {
        IOParameters.DOCUMENTS doc = null;

        for (Entry<CONNECTOR_NAMES, IOParameters> entry : ioparametersMap.entrySet()) {
            if (entry.getKey().equals(CONNECTOR_NAMES.centreProcedureSetIncarnator)) {
                logger.info("instantiating CentreProcedureSetIncarnator");
                this.getCentreProcedureSetIncarnator(entry.getValue().getIos(), entry.getValue().getValues());
                doc = IOParameters.DOCUMENTS.CENTREPROCEDURESET;
                continue;
            }
            if (entry.getKey().equals(CONNECTOR_NAMES.centreSpecimenSetIncarnator)) {
                logger.info("instantiating centreSpecimenSetIncarnator");
                this.getCentreSpecimenSetIncarnator(entry.getValue().getIos(), entry.getValue().getValues());
                doc = IOParameters.DOCUMENTS.CENTRESPECIMENSET;
                continue;
            }
            if (entry.getKey().equals(CONNECTOR_NAMES.submissionIncarnator)) {
                logger.info("instantiating submissionSetIncarnator");
                doc = this.instantiateSubmissionIncarnator(entry.getValue().getIos(), entry.getValue());
                continue;
            }

            if (entry.getKey().equals(CONNECTOR_NAMES.validationSetSerializer)) {
                logger.info("instantiating validationSetSerializer");
                this.getValidationSetSerializer(entry.getValue().getIos(), entry.getValue().getValues());
                continue;
            }
            if (entry.getKey().equals(CONNECTOR_NAMES.validationReportSetSerializer)) {
                logger.info("instantiating validationReportSetSerializer");
                this.getValidationReportSetSerializer(entry.getValue().getIos(), entry.getValue().getValues());
                continue;
            }
            if (entry.getKey().equals(CONNECTOR_NAMES.resources)) {//here we instantiate every browser
                logger.info("instantiating impressBrowser");
                this.getImpressBrowser(entry.getValue().getIos(), entry.getValue().getValues());
                logger.info("instantiating MGIBrowser");
                this.getMGIBrowser(entry.getValue().getIos(), entry.getValue().getValues());
                logger.info("instantiating IMitsBrowser");
                this.getImitsBrowser(entry.getValue().getIos(), entry.getValue().getValues());
                this.getStatusCodesBrowser(entry.getValue().getIos(), entry.getValue().getValues());
                continue;
            }
        }
        return doc;
    }

    protected HibernateManager getHibernateManager(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        Properties hibernateProperties = null;
        Reader reader = new Reader(properties.get(IOParameters.IOS_PROPERTIES.PROPERTIES_FILENAME));
        hibernateProperties = reader.getProperties();
        if (this.hibernateManagers.get(properties.get(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME), hibernateProperties.hashCode()) != null) {
            logger.info("hibernate manager already instantiated");
            return this.hibernateManagers.get(properties.get(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME), hibernateProperties.hashCode());
        } else {
            logger.info("new hibernate manager instantiated");
            HibernateManager hibernateManager = new HibernateManager(hibernateProperties, properties.get(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME));
            this.hibernateManagers.put(properties.get(IOParameters.IOS_PROPERTIES.PERSISTENCEUNITNAME), hibernateProperties.hashCode(), hibernateManager);
            return hibernateManager;
        }
    }

    public <T> Incarnator<T> getIncarnator(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        switch (ios) {
            case XML_FILE: //String contextPath, String xmlFilename
                logger.info(" XMLFile incarnator for {}", properties.get(IOParameters.IOS_PROPERTIES.XMLFILENAME));
                return new Incarnator<>(properties.get(IOParameters.IOS_PROPERTIES.CONTEXTPATH), properties.get(IOParameters.IOS_PROPERTIES.XMLFILENAME));
            case EXTERNAL_DB_PROPERTIES: //String persistenceUnitName, Properties properties
            case DEFAULT_DB_PROPERTIES:
                return new Incarnator<>(this.getHibernateManager(ios, properties));

            case WEB_SERVICES:
                logger.info("DB Incarnator for WEB SERVICES");

                throw new UnsupportedOperationException("incarnator for web services not ready");

        }
        return null;
    }

    /**
     * @return the xmlValidationResources
     */
    public ImpressBrowser getImpressBrowser(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        if (this.impressBrowser == null) {
            switch (ios) {
                case XML_FILE: //String contextPath, String xmlFilename
                case WEB_SERVICES:
                    throw new UnsupportedOperationException("IMPRESS BROWSER from xml file not supported");
                case EXTERNAL_DB_PROPERTIES:
                case DEFAULT_DB_PROPERTIES:
                case TEST_DB_PROPERTIES:
                    this.impressBrowser = new ImpressBrowser(this.getHibernateManager(ios, properties));
            }
        }

        return impressBrowser;
    }

    public MGIBrowser getMGIBrowser(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        if (this.mgiBrowser == null) {
            switch (ios) {
                case XML_FILE: //String contextPath, String xmlFilename
                case WEB_SERVICES:
                    throw new UnsupportedOperationException("MGI BROWSER from xml file not supported");
                case EXTERNAL_DB_PROPERTIES:
                case DEFAULT_DB_PROPERTIES:
                case TEST_DB_PROPERTIES:
                    this.mgiBrowser = new MGIBrowser(this.getHibernateManager(ios, properties));
            }
        }
        return this.mgiBrowser;
    }
    
        public StatusCodesBrowser getStatusCodesBrowser(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        if (this.statusCodesBrowser == null) {
            switch (ios) {
                case XML_FILE: //String contextPath, String xmlFilename
                case WEB_SERVICES:
                    throw new UnsupportedOperationException("StatusCode BROWSER from xml file not supported");
                case EXTERNAL_DB_PROPERTIES:
                case DEFAULT_DB_PROPERTIES:
                case TEST_DB_PROPERTIES:
                    this.statusCodesBrowser = new StatusCodesBrowser(this.getHibernateManager(ios, properties));
            }
        }
        return this.statusCodesBrowser;
    }
    

    public IMITSBrowser getImitsBrowser(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        if (this.imitsBrowser == null) {
            switch (ios) {
                case XML_FILE: //String contextPath, String xmlFilename
                case WEB_SERVICES:
                    throw new UnsupportedOperationException("IMITS browser from xml file not supported");
                case EXTERNAL_DB_PROPERTIES:
                case DEFAULT_DB_PROPERTIES:
                case TEST_DB_PROPERTIES:
                    this.imitsBrowser = new IMITSBrowser(this.getHibernateManager(ios, properties));
                    this.imitsBrowser.setConnectionProperties(this.getProperties(IOParameters.IMITS_PROPERTIES.propertyCode));
            }
        }
        return imitsBrowser;
    }

    /**
     * @return the centreProcedureSetIncarnator
     */
    public Incarnator<CentreProcedureSet> getCentreProcedureSetIncarnator(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        if (this.getCentreProcedureSetIncarnator() == null) {
            this.centreProcedureSetIncarnator = this.getIncarnator(ios, properties);
        }
        return getCentreProcedureSetIncarnator();
    }

    /**
     * @return the centreSpecimenSetIncarnator
     */
    public Incarnator<CentreSpecimenSet> getCentreSpecimenSetIncarnator(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        if (this.getCentreSpecimenSetIncarnator() == null) {
            this.centreSpecimenSetIncarnator = this.getIncarnator(ios, properties);
        }
        return getCentreSpecimenSetIncarnator();
    }

    /**
     * @return the validationSetSerializer
     */
    public Serializer<ValidationSet> getValidationSetSerializer(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        if (this.getValidationSetSerializer() == null) {
            switch (ios) {
                case XML_FILE: //String contextPath, String xmlFilename
                    logger.info("instantiating ValidationSetSerializer on XML FILE {}", properties.get(IOParameters.IOS_PROPERTIES.XMLFILENAME));
                    this.validationSetSerializer = new Serializer<>(properties.get(IOParameters.IOS_PROPERTIES.CONTEXTPATH),
                            properties.get(IOParameters.IOS_PROPERTIES.XMLFILENAME));
                    break;
                case EXTERNAL_DB_PROPERTIES:
                case DEFAULT_DB_PROPERTIES: //String persistenceUnitName, Properties properties
                    this.validationSetSerializer = new Serializer<>(this.getHibernateManager(ios, properties));
            }
        }
        return getValidationSetSerializer();
    }

    /**
     * @return the validationReportSetSerializer
     */
    public Serializer<ValidationReportSet> getValidationReportSetSerializer(IOS ios, Map<IOParameters.IOS_PROPERTIES, String> properties) throws HibernateException, Exception {
        if (this.getValidationReportSetSerializer() == null) {

            switch (ios) {
                case XML_FILE: //String contextPath, String xmlFilename
                    logger.info("instantiating ValidationReportSetSerializer on XML FILE {}", properties.get(IOParameters.IOS_PROPERTIES.XMLFILENAME));
                    this.validationReportSetSerializer = new Serializer<>(properties.get(IOParameters.IOS_PROPERTIES.CONTEXTPATH), properties.get(IOParameters.IOS_PROPERTIES.XMLFILENAME));
                    break;
                case EXTERNAL_DB_PROPERTIES:
                case DEFAULT_DB_PROPERTIES: //String persistenceUnitName, Properties properties

                    this.validationReportSetSerializer = new Serializer<>(this.getHibernateManager(ios, properties));
            }
        }
        return getValidationReportSetSerializer();
    }

    /**
     * @return the centreProcedureSetIncarnator
     */
    public Incarnator<CentreProcedureSet> getCentreProcedureSetIncarnator() {
        return centreProcedureSetIncarnator;
    }

    /**
     * @return the centreSpecimenSetIncarnator
     */
    public Incarnator<CentreSpecimenSet> getCentreSpecimenSetIncarnator() {
        return centreSpecimenSetIncarnator;
    }

    /**
     * @return the validationSetSerializer
     */
    public Serializer<ValidationSet> getValidationSetSerializer() {
        return validationSetSerializer;
    }

    /**
     * @return the validationReportSetSerializer
     */
    public Serializer<ValidationReportSet> getValidationReportSetSerializer() {
        return validationReportSetSerializer;
    }

    /**
     * @return the impressBrowser
     */
    public ImpressBrowser getImpressBrowser() {
        return impressBrowser;
    }

    public MGIBrowser getMGIBrowser() {
        return mgiBrowser;
    }

    public IMITSBrowser getIMITSBrowser() {
        return imitsBrowser;
    }

    public StatusCodesBrowser getStatusCodesBrowser() {
    return statusCodesBrowser;
    }
    
    
    public SubmissionSetIncarnator getSubmissionSetIncarnator() {
        return submissionSetIncarnator;
    }

    public String getExternalPropertiesFilename() {
        return externalPropertiesFilename;
    }

    public void setExternalPropertiesFilename(String externalPropertiesFilename) {
        this.externalPropertiesFilename = externalPropertiesFilename;
    }

    /*
     *  loading submission has to happen as to see what kind of content does it carry
     */
    private IOParameters.DOCUMENTS instantiateSubmissionIncarnator(IOS ios, IOParameters ioParameters) throws HibernateException, Exception {
        switch (ioParameters.getDoc_srcs()) {
            case SUBMISSIONSET:
                this.submissionSetIncarnator = new SubmissionSetIncarnator(ValidatorSupport.CONTEXTPATH_4_DATA, ValidatorSupport.PERSISTENCEUNITNAME_4_DATA);
                this.submissionSetIncarnator.loadFromSubmissionSet();
                return this.submissionSetIncarnator.getDoc();
            case SUBMISSIONSET_HJID:
                this.submissionSetIncarnator = new SubmissionSetIncarnator(this.getHibernateManager(ios, ioParameters.getValues()));
                this.submissionSetIncarnator.loadSubmission(ioParameters.getHjid());
                return this.submissionSetIncarnator.getDoc();
            case SUBMISSION_TRACKERID:
                this.submissionSetIncarnator = new SubmissionSetIncarnator(this.getHibernateManager(ios, ioParameters.getValues()));
                this.submissionSetIncarnator.loadSubmissionFromTrackerID(ioParameters.getHjid());
                return this.submissionSetIncarnator.getDoc();
        }

        return null;
    }

    public SpecimenWSclient getSpecimenWSclient() {
        /*
         specimen.ws.url.option=public
         specimen.ws.url.public=http://mousephenotype.org/phenodcc-validation-ws/resources/mousesummary/byspecimenid
         */
        Properties properties = this.getProperties(SpecimenWSclient.SPECIMEN_WS_URL_PROPERTY_PREFIX); //"specimen.ws.url";
        if (properties != null) {
            String sufix = properties.getProperty(SpecimenWSclient.SPECIMEN_WS_URL_OPTION_PROPERTY);//specimen.ws.url + ".option";

            SpecimenWSclient specimenWSclient = new SpecimenWSclient(properties.getProperty(SpecimenWSclient.SPECIMEN_WS_URL_PROPERTY_PREFIX + "." + sufix));
            return specimenWSclient;
        }
        return null;
    }

    
}
