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
package org.mousephenotype.dcc.exportlibrary.xmlserialization.utils;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Ignore;

import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Gender;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StageUnit;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Zygosity;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.controls.CoreSerializer;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class PersistenceManagerTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PersistenceManagerTest.class);

    public static CentreSpecimenSet generateCentreSpecimenSetExample() {
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        //
        centreSpecimenSet.getCentre().add(new CentreSpecimen());
        centreSpecimenSet.getCentre().get(0).setCentreID(CentreILARcode.J);
        //
        Mouse mouse = new Mouse();
        mouse.setColonyID("colonyID");
        mouse.setIsBaseline(Boolean.FALSE);
        mouse.setStrainID("strainID");
        mouse.setSpecimenID("specimenID_00_at_Jacks");
        mouse.setGender(Gender.MALE);
        mouse.setZygosity(Zygosity.WILD_TYPE);
        mouse.setLitterId("litterID");
        mouse.setPipeline("IMPC_001");
        mouse.setProductionCentre(CentreILARcode.J);
        mouse.setPhenotypingCentre(CentreILARcode.J);
        mouse.setProject("IMPC");
        mouse.setDOB(DatatypeConverter.parseDate("2012-06-30"));

        //
        mouse.getRelatedSpecimen().add(new RelatedSpecimen());
        mouse.getRelatedSpecimen().get(0).setSpecimenID("specimenID_00_a");
        mouse.getRelatedSpecimen().get(0).setRelationship(Relationship.LITTERMATE);
        //
        mouse.getRelatedSpecimen().add(new RelatedSpecimen());
        mouse.getRelatedSpecimen().get(1).setSpecimenID("specimenID_00_m");
        mouse.getRelatedSpecimen().get(1).setRelationship(Relationship.MOTHER);
        //
        centreSpecimenSet.getCentre().get(0).getMouseOrEmbryo().add(mouse);

        //
        Embryo embryo = new Embryo();

        embryo.setSpecimenID("ColonyName");




        embryo.setStage("34");
        embryo.setStageUnit(StageUnit.DPC);
        embryo.setGender(Gender.FEMALE);
        embryo.setZygosity(Zygosity.WILD_TYPE);
        embryo.setLitterId("litterId");
        embryo.setPipeline("Pipeline");
        embryo.setProductionCentre(CentreILARcode.ICS);
        embryo.setPhenotypingCentre(CentreILARcode.J);
        embryo.setProject("Project");
        embryo.setSpecimenID("embryoID_00");
        embryo.setColonyID("colonyID");


        RelatedSpecimen relatedSpecimen1 = new RelatedSpecimen();
        relatedSpecimen1.setSpecimenID("subjectname1");
        relatedSpecimen1.setRelationship(Relationship.LITTERMATE);

        RelatedSpecimen relatedSpecimen2 = new RelatedSpecimen();
        relatedSpecimen2.setSpecimenID("subjectname2");
        relatedSpecimen2.setRelationship(Relationship.LITTERMATE);

        centreSpecimenSet.getCentre().get(0).getMouseOrEmbryo().add(embryo);

        return centreSpecimenSet;
    }

    @Test
    public void foo() {
        CentreSpecimenSet centreSpecimenSet = PersistenceManagerTest.generateCentreSpecimenSetExample();
        Assert.assertNotNull(centreSpecimenSet);
    }

    /*
     * Needs META-INF/persistence.xml in the class path. So the project needs to
     * be built before executing the test, if not cannot find it in the
     * classpath.
     */
    @Test
    public void testSimpleCase() {
        Properties persistenceProperties = null;
        String derbyPropertiesFilename = "derby.properties";
        String unitname = "org.mousephenotype.dcc.exportlibrary.xmlserialization.utils";
        Reader reader = null;
        try {
            reader = new Reader(derbyPropertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("file {} not found", derbyPropertiesFilename, ex);
            Assert.fail();
        }

        persistenceProperties = reader.getProperties();
        Assert.assertNotNull(persistenceProperties);

        persistenceProperties.setProperty("hibernate.hbm2ddl.auto", "create");
        persistenceProperties.setProperty("hibernate.connection.url", "jdbc:derby:test_databases/simpleCase;create=true");
        persistenceProperties.setProperty("hibernate.ejb.entitymanager_factory_name", "simple_hsqldb_localhost");

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(unitname, persistenceProperties);
        Assert.assertNotNull(entityManagerFactory);

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(new Event("Our very first event!", new Date()));
        entityManager.persist(new Event("A follow up event", new Date()));
        entityManager.getTransaction().commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Event> result = entityManager.createQuery("from Event", Event.class).getResultList();
        for (Event event : result) {
            logger.info("Event ({}) : {}", event.getDate(), event.getTitle());
        }
        entityManager.getTransaction().commit();
        entityManager.close();

    }

    @Ignore
    @Test
    public void testPersistenceManagerMYSQL_XSDs() {
        Properties persistenceProperties = null;
        String mysqldb_basic_properties = "mysql.properties";

         Reader reader = null;
        try {
            reader = new Reader(mysqldb_basic_properties);
        } catch (ConfigurationException ex) {
            logger.error("file {} not found", mysqldb_basic_properties, ex);
            Assert.fail();
        }

        persistenceProperties = reader.getProperties();
        Assert.assertNotNull(persistenceProperties);
        //hibernate.hbm2ddl.auto=create-drop create update validate
        persistenceProperties.setProperty("hibernate.hbm2ddl.auto", "create");
        persistenceProperties.setProperty("hibernate.ejb.entitymanager_factory_name", "xsd_mysql_localhost");
        HibernateManager hibernateManager = new HibernateManager(persistenceProperties, CoreSerializer.PERSISTENCE_UNITNAME);

        Assert.assertNotNull(hibernateManager);

        hibernateManager.persist(PersistenceManagerTest.generateCentreSpecimenSetExample());

        List<CentreSpecimenSet> fromdb = hibernateManager.query("from CentreSpecimenSet", CentreSpecimenSet.class);

        hibernateManager.close();
        

        Assert.assertNotNull(fromdb);

        CentreSpecimenSet centreSpecimenSet = PersistenceManagerTest.generateCentreSpecimenSetExample();

        Assert.assertEquals(centreSpecimenSet, fromdb.get(0));
    }

    @Test
    public void testhibernateManagerDERBY_XSDs() {

        Properties persistenceProperties = null;
        String derby_basic_properties = "derby.properties";

   
        
        
         Reader reader = null;
        try {
            reader = new Reader(derby_basic_properties);
        } catch (ConfigurationException ex) {
            logger.error("file {} not found", derby_basic_properties, ex);
            Assert.fail();
        }

        persistenceProperties = reader.getProperties();
        Assert.assertNotNull(persistenceProperties);

        persistenceProperties.setProperty("hibernate.hbm2ddl.auto", "create");
        
        persistenceProperties.setProperty("hibernate.connection.url", "jdbc:derby:test_databases/exportlibrary;create=true");
        persistenceProperties.setProperty("hibernate.ejb.entitymanager_factory_name", "xsd_derby_exportlibrary_localhost");
        HibernateManager hibernateManager = new HibernateManager(persistenceProperties, CoreSerializer.PERSISTENCE_UNITNAME);

        Assert.assertNotNull(hibernateManager);

        hibernateManager.persist(PersistenceManagerTest.generateCentreSpecimenSetExample());

        List<CentreSpecimenSet> fromdb = hibernateManager.query("from CentreSpecimenSet", CentreSpecimenSet.class);

        hibernateManager.close();

        Assert.assertNotNull(fromdb);

        CentreSpecimenSet centreSpecimenSet = PersistenceManagerTest.generateCentreSpecimenSetExample();

        Assert.assertEquals(centreSpecimenSet, fromdb.get(0));
    }
}
