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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mousephenotype.dcc.exportlibrary.xmlserialization.controls;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.xml.bind.JAXBException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class CoreSerializer {

    private static final Logger logger = LoggerFactory.getLogger(CoreSerializer.class);
    //
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    public static final String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    //
    private HibernateManager hibernateManager;
    //
    private CentreSpecimenSet centreSpecimenSet;
    private CentreProcedureSet centreProcedureSet;
    

    public CoreSerializer(String persistenceUnitname, Properties serializeProperties) throws PersistenceException {
        this.hibernateManager = new HibernateManager(serializeProperties,persistenceUnitname);
    }

    public CoreSerializer(HibernateManager persistenceManager) {
        this.hibernateManager = persistenceManager;
    }

    private void loadSpecimens(String filename) throws JAXBException, FileNotFoundException, IOException {
        logger.info("loading specimens from {}", filename);
        this.centreSpecimenSet = XMLUtils.unmarshal(CoreSerializer.CONTEXT_PATH, CentreSpecimenSet.class, filename);
        logger.info("{} unmarshalled succcessfully", filename);
    }

    private void loadProcedureResults(String filename) throws JAXBException, FileNotFoundException, IOException {
        logger.info("loading procedures from {}", filename);
        this.centreProcedureSet = XMLUtils.unmarshal(CoreSerializer.CONTEXT_PATH, CentreProcedureSet.class, filename);
        logger.info("{} unmarshalled succcessfully", filename);
    }

    private void persistSpecimens() throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException {
        logger.info("starting specimens persistance");
        this.hibernateManager.persist(centreSpecimenSet);
        logger.info("specimens persisted");
    }

    private void persistProcedureResults() throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException {
        logger.info("starting procedures persistance");
        this.hibernateManager.persist(centreProcedureSet);
        logger.info("procedures persisted");
    }

    private void processSpecimens(String specimenResultsFilename) throws JAXBException, FileNotFoundException, IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, IOException {
        this.loadSpecimens(specimenResultsFilename);
        if (this.centreSpecimenSet != null) {
            this.persistSpecimens();
        }
    }

    private void processProcedureResults(String procedureResultsFilename) throws JAXBException, FileNotFoundException, IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, IOException {
        this.loadProcedureResults(procedureResultsFilename);
        if (this.centreProcedureSet != null) {
            this.persistProcedureResults();
        }
    }

    public void close() throws IllegalArgumentException {
        this.hibernateManager.close();
    }

    public void fullClose() throws IllegalStateException {
        this.hibernateManager.close();
    }

    public void run(String specimenResultsFilename, String procedureResultsFilename) throws JAXBException, FileNotFoundException, IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, IOException {
        this.processSpecimens(specimenResultsFilename);
        this.processProcedureResults(procedureResultsFilename);
    }

    public void serializeSpecimensOnly(String specimenResultsFilename) throws JAXBException, FileNotFoundException, IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, IOException {
        this.processSpecimens(specimenResultsFilename);
    }

    public void serializeProceduresOnly(String procedureResultsFilename) throws JAXBException, FileNotFoundException, IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, IOException {
        this.processProcedureResults(procedureResultsFilename);
    }
}
