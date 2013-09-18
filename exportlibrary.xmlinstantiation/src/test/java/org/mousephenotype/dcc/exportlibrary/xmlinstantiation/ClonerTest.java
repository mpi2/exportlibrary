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
package org.mousephenotype.dcc.exportlibrary.xmlinstantiation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Assert;

import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Gender;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StageUnit;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Zygosity;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.utils.xml.XMLUtils;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ClonerTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ClonerTest.class);
    private static final String SPECIMEN_FILENAME = "data/marshalledCentreSpecimenSet.xml";
    private static final String PROCEDURE_FILENAME = "data/marshalledCentreProcedureSet.xml";
    private static final String SPECIMEN_EXISTING_FILENAME = "data/specimens.xml";
    private static final String PROCEDURE_EXISTING_FILENAME = "data/procedures.xml";

    public static CentreSpecimenSet generateCentreSpecimenSetExample() {
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        //
        centreSpecimenSet.getCentre().add(new CentreSpecimen());
        centreSpecimenSet.getCentre().get(0).setCentreID(CentreILARcode.J);
        //
        Mouse mouse = new Mouse();
        mouse.setColonyID("colonyID");
        mouse.setIsBaseline(false);
        mouse.setStrainID("strainID");
        mouse.setSpecimenID("specimenID_00_at_Jax");
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
        
        
        Embryo embryo = new Embryo();
        
        embryo.setSpecimenID("ColonyName");

        Calendar xmlDate = DatatypeConverter.parseDate("2012-06-30");


        embryo.setStage("34");
        embryo.setIsBaseline(false);
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

    public static CentreProcedureSet generateCentreProcedureSetExample() {
        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        //
        centreProcedureSet.getCentre().add(new CentreProcedure());
        
        centreProcedureSet.getCentre().get(0).setCentreID(CentreILARcode.J);
        
        centreProcedureSet.getCentre().get(0).setPipeline("IMPC_001");
        
        centreProcedureSet.getCentre().get(0).setProject("IMPC");
        //

        centreProcedureSet.getCentre().get(0).getHousing().add(new Housing());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).setProcedure(new Procedure());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().setProcedureID("IMPC_HOU_001");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSimpleParameter().add(new SimpleParameter());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSimpleParameter().get(0).setParameterID("IMPC_HOU_001_001");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSimpleParameter().get(0).setValue("yes");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSimpleParameter().add(new SimpleParameter());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSimpleParameter().get(1).setParameterID("IMPC_HOU_001_002");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSimpleParameter().get(1).setValue("21");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSimpleParameter().add(new SimpleParameter());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSimpleParameter().get(2).setParameterID("IMPC_HOU_001_003");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSimpleParameter().get(2).setValue("23");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().add(new SeriesParameter());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(0).setParameterID("ESLIM_HOU_001_004");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(0).getValue().add(new SeriesParameterValue());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(0).getValue().get(0).setIncrementValue("1");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(0).getValue().get(0).setValue("Wood chips");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(0).getValue().add(new SeriesParameterValue());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(0).getValue().get(1).setIncrementValue("2");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(0).getValue().get(1).setValue("Fun Tunnels");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().add(new SeriesParameter());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(1).setParameterID("ESLIM_HOU_001_005");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(1).getValue().add(new SeriesParameterValue());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(1).getValue().get(0).setIncrementValue("1");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(1).getValue().get(0).setValue("Malburg");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(1).getValue().add(new SeriesParameterValue());
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(1).getValue().get(1).setIncrementValue("2");
        centreProcedureSet.getCentre().get(0).getHousing().get(0).getProcedure().getSeriesParameter().get(1).getValue().get(1).setValue("Ebola");
        
        //
        centreProcedureSet.getCentre().get(0).getExperiment().add(new Experiment());
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).setExperimentID("localExperimentID");

        centreProcedureSet.getCentre().get(0).getExperiment().get(0).setDateOfExperiment(DatatypeConverter.parseDate("2012-06-30"));

        //
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getSpecimenID().add("specimenID_00_at_Jax");
        //tail elevation
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).setProcedure(new Procedure());
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().setProcedureID("IMPC_002_001");
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getSimpleParameter().add(new SimpleParameter());
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getSimpleParameter().get(0).setParameterID("IMPC_002_001_006_001");
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getSimpleParameter().get(0).setValue("Straub tail");
        //metadata: location of test
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().add(new ProcedureMetadata());
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().get(0).setParameterID("IMPC_002_001_057_001");
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().get(0).setValue("bench");
        //metadata: number of animals
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().add(new ProcedureMetadata());
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().get(1).setParameterID("IMPC_002_001_058_001");
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().get(1).setValue("1");
        //metadata: Number of days since cage changed 
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().add(new ProcedureMetadata());
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().get(2).setParameterID("IMPC_002_001_059_001");
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().get(2).setValue("3.2");
        //metadata: Experimenter ID
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().add(new ProcedureMetadata());
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().get(3).setParameterID("IMPC_002_001_060_001");
        centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure().getProcedureMetadata().get(3).setValue("John Doe");
        return centreProcedureSet;
    }

    
    @Test
    public void marshallSpecimen() {
        CentreSpecimenSet centreSpecimenSet= ClonerTest.generateCentreSpecimenSetExample();
        Assert.assertNotNull(centreSpecimenSet);
        try {
            //Cloner.marshallCentreSpecimenSet(centreSpecimenSet, SPECIMEN_FILENAME);
            String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen";
            XMLUtils.marshall(CONTEXT_PATH,"http://www.mousephenotype.org/dcc/exportlibrary/datastructure/core/specimen http://www.mousephenotype.org/dcc/exportlibrary/datastructure/core/specimen/specimen_definition.xsd" ,centreSpecimenSet, SPECIMEN_FILENAME);
            //DatatypeConverter.marshall(CONTEXT_PATH,centreSpecimenSet, SPECIMEN_FILENAME);
        } catch (JAXBException ex) {
            logger.error("jaxb exception",ex);
            Assert.fail();
        }
    }

    @Test
    public void marshallProcedure() {
        try {
            Cloner.marshallCentreProcedureSet(ClonerTest.generateCentreProcedureSetExample(), PROCEDURE_FILENAME);
        } catch (JAXBException ex) {
            logger.error("",ex);
            Assert.fail();
        }
    }

    
    @Test
    public void unmarshallSpecimen() {
        CentreSpecimenSet unmarshallSpecimenXMLfile = null;
        try {
            unmarshallSpecimenXMLfile = Cloner.unmarshallSpecimenXMLfile(SPECIMEN_EXISTING_FILENAME);
        } catch (JAXBException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("",ex);
            Assert.fail();
        }catch(IOException ex){
                logger.error("",ex);
            Assert.fail();
        }

        Assert.assertNotNull(unmarshallSpecimenXMLfile);
    }

    
    @Test
    public void cloneSpecimen() {
        CentreSpecimenSet unmarshallSpecimenXMLfile = null;
        try {
            unmarshallSpecimenXMLfile = Cloner.unmarshallSpecimenXMLfile(SPECIMEN_EXISTING_FILENAME);
        } catch (JAXBException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("",ex);
            Assert.fail();
        }catch(IOException ex){
                logger.error("",ex);
            Assert.fail();
        }

        Assert.assertNotNull(unmarshallSpecimenXMLfile);

        CentreSpecimenSet unmarshallSpecimenXMLfile_clone = null;
        try {
            unmarshallSpecimenXMLfile_clone = Cloner.cloneSpecimen(unmarshallSpecimenXMLfile);
        } catch (NoSuchMethodException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalAccessException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InvocationTargetException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InstantiationException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (DatatypeConfigurationException ex) {
            logger.error("",ex);
            Assert.fail();
        }
        Assert.assertNotNull(unmarshallSpecimenXMLfile_clone);

        Assert.assertEquals(unmarshallSpecimenXMLfile, unmarshallSpecimenXMLfile_clone);

        unmarshallSpecimenXMLfile.getCentre().get(0).setCentreID(CentreILARcode.TCP);

        Assert.assertFalse(unmarshallSpecimenXMLfile.equals(unmarshallSpecimenXMLfile_clone));




    }

    
    @Test
    public void cloneProcedure() {
        CentreProcedureSet unmarshallProcedureXMLfile = null;
        try {
            unmarshallProcedureXMLfile = Cloner.unmarshallProcedureXMLfile(PROCEDURE_EXISTING_FILENAME);
        } catch (JAXBException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("",ex);
            Assert.fail();
        }catch(IOException ex){
                logger.error("",ex);
            Assert.fail();
        }
        Assert.assertNotNull(unmarshallProcedureXMLfile);

        CentreProcedureSet unmarshallProcedureXMLfile_clone = null;
        try {
            unmarshallProcedureXMLfile_clone = Cloner.cloneProcedure(unmarshallProcedureXMLfile);
        } catch (NoSuchMethodException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalAccessException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InvocationTargetException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InstantiationException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (DatatypeConfigurationException ex) {
            logger.error("",ex);
            Assert.fail();
        }
        Assert.assertNotNull(unmarshallProcedureXMLfile_clone);
        try {
            Cloner.marshallCentreProcedureSet(unmarshallProcedureXMLfile_clone, "data/cloned.xml");
        } catch (JAXBException ex) {
            logger.error("",ex);
            Assert.fail();
        }

        Assert.assertEquals(unmarshallProcedureXMLfile, unmarshallProcedureXMLfile_clone);

        Assert.assertTrue(unmarshallProcedureXMLfile.equals(unmarshallProcedureXMLfile_clone));

        unmarshallProcedureXMLfile.getCentre().get(0).setPipeline(unmarshallProcedureXMLfile.getCentre().get(0).getPipeline().concat("_new"));

        Assert.assertFalse(unmarshallProcedureXMLfile.equals(unmarshallProcedureXMLfile_clone));

    }
    
    @Test
    public void parameterTests(){
        SimpleParameter simpleParameter = new SimpleParameter();
        simpleParameter.setParameterID("");
        simpleParameter.setUnit("");
        simpleParameter.setValue("");
        //
        OntologyParameter ontologyParameter = new OntologyParameter();
        ontologyParameter.setParameterID("");
        List<String> terms = new ArrayList<String>(2);
        terms.add("");
        terms.add("");
        ontologyParameter.setTerm(terms);
        //
        SeriesParameter seriesParameter = new SeriesParameter();
        seriesParameter.setParameterID("");
        seriesParameter.setUnit("");
        
        SeriesParameterValue seriesParameterValue = new SeriesParameterValue();
        seriesParameterValue.setIncrementStatus("");
        seriesParameterValue.setIncrementValue("");
        seriesParameterValue.setValue("");
        seriesParameter.getValue().add(seriesParameterValue);
        //
        MediaParameter mediaParameter = new MediaParameter();
        mediaParameter.setParameterID("");
        mediaParameter.setFileType("");
        mediaParameter.setURI("");
        
        ParameterAssociation parameterAssociation = new ParameterAssociation();
        parameterAssociation.setParameterID("");
        Dimension dimension = new Dimension();
        parameterAssociation.getDim().add(dimension);
        dimension.setId("");
        dimension.setOrigin("");
        dimension.setUnit("");
        dimension.setValue(BigDecimal.ZERO);
        
        mediaParameter.getParameterAssociation().add(parameterAssociation);
        ProcedureMetadata procedureMetadata = new ProcedureMetadata();
        procedureMetadata.setParameterID("");
        procedureMetadata.setParameterStatus("");
        procedureMetadata.setValue("");
        
        mediaParameter.getProcedureMetadata().add(procedureMetadata);
        //
        MediaSampleParameter mediaSampleParameter = new MediaSampleParameter();
        mediaSampleParameter.setParameterID("");
        mediaSampleParameter.setParameterStatus("");
        
        MediaSample mediaSample = new MediaSample();
        mediaSampleParameter.getMediaSample().add(mediaSample);
        mediaSample.setLocalId("");
        
        MediaSection mediaSection = new MediaSection();
        mediaSample.getMediaSection().add(mediaSection);
        mediaSection.setLocalId("");
        MediaFile mediaFile = new MediaFile();
        
        mediaSection.getMediaFile().add(mediaFile);
        mediaFile.setFileType("");
        mediaFile.setLocalId("");
        
        //missing parameter Association and ProcedureMedatadta
        //
        SeriesMediaParameter seriesMediaParameter = new SeriesMediaParameter();
        seriesMediaParameter.setParameterID("");
        seriesMediaParameter.setParameterStatus("");
        SeriesMediaParameterValue seriesMediaParameterValue = new SeriesMediaParameterValue();
        seriesMediaParameterValue.setFileType("");
        seriesMediaParameterValue.getParameterAssociation().add(parameterAssociation);
        seriesMediaParameterValue.getProcedureMetadata().add(procedureMetadata);
        //
        
        
    
    }
 
}
