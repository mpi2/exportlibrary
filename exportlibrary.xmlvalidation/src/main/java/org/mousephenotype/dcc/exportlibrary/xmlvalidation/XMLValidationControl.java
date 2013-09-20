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

import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Map;
import javax.persistence.*;
import javax.xml.bind.JAXBException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ResourceVersion;
import org.mousephenotype.dcc.exportlibrary.fullTraverser.Traverser;

import org.mousephenotype.dcc.exportlibrary.validationRemover.ValidationRemover;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.CONNECTOR_NAMES;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.DOCUMENTS;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.DOCUMENT_SRCS;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class XMLValidationControl {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLValidationControl.class);
    //
    private final IOController ioController;
    private final EntityController entityController;
    private final ValidatorController validatorController;
    private ValidationRemover validationRemover;
    private Traverser traverser;
    //

    public XMLValidationControl() {
        this.ioController = new IOController();
        this.entityController = new EntityController();
        this.validatorController = new ValidatorController();
    }

    private IOParameters getDataToValidate(Map<CONNECTOR_NAMES, IOParameters> ioparametersMap) {
        if (ioparametersMap.containsKey(CONNECTOR_NAMES.centreProcedureSetIncarnator)) {
            return ioparametersMap.get(CONNECTOR_NAMES.centreProcedureSetIncarnator);
        }
        if (ioparametersMap.containsKey(CONNECTOR_NAMES.centreSpecimenSetIncarnator)) {
            return ioparametersMap.get(CONNECTOR_NAMES.centreSpecimenSetIncarnator);
        }
        if (ioparametersMap.containsKey(CONNECTOR_NAMES.submissionIncarnator)) {
            return ioparametersMap.get(CONNECTOR_NAMES.submissionIncarnator);
        }

        return null;
    }

    public void run(Map<CONNECTOR_NAMES, IOParameters> ioparametersMap, String externalConfigurationFilename) throws JAXBException, FileNotFoundException, Exception {
        this.ioController.setExternalPropertiesFilename(externalConfigurationFilename);
        logger.info("setting up i/o");
        DOCUMENTS doc = this.getIoController().setup(ioparametersMap);
        logger.info("loading data to validate");
        IOParameters dataToValidate = getDataToValidate(ioparametersMap);
        this.loadDataToValidate(doc, dataToValidate.getDoc_srcs(), dataToValidate.getHjid(), dataToValidate.getHjid(), dataToValidate.getHjid());


        IOParameters ipp = null;

        if (ioparametersMap.containsKey(CONNECTOR_NAMES.centreProcedureSetIncarnator)) {
            ipp = ioparametersMap.get(CONNECTOR_NAMES.centreProcedureSetIncarnator);
        }
        if (ioparametersMap.containsKey(CONNECTOR_NAMES.centreSpecimenSetIncarnator)) {
            ipp = ioparametersMap.get(CONNECTOR_NAMES.centreSpecimenSetIncarnator);
        }
        if (ioparametersMap.containsKey(CONNECTOR_NAMES.submissionIncarnator)) {
            ipp = ioparametersMap.get(CONNECTOR_NAMES.submissionIncarnator);
        }

        if (ipp.getIos().equals(IOParameters.IOS.EXTERNAL_DB_PROPERTIES)
                || ipp.getIos().equals(IOParameters.IOS.DEFAULT_DB_PROPERTIES)) {
            logger.error("removing previous validation exceptions");

            try {
                this.validationRemover = new ValidationRemover(this.getIoController().getHibernateManager(ipp.getIos(), ipp.getValues()));
                validationRemover.run(ipp.getHjid());
            } catch (Exception ex) {
                logger.error("exception thrown on removing validations", ex.getMessage());
            }

            logger.info("validating process running");
            this.validate(doc);
            logger.info("serializing results");
            this.serializeResults(doc);



            try {
                traverser = new Traverser(this.getIoController().getHibernateManager(ipp.getIos(), ipp.getValues()));
                traverser.run(dataToValidate.getHjid(), new ResourceVersion());
            } catch (Exception ex) {
                logger.error("exception thrown building validation reports", ex);
            }

        }

        logger.info("closing infrastructure");
        this.close();
    }

    private void loadDataToValidate(DOCUMENTS doc, DOCUMENT_SRCS dsrc, Long centreset_hijd, Long submission_trackerID, Long submissionSet_hjid) throws JAXBException, FileNotFoundException, Exception {
        switch (doc) {
            case CENTREPROCEDURESET:
                switch (dsrc) {
                    case CENTREPROCEDURESET:
                        this.getEntityController().setCentreProcedureSet(this.getIoController().getCentreProcedureSetIncarnator().load(CentreProcedureSet.class));
                        return;
                    case CENTREPROCEDURESET_HJID:
                        this.getEntityController().setCentreProcedureSet(this.getIoController().getCentreProcedureSetIncarnator().load(null, centreset_hijd));
                        return;
                    case SUBMISSIONSET:
                    case SUBMISSIONSET_HJID:
                    case SUBMISSION_TRACKERID:
                        this.getEntityController().setCentreProcedureSet(this.getIoController().getSubmissionSetIncarnator().getCentreProcedureSet());
                        return;
                }

            case CENTRESPECIMENSET:
                switch (dsrc) {
                    case CENTRESPECIMENSET:
                        this.getEntityController().setCentreSpecimenSet(this.getIoController().getCentreSpecimenSetIncarnator().load(CentreSpecimenSet.class));
                        return;
                    case CENTRESPECIMENSET_HJID:
                        this.getEntityController().setCentreSpecimenSet(this.getIoController().getCentreSpecimenSetIncarnator().load(null, centreset_hijd));
                        return;
                    case SUBMISSIONSET:
                    case SUBMISSIONSET_HJID:
                    case SUBMISSION_TRACKERID:
                        this.getEntityController().setCentreSpecimenSet(this.getIoController().getSubmissionSetIncarnator().getCentreSpecimenSet());
                }
        }
    }
// Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<IOParameters.IOS_PROPERTIES, String>(IOParameters.IOS_PROPERTIES.class);

    private void setupValidationInfrastructure(DOCUMENTS doc) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException, LockTimeoutException, PersistenceException, JAXBException, FileNotFoundException, Exception {
        Map<IOParameters.VALIDATIONRESOURCES_IDS, Incarnator<?>> xmlValidationResources = new EnumMap<>(IOParameters.VALIDATIONRESOURCES_IDS.class);
        xmlValidationResources.put(IOParameters.VALIDATIONRESOURCES_IDS.ImpressBrowser, this.ioController.getImpressBrowser());
        xmlValidationResources.put(IOParameters.VALIDATIONRESOURCES_IDS.Imits, this.ioController.getIMITSBrowser());
        xmlValidationResources.put(IOParameters.VALIDATIONRESOURCES_IDS.MGIBrowser, this.ioController.getMGIBrowser());
        xmlValidationResources.put(IOParameters.VALIDATIONRESOURCES_IDS.Statuscodes, this.ioController.getStatusCodesBrowser());

        switch (doc) {
            case CENTREPROCEDURESET:
                CentreProcedureSetValidator centreProcedureSetValidator =
                        new CentreProcedureSetValidator(this.entityController.getCentreProcedureSet(),
                        this.entityController.getValidationSet(),
                        this.entityController.getValidationReportSet(),
                        xmlValidationResources, this.ioController.getSpecimenWSclient());
                this.validatorController.setCentreProcedureSetValidator(centreProcedureSetValidator);
                break;
            case CENTRESPECIMENSET:

                CentreSpecimenSetValidator centreSpecimenSetValidator =
                        new CentreSpecimenSetValidator(this.entityController.getCentreSpecimenSet(),
                        this.entityController.getValidationSet(),
                        this.entityController.getValidationReportSet(),
                        xmlValidationResources);
                this.validatorController.setCentreSpecimenSetValidator(centreSpecimenSetValidator);
                break;
        }
    }

    private void validate(DOCUMENTS doc) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException, LockTimeoutException, PersistenceException, JAXBException, FileNotFoundException, Exception {
        this.setupValidationInfrastructure(doc);
        switch (doc) {
            case CENTREPROCEDURESET:
                this.validatorController.getCentreProcedureSetValidator().validateWithHandler();
                this.validatorController.getCentreProcedureSetValidator().compileValidationSet();
                break;
            case CENTRESPECIMENSET:
                this.validatorController.getCentreSpecimenSetValidator().validateWithHandler();
                this.validatorController.getCentreSpecimenSetValidator().compileValidationSet();
        }
    }

    private void serializeResults(DOCUMENTS doc) throws JAXBException, IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, RuntimeException, Exception {
        this.ioController.getValidationSetSerializer().serialize(this.entityController.getValidationSet(), ValidationSet.class);
        //this.ioController.getValidationReportSetSerializer().serialize(this.entityController.getValidationReportSet(), ValidationReportSet.class);
    }

    public void close() {
        logger.info("closing {} hibernate managers", this.ioController.getHibernateManagers().size());
        for (HibernateManager hibernateManager : this.ioController.getHibernateManagers()) {
            try {
                logger.info("closing persistence unitname {} ", hibernateManager.getPersistencename());
                hibernateManager.close();
            } catch (HibernateException ex) {
                logger.error("cannot close hibernate manager for {}", ex, hibernateManager.getPersistencename());
            }

        }
        this.ioController.clearHibernateManagers();
    }

    /**
     * @return the ioController
     */
    public IOController getIoController() {
        return ioController;
    }

    /**
     * @return the entityController
     */
    public EntityController getEntityController() {
        return entityController;
    }

    /**
     * @return the validatorController
     */
    public ValidatorController getValidatorController() {
        return validatorController;
    }
}
