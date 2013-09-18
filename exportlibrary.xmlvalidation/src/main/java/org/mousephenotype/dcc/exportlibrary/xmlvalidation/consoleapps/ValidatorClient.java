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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.consoleapps;

import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.CentreProcedureSetValidator;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.Incarnator;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.imits.IMITSBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress.ImpressBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.mgi.MGIBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.statuscodes.StatusCodesBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.support.SpecimenWSclient;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class ValidatorClient {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(ValidatorClient.class);
    private ValidationSet validationSet;
    private CentreProcedureSetValidator centreProcedureSetValidator;
    private final String hibernateManagerXMLResourcesFilename;
    private final Properties hibernateManagerXMLResourcesProperties;
    private final HibernateManager hibernateManager;
    private final Reader reader;
    
    public ValidatorClient(String hibernateManagerXMLResourcesFilename) throws ConfigurationException, HibernateException {
        this.hibernateManagerXMLResourcesFilename = hibernateManagerXMLResourcesFilename;
        reader = new Reader(hibernateManagerXMLResourcesFilename);
        hibernateManagerXMLResourcesProperties = reader.getProperties();
        hibernateManager = new HibernateManager(hibernateManagerXMLResourcesProperties, "org.mousephenotype.dcc.exportlibrary.xmlvalidation.external");
    }

    public void run() {
        try {
            this.centreProcedureSetValidator = new CentreProcedureSetValidator(this.getCentreProcedureSet(),
                    this.getValidationSet(), this.getValidationReportSet(), this.getXMLValidationResources(), this.getSpecimenWSclient());
        } catch (IllegalStateException ex) {
            logger.error("", ex);
        } catch (QueryTimeoutException ex) {
            logger.error("", ex);
        } catch (TransactionRequiredException ex) {
            logger.error("", ex);
        } catch (PessimisticLockException ex) {
            logger.error("", ex);
        } catch (LockTimeoutException ex) {
            logger.error("", ex);
        } catch (PersistenceException ex) {
            logger.error("", ex);
        } catch (JAXBException ex) {
            logger.error("", ex);
        } catch (Exception ex) {
            logger.error("", ex);
        }
        this.centreProcedureSetValidator.validateWithHandler();
        this.centreProcedureSetValidator.compileValidationSet();
    }
    
    

    //from your db
    private CentreProcedureSet getCentreProcedureSet() {
        return null;
    }

    //just instantiate a container
    private ValidationSet getValidationSet() {
        if(this.validationSet == null)
            this.validationSet = new ValidationSet();
        return this.validationSet;
    }
//just instantiate a container

    private ValidationReportSet getValidationReportSet() {
        return new ValidationReportSet();
    }

    private Map<IOParameters.VALIDATIONRESOURCES_IDS, Incarnator<?>> getXMLValidationResources() throws JAXBException, FileNotFoundException, Exception {
        Map<IOParameters.VALIDATIONRESOURCES_IDS, Incarnator<?>> xmlValidationResources = new EnumMap<>(IOParameters.VALIDATIONRESOURCES_IDS.class);
        xmlValidationResources.put(IOParameters.VALIDATIONRESOURCES_IDS.ImpressBrowser, new ImpressBrowser(hibernateManager));
        xmlValidationResources.put(IOParameters.VALIDATIONRESOURCES_IDS.Imits, new IMITSBrowser(hibernateManager));
        xmlValidationResources.put(IOParameters.VALIDATIONRESOURCES_IDS.MGIBrowser, new MGIBrowser(hibernateManager));
        xmlValidationResources.put(IOParameters.VALIDATIONRESOURCES_IDS.Statuscodes, new StatusCodesBrowser(hibernateManager));
        
        return xmlValidationResources;
    }

    private SpecimenWSclient getSpecimenWSclient() {
        String url = "http://mousephenotype.org/phenodcc-validation-ws/resources/mousesummary/byspecimenid";
        return new SpecimenWSclient(url);
    }
}
