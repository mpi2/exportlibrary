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

import com.google.common.collect.Table;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;

import javax.xml.bind.JAXBException;
import junitx.util.PrivateAccessor;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.CentreProcedureSetValidator;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress.ImpressBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.utils.ValidationException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressProcedure;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.LoggerFactory;

public class RepeatedParameterInfoTest {

    static {
        System.setProperty("derby.system.home", System.getProperty("test.databases.folder"));
    }
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(RepeatedParameterInfoTest.class);
    private static final String filename = "src/test/resources/data/multiple_repeats.experiment.impc.xml";
    private static CentreProcedureSet centreProcedureSet;
    private static CentreProcedureSetValidator centreProcedureSetValidator;
    private static final String PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.xmlvalidation.external";
    private static HibernateManager hibernateManager = null;
    private static ImpressBrowser impressBrowser;

    @BeforeClass
    public static void setup() {
        try {
            centreProcedureSet = XMLUtils.unmarshal(XMLUtils.CONTEXTPATH, CentreProcedureSet.class, filename);
        } catch (JAXBException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedureSet);
        centreProcedureSetValidator = new CentreProcedureSetValidator();
        //
        Properties properties = null;
        Reader reader = null;
        try {
            reader = new Reader(System.getProperty("test.databases.conf") + "/xmlvalidationresources.derby.properties");
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        properties = reader.getProperties();

        Assert.assertNotNull(properties);

        try {
            hibernateManager = new HibernateManager(properties, PERSISTENCEUNITNAME);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        impressBrowser = new ImpressBrowser(hibernateManager);

        Assert.assertNotNull(impressBrowser);
        try {
            impressBrowser.loadPipeline("IMPC_001");
        } catch (IllegalStateException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (QueryTimeoutException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (TransactionRequiredException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (PessimisticLockException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (LockTimeoutException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (PersistenceException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (JAXBException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }

    }

    @AfterClass
    public static void tearDown() {
        if (hibernateManager != null) {
            hibernateManager.close();
        }
    }

    @Test
    public void foo() {
        ImpressProcedure impressProcedure = null;
        Table<String, BigInteger, List<RepeatedParameterInfo>> parameterIds = null;
        for (CentreProcedure centre : centreProcedureSet.getCentre()) {
            for (Experiment experiment : centre.getExperiment()) {

                impressProcedure = impressBrowser.getImpressProcedure(centre.getPipeline(), experiment.getProcedure().getProcedureID());
                if (impressProcedure != null) {
                    try {
                        parameterIds = (Table) PrivateAccessor.invoke(centreProcedureSetValidator, "composeTable", new Class[]{Procedure.class, ImpressProcedure.class}, new Object[]{centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure(), impressProcedure});
                    } catch (Throwable ex) {
                        logger.error("", ex);
                        Assert.fail();
                    }

                    if (impressProcedure != null) {
                        try {
                            PrivateAccessor.invoke(centreProcedureSetValidator, "checkRepeatedParameters", new Class[]{Table.class}, new Object[]{parameterIds});
                        } catch (Throwable ex) {
                            logger.error("", ex);
                            Assert.fail();
                        }
                    } else {
                        logger.error("cannot find procedure {}  on pipeline {}", experiment.getProcedure().getProcedureID(), centre.getPipeline());
                    }
                }else{
                    logger.info("no impress procedure found for pipeline {}, procedureID {}", centre.getPipeline(), experiment.getProcedure().getProcedureID());
                }
            }
        }
        List<ValidationException> errorExceptions = centreProcedureSetValidator.getErrorExceptions();
        for (ValidationException ex : errorExceptions) {
            logger.info("{}", ex.toString());
        }

    }
}
