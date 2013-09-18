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
package org.mousephenotype.dcc.exportlibrary.fullTraverser;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

import java.util.Properties;
import javax.xml.bind.JAXBException;
import junitx.util.PrivateAccessor;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.SubmissionSet;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FullTraverserTest {

    private static final Logger logger = LoggerFactory.getLogger(FullTraverserTest.class);
    //private static final String propertiesFilename = "testdatabase.derby.properties";
    private static final String propertiesFilename = "src/test/resources/dbconf/traverser.mysql.properties";
    private static final String persistenceUnitname = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private static final String contextPath = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    private static final String experimentsFilename = "data/IMPC_EYE_001.xml";
    
    private static HibernateManager hibernateManager;
    private static Reader reader;
    private static FullTraverser fullTraverser;

    static {
        System.setProperty("derby.system.home", System.getProperty("test.databases.folder"));
    }

    private static void newHibernateManager() {
        if (!(new File(System.getProperty("test.databases.folder")).exists())) {
            new File(System.getProperty("test.databases.folder")).mkdir();
        }
        Properties properties;
        logger.info("loading hibernate configuration from {}", propertiesFilename);
        try {
            reader = new Reader(propertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        logger.info("basepath {} ",reader.getPropertiesConfiguration().getBasePath());
        properties = reader.getProperties();
        Assert.assertNotNull(properties);
        hibernateManager = new HibernateManager(properties, persistenceUnitname);

    }

    private static SubmissionSet loadData(long trackerID, Calendar submissionDate) throws JAXBException, IOException {
        logger.info("one {}", System.getProperty("one"));
        logger.info("two {}", System.getProperty("two"));
        logger.info("three {}", System.getProperty("three"));
        logger.info("four {}", System.getProperty("four"));
        logger.info("five {}", System.getProperty("five"));
        logger.info("six {}", System.getProperty("six"));
        
        SubmissionSet submissionset = new SubmissionSet();
        Submission submission = new Submission();
        submissionset.getSubmission().add(submission);
        submission.setTrackerID(trackerID);
        submission.setSubmissionDate(submissionDate);
        
        if(new File(System.getProperty("six") + File.separator+experimentsFilename).exists()){
            logger.info("file can be loaded");
        }else{
            logger.info("cannot");
        }
        
        if(new File(experimentsFilename).exists()){
            logger.info("file can be loaded");
        }else{
            logger.info("cannot");
        }
        
        Path dir = Paths.get(System.getProperty("six")+"/data");
        try(
            DirectoryStream<Path> stream =Files.newDirectoryStream(dir, "*.*")){
            for(Path entry :stream){
                logger.info("{}", entry.getFileName());
            }
        }catch(IOException ex){
            logger.error("damn it");
        }
        logger.info("loading file");
        //submission.setCentreProcedure(XMLUtils.unmarshal(contextPath, CentreProcedureSet.class,System.getProperty("thirdteen") + experimentsFilename).getCentre());
        try{
        submission.setCentreProcedure(XMLUtils.unmarshal(contextPath, CentreProcedureSet.class, experimentsFilename).getCentre());
        }catch(JAXBException | IOException ex){
            logger.error("only name does not work",ex);
        }
        try{
        submission.setCentreProcedure(XMLUtils.unmarshal(contextPath, CentreProcedureSet.class, System.getProperty("six") + File.separator + experimentsFilename).getCentre());
        }catch(JAXBException | IOException ex){
            logger.error("project.build.testOutputDirectory does not work",ex);
        }
                
        
        logger.info("file loaded");
        return submissionset;
    }

    @BeforeClass
    public static void setup() {
        FullTraverserTest.newHibernateManager();
        long TrackerID = 1L;

        Calendar firstNow = DatatypeConverter.now();
        firstNow.roll(Calendar.DAY_OF_YEAR, -2);
        SubmissionSet submissionSet = null;
        try {
            submissionSet = FullTraverserTest.loadData(TrackerID, firstNow);

        } catch (JAXBException | IOException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(submissionSet);
        Assert.assertTrue(submissionSet.isSetSubmission());
        Assert.assertFalse(submissionSet.getSubmission().isEmpty());
        TrackerID++;
        Calendar secondRow = DatatypeConverter.now();
        
        hibernateManager.persist(submissionSet);
        
        FullTraverserTest.hibernateManager.getEntityManager().close();

        
         SubmissionSet sb2 = null;

         try {
         sb2 = FullTraverserTest.loadData(TrackerID, secondRow);

         } catch (JAXBException | IOException ex) {
         logger.error("", ex);
         Assert.fail();
         }

         hibernateManager.persist(sb2);
        
        
        FullTraverserTest.fullTraverser = new FullTraverser(hibernateManager);

    }

    @Test
    public void loadSubmissionTrackerIDsTest() {
        try {
            PrivateAccessor.invoke(FullTraverserTest.fullTraverser, "loadSubmissionTrackerIDs", new Class[]{Calendar.class}, new Object[]{null});
        } catch (Throwable ex) {
            logger.error("", ex);
            Assert.fail();
        }
        List trackerIDs = null;
        try {
            trackerIDs = (List) PrivateAccessor.getField(FullTraverserTest.fullTraverser, "trackerIDs");
        } catch (NoSuchFieldException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(trackerIDs);
        Assert.assertFalse(trackerIDs.isEmpty());
        Assert.assertTrue(trackerIDs.size() == 2);
    }
    
    @Test
    public void run(){
        try{
        fullTraverser.run(null);
        }catch(Exception ex){
            logger.error("", ex);
            Assert.fail();
        }
    }

    @AfterClass
    public static void teardown() {
        hibernateManager.close();
    }
}

