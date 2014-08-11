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
import java.io.FileInputStream;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import junitx.util.PrivateAccessor;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress.ImpressBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.support.RepeatedParameterInfo;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressProcedure;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

import org.slf4j.LoggerFactory;


@Ignore
public class CentreProcedureSetValidatorDSTest {
   static {
        System.setProperty("derby.system.home", System.getProperty("test.databases.folder"));
    }
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CentreProcedureSetValidatorDSTest.class);
    private static final String PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.xmlvalidation.external";
    private static HibernateManager hibernateManager = null;
    private static ImpressBrowser impressBrowser;

    @BeforeClass
    public static void setup() {
        Properties properties = null;
        Reader reader = null;
        try {
            //reader = new Reader(System.getProperty("test.databases.conf") +"/xmlvalidationresources.derby.properties");
            reader = new Reader("xmlvalidationresources.derby.properties");
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
       try {
           impressBrowser = new ImpressBrowser(hibernateManager);
       } catch (IllegalArgumentException iae) {
            logger.error("An IllegalArgument Exception was thown! This may well have been the result of the databases not being populated");
            Assert.fail();
        } catch (Exception ex) {
            logger.error("An exception was thrown!");
            Assert.fail();
        }


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
    public void testTable() {
        Table<String, BigInteger, List<RepeatedParameterInfo>> parameterIds = HashBasedTable.create();

        SimpleParameter parameter = new SimpleParameter();
        parameter.setParameterID("IMPC_BWT_001_001");
        List<RepeatedParameterInfo> put = RepeatedParameterInfo.put(parameterIds, new RepeatedParameterInfo(parameter, parameter.getParameterID(), parameter.getSequenceID(), ImpressParameterType.SIMPLE_PARAMETER, ImpressParameterType.SIMPLE_PARAMETER));

        Assert.assertTrue(put.size() == 1);

        put = RepeatedParameterInfo.put(parameterIds, new RepeatedParameterInfo(parameter, parameter.getParameterID(), parameter.getSequenceID(), ImpressParameterType.SIMPLE_PARAMETER, ImpressParameterType.SIMPLE_PARAMETER));
        Assert.assertTrue(put.size() == 2);

        parameter = new SimpleParameter();
        parameter.setParameterID("IMPC_BWT_001_001");
        parameter.setSequenceID(BigInteger.valueOf(1));
        put = RepeatedParameterInfo.put(parameterIds, new RepeatedParameterInfo(parameter, parameter.getParameterID(), parameter.getSequenceID(), ImpressParameterType.SIMPLE_PARAMETER, ImpressParameterType.SIMPLE_PARAMETER));
        Assert.assertTrue(put.size() == 1);

        parameter.setParameterID("IMPC_BWT_001_001");
        parameter.setSequenceID(BigInteger.valueOf(1));
        put = RepeatedParameterInfo.put(parameterIds, new RepeatedParameterInfo(parameter, parameter.getParameterID(), parameter.getSequenceID(), ImpressParameterType.SIMPLE_PARAMETER, ImpressParameterType.SIMPLE_PARAMETER));
        Assert.assertTrue(put.size() == 2);

        Assert.assertTrue(parameterIds.size() == 2);

        for (List<RepeatedParameterInfo> list : parameterIds.values()) {
            if (list.size() > 1) {
                logger.info(list.get(0).getErrorMessage(list.size()));
            }
        }
    }

    @Test
    public void test() {
        String contextPath = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(contextPath);
        } catch (JAXBException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        Unmarshaller unmarshaller = null;
        try {
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream("src/test/resources/data/IMPC_BWT_001_repeated_parameters.xml");
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        CentreProcedureSet centreProcedureSet = null;
        JAXBElement<CentreProcedureSet> element = null;
        try {
            element = unmarshaller.unmarshal(new StreamSource(fileInputStream), CentreProcedureSet.class);

        } catch (JAXBException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        try {
            fileInputStream.close();
        } catch (IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        centreProcedureSet = element.getValue();

        CentreProcedureSetValidator validator = new CentreProcedureSetValidator();
        ImpressProcedure impressProcedure = impressBrowser.getImpressProcedure(centreProcedureSet.getCentre().get(0).getPipeline(), centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureID());

        Table<String, BigInteger, List<RepeatedParameterInfo>> parameterIds = null;
        
        try {
            parameterIds= (Table)PrivateAccessor.invoke(validator, "composeTable", new Class[]{Procedure.class, ImpressProcedure.class}, new Object[]{centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure(), impressProcedure});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }

        
        Assert.assertNotNull(parameterIds);
        
        try {
            PrivateAccessor.invoke(validator, "checkRepeatedParameters", new Class[]{Table.class}, new Object[]{parameterIds});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }

    }
}
