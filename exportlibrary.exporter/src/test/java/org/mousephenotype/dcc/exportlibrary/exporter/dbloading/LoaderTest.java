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
package org.mousephenotype.dcc.exportlibrary.exporter.dbloading;

import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.exportlibrary.exporter.App;
import org.mousephenotype.dcc.utils.io.conf.FileReader;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoaderTest {
    
    private static final Logger logger = LoggerFactory.getLogger(LoaderTest.class);
    private static HibernateManager hibernateManager;
    private static Loader loader;
    private static final String colonyID = "MEFJ";
    private static final String ACROSS_DATABASES = "across_databases.sql";
    
    @BeforeClass
    public static void setup() {
        Properties properties = null;
        Reader reader = null;
        try {
            reader = new Reader("localhost.mysql.properties");
            //reader = new Reader("live.mysql.properties");
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        properties = reader.getProperties();
        try {
            hibernateManager = new HibernateManager(properties, "org.mousephenotype.dcc.exportlibrary.datastructure");
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        
        loader = new Loader(hibernateManager);
    }
    
    @Ignore
    @Test
    public void loadColonyIDTest() {
        CentreSpecimenSet colony = null;
        try {
            colony = loader.getColony(colonyID);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(colony);
        Assert.assertTrue(!colony.getCentre().isEmpty());
    }
    
    @Ignore
    @Test
    public void getValidMutantsTest() {
        CentreSpecimenSet validMutants = null;
        try {
            validMutants = loader.getValidMutants();
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(validMutants);
        Assert.assertTrue(!validMutants.getCentre().isEmpty());
    }
    
    @Ignore
    @Test
    public void getSingleColonyIDTest() {
        CentreSpecimenSet validMutants = null;
        try {
            validMutants = loader.getSingleColonyID();
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(validMutants);
        Assert.assertTrue(!validMutants.getCentre().isEmpty());
        logger.info("retrieved {} specimens",validMutants.getCentre().size());
    }
    
    
    
    @Test
    public void testGetValidExperiments(){
        String validExperimentscolonyID = "BL1324";
        CentreProcedureSet centreProcedureSet = null;
        try {
            centreProcedureSet = loader.getValidExperiments(validExperimentscolonyID);
        }catch(Exception ex){
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedureSet);
        
        
        String filename = null;
        App app;
        app = new App(null);
        filename = app.assignFilename(app.getExperimentsFilename(centreProcedureSet));
        
        logger.info("==FILENAME = {}",filename);
        
        String filename2 = app.assignFilename(app.getExperimentsFilename(centreProcedureSet));
        
        logger.info("==FILENAME = {}",filename2);
        
        String filename3 = app.assignFilename(app.getExperimentsFilename(centreProcedureSet));
        
        logger.info("==FILENAME = {}",filename3);
        
    }
    
    
    @Ignore
    @Test
    public void testAcrossDatabases() {
        String printFile = null;
        try {
            printFile = FileReader.printFile(ACROSS_DATABASES);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(printFile);
        List<Specimen> specimens = null;
        try {
            specimens = hibernateManager.nativeQuery(printFile, Specimen.class);
        } catch (Exception ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(specimens);
        logger.info("retrieved {} specimens",specimens.size());
    }
    
    @AfterClass
    public static void tearDown() {
        if (hibernateManager != null) {
            hibernateManager.close();
        }
    }
}
