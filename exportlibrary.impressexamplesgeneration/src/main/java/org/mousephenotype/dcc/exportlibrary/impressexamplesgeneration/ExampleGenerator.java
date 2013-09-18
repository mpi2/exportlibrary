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
package org.mousephenotype.dcc.exportlibrary.impressexamplesgeneration;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import javax.xml.bind.JAXBException;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;

import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.*;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ExampleGenerator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExampleGenerator.class);
    private static final Random random = new Random();
    private static String DECIMAL_FORMAT_PATTERN = "#,###,##0.00";
    private final HibernateManager impressHibernateManager;
    private CentreProcedureSet centreProcedureSet;
    public static final String[] projects = new String[]{"MGP Legacy", "EUCOMM-EUMODIC", "UCD-KOMP", "MGP", "MARC", "EUCOMMToolsCre",
        "RIKEN BRC", "DTCC", "BaSH", "Monterotondo", "JAX", "Helmholtz GMC", "DTCC-Legacy", "MRC", "NorCOMM2", "Phenomin"};

    public CentreProcedureSet getCentreProcedureSet() {
        return centreProcedureSet;
    }

    public ExampleGenerator(HibernateManager impressHibernateManager, CentreILARcode centreILARcode, String project, String pipeline, String experimentID, String specimenID) {
        this.impressHibernateManager = impressHibernateManager;
        //


        this.initializeXMLDocumentDataStructure(centreILARcode, project, pipeline, experimentID, specimenID);
    }

    public ExampleGenerator(HibernateManager impressHibernateManager) {
        this.impressHibernateManager = impressHibernateManager;
    }

    public Procedure getProcedure() {
        return this.centreProcedureSet.getCentre().get(0).getExperiment().get(0).getProcedure();
    }

    private void initializeXMLDocumentDataStructure(CentreILARcode centreILARcode, String project, String pipeline, String experimentID, String specimenID) {
        this.centreProcedureSet = new CentreProcedureSet();
        //
        CentreProcedure centreProcedure = new CentreProcedure();
        centreProcedure.setCentreID(centreILARcode);
        centreProcedure.setPipeline(pipeline);
        centreProcedure.setProject(project);

        this.centreProcedureSet.getCentre().add(centreProcedure);
        //
        Experiment experiment = new Experiment();
        centreProcedure.getExperiment().add(experiment);
        experiment.setDateOfExperiment(DatatypeConverter.now());
        experiment.setExperimentID(experimentID);
        experiment.getSpecimenID().add(specimenID);
        //
        Procedure procedure = new Procedure();
        experiment.setProcedure(procedure);
    }

    public static String getRandomFloatValue(float qcmaximum, float qcmiminum) {
        float a = random.nextFloat();
        DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
        return df.format(getRandomFloatValueFloat(qcmaximum, qcmiminum));
    }

    private static float getRandomFloatValueFloat(float qcmaximum, float qcmiminum) {
        float a = random.nextFloat();
        return (a * qcmaximum) + (1 - a) * qcmiminum;
    }

    public static String getRandomIntValue(float qcmaximum, float qcmiminum) {
        int a = random.nextInt(Math.round(qcmaximum));
        return Integer.toString(a > (Math.round(qcmaximum) + Math.round(qcmiminum)) ? Math.round(qcmaximum) : a);

    }
    private static final String[] dimIds = new String[]{"x", "y", "z", "t", "m", "k"};
    private static final String[] incrementValues = new String[]{"one", "two", "three", "four"};

    private void assign(ImpressParameter impressParameter, SeriesMediaParameter seriesMediaParameter) {
        seriesMediaParameter.setParameterID(impressParameter.getParameterKey());
        SeriesMediaParameterValue seriesMediaParameterValue = null;
        ParameterAssociation parameterAssociation = null;
        Dimension dimension = null;
        for (int i = 0; i < 1 + random.nextInt(10); i++) {
            seriesMediaParameterValue = new SeriesMediaParameterValue();
            seriesMediaParameterValue.setFileType("img/jpg");
            seriesMediaParameterValue.setURI("ftp://images/image.jpg");
            seriesMediaParameterValue.setIncrementValue(incrementValues[i % incrementValues.length]);
            for (int j = 0; j < 1 + random.nextInt(3); j++) {
                parameterAssociation = new ParameterAssociation();
                parameterAssociation.setParameterID("paramID" + Integer.toString(i) + Integer.toString(j));
                seriesMediaParameterValue.setIncrementValue(incrementValues[j]);
                seriesMediaParameterValue.getParameterAssociation().add(parameterAssociation);
                for (int k = 0; k < 2 + random.nextInt(2); k++) {
                    dimension = new Dimension();
                    dimension.setId(dimIds[k]);
                    dimension.setOrigin("0.0");
                    dimension.setUnit("px");
                    dimension.setValue(BigDecimal.valueOf(getRandomFloatValueFloat(1.0f, 0.0f)));
                    parameterAssociation.getDim().add(dimension);
                }
            }
            seriesMediaParameter.getValue().add(seriesMediaParameterValue);
        }

    }

    private void assign(ImpressParameter impressParameter, ProcedureMetadata procedureMetadata) {
        procedureMetadata.setParameterID(impressParameter.getParameterKey());
        if (impressParameter.getImpressParameterOption() != null && impressParameter.getImpressParameterOption().size() > 0) {
            procedureMetadata.setValue(impressParameter.getImpressParameterOption().get(random.nextInt(impressParameter.getImpressParameterOption().size())).getName());
            return;
        }
        if (impressParameter.getValueType() == null || impressParameter.getValueType().equals("")) {
            logger.error("impressParameter {} has no value type", impressParameter.getParameterKey());
            return;
        }
        if (impressParameter.getValueType().equals("INT")) {
            if (impressParameter.isSetQcMaximum() && impressParameter.isSetQcMinimum() && impressParameter.getQcMaximum() > 0.0f) {
                String randomValue = getRandomIntValue(impressParameter.getQcMaximum(), impressParameter.getQcMinimum());
                procedureMetadata.setValue(randomValue);
            } else {
                String randomValue = getRandomIntValue(10.0f, 0.0f);
                procedureMetadata.setValue(randomValue);
            }
            return;
        }
        if (impressParameter.getValueType().equals("FLOAT")) {
            if (impressParameter.isSetQcMaximum() && impressParameter.isSetQcMinimum() && impressParameter.getQcMaximum() > 0.0f) {
                String randomValue = getRandomFloatValue(impressParameter.getQcMaximum(), impressParameter.getQcMinimum());
                procedureMetadata.setValue(randomValue);
            } else {
                String randomValue = getRandomFloatValue(1.0f, 0.0f);
                procedureMetadata.setValue(randomValue);
            }
            return;
        }
        if (impressParameter.getValueType().equals("TEXT")) {
            procedureMetadata.setValue("this is random text");
            return;
        }
        if (impressParameter.getValueType().equals("IMAGE")) {
            procedureMetadata.setValue("ftp://images/image.jpg");
            return;
        }
        if (impressParameter.getValueType().equals("DATE")) {
            procedureMetadata.setValue(DatatypeConverter.printDate(DatatypeConverter.now()));
            return;
        }

        if (impressParameter.getValueType().equals("DATETIME")) {
            procedureMetadata.setValue(DatatypeConverter.printDateTime(DatatypeConverter.now()));
            return;
        }
        if (impressParameter.getValueType().equals("TIME")) {
            procedureMetadata.setValue(DatatypeConverter.printTime(DatatypeConverter.now()));
            return;
        }


        if (impressParameter.getValueType().equals("BOOL")) {
            procedureMetadata.setValue(Boolean.toString(false));
        } else {
            procedureMetadata.setValue("this is random text not value type set up");
        }

    }
    /*
     * please rewrite
     */

    private void assign(ImpressParameter impressParameter, OntologyParameter ontologyParameter) {
        ontologyParameter.setParameterID(impressParameter.getParameterKey());
        int index = random.nextInt(impressParameter.getImpressOntologyParameterOption().size());
        StringBuilder value = new StringBuilder(impressParameter.getImpressOntologyParameterOption().get(index).getOntologyId());
        value.append(":");
        value.append(impressParameter.getImpressOntologyParameterOption().get(index).getOntologyTerm());
        ontologyParameter.getTerm().add(value.toString());
    }

    /*
     * please rewrite
     */
    private SeriesParameterValue getParameterValue(ImpressParameter impressParameter) {
        SeriesParameterValue seriesParameterValue = new SeriesParameterValue();
        if (impressParameter.getValueType().equals("INT")) {
            if (impressParameter.isSetQcMaximum() && impressParameter.isSetQcMinimum() && impressParameter.getQcMaximum() > 0.0f) {
                String randomValue = getRandomIntValue(impressParameter.getQcMaximum(), impressParameter.getQcMinimum());
                seriesParameterValue.setValue(randomValue);
            } else {
                String randomValue = getRandomIntValue(10.0f, 0.0f);
                seriesParameterValue.setValue(randomValue);
            }
            return seriesParameterValue;
        }
        if (impressParameter.getValueType().equals("FLOAT")) {
            if (impressParameter.isSetQcMaximum() && impressParameter.isSetQcMinimum() && impressParameter.getQcMaximum() > 0.0f) {
                String randomValue = getRandomFloatValue(impressParameter.getQcMaximum(), impressParameter.getQcMinimum());
                seriesParameterValue.setValue(randomValue);
            } else {
                String randomValue = getRandomFloatValue(1.0f, 0.0f);
                seriesParameterValue.setValue(randomValue);
            }
            return seriesParameterValue;
        }
        if (impressParameter.getValueType().equals("TEXT")) {
            seriesParameterValue.setValue("this is random text");
            return seriesParameterValue;
        }
        if (impressParameter.getValueType().equals("IMAGE")) {
            seriesParameterValue.setValue("ftp://images/image.jpg");
            return seriesParameterValue;
        }
        if (impressParameter.getValueType().equals("DATE")) {
            seriesParameterValue.setValue(DatatypeConverter.printDate(DatatypeConverter.now()));
            return seriesParameterValue;

        }
        if (impressParameter.getValueType().equals("DATETIME")) {
            seriesParameterValue.setValue(DatatypeConverter.printDateTime(DatatypeConverter.now()));
            return seriesParameterValue;
        }

        if (impressParameter.getValueType().equals("TIME")) {
            seriesParameterValue.setValue(DatatypeConverter.printTime(DatatypeConverter.now()));
            return seriesParameterValue;
        }

        if (impressParameter.getValueType().equals("BOOL")) {
            seriesParameterValue.setValue(Boolean.toString(false));
        } else {
            seriesParameterValue.setValue("this is random text not value type set up");
        }
        return seriesParameterValue;
    }

    private void assign(ImpressParameter impressParameter, SeriesParameter seriesParameter) {
        seriesParameter.setParameterID(impressParameter.getParameterKey());

        if (impressParameter.getValueType() == null || impressParameter.getValueType().equals("")) {
            logger.error("impressParameter {} has no value type", impressParameter.getParameterKey());
            return;
        }
        SeriesParameterValue seriesParameterValue = null;
        Calendar now = DatatypeConverter.now();
        if (impressParameter.getImpressParameterIncrement() != null && impressParameter.getImpressParameterIncrement().size() > 0) {

            if (impressParameter.getImpressParameterIncrement().size() == 1 && impressParameter.getImpressParameterIncrement().get(0).isSetMin()) {
                for (int i = 0; i < impressParameter.getImpressParameterIncrement().get(0).getMin().intValue(); i++) {
                    seriesParameterValue = getParameterValue(impressParameter);
                    // impressParameter.getImpressParameterIncrement().get(0).getType() the increment to set here.
                    switch (impressParameter.getImpressParameterIncrement().get(0).getType()) {
                        case "float":
                            seriesParameterValue.setIncrementValue(Float.toString(random.nextFloat()));
                            break;
                        case "repeat":
                            seriesParameterValue.setIncrementValue(Integer.toString(i));
                            break;
                        case "datetime":
                            seriesParameterValue.setIncrementValue(DatatypeConverter.printDateTime(now));
                            break;
                        default:
                            seriesParameterValue.setIncrementValue(Integer.toString(i));
                    }
                    seriesParameter.getValue().add(seriesParameterValue);
                    now.add(Calendar.SECOND, 5);
                }
                return;
            }

            for (ImpressParameterIncrement impressParameterIncrement : impressParameter.getImpressParameterIncrement()) {
                seriesParameterValue = getParameterValue(impressParameter);
                seriesParameter.getValue().add(seriesParameterValue);
                if (impressParameterIncrement.getString() != null) {
                    seriesParameterValue.setIncrementValue(impressParameterIncrement.getString());
                }
            }
        }
        int numberOfValues = random.nextInt(10);
        for (int i = 0; i < numberOfValues; i++) {
            seriesParameterValue = getParameterValue(impressParameter);
            seriesParameter.getValue().add(seriesParameterValue);
            if (impressParameter.isSetImpressParameterIncrement()) {
                switch (impressParameter.getImpressParameterIncrement().get(0).getType()) {
                    case "float":
                        seriesParameterValue.setIncrementValue(Float.toString(random.nextFloat()));
                        break;
                    case "repeat":
                        seriesParameterValue.setIncrementValue(Integer.toString(i));
                        break;
                    case "datetime":
                        seriesParameterValue.setIncrementValue(DatatypeConverter.printDateTime(now));
                        break;
                    default:
                        seriesParameterValue.setIncrementValue(Integer.toString(i));
                }
            }
            now.add(Calendar.SECOND, 5);
            seriesParameterValue.setIncrementValue(new Integer(i).toString());
        }
    }

    /*
     * please rewrite
     */
    private void assign(ImpressParameter impressParameter, MediaParameter mediaParameter) {
        mediaParameter.setParameterID(impressParameter.getParameterKey());
        mediaParameter.setFileType("img/jpg");
        mediaParameter.setURI("ftp://public.download/1.jpg");
    }

    private void assign(ImpressParameter impressParameter, MediaSampleParameter mediaSampleParameter) {
        mediaSampleParameter.setParameterID(impressParameter.getParameterKey());
        MediaSample mediaSample = new MediaSample();
        mediaSampleParameter.getMediaSample().add(mediaSample);
        mediaSample.setLocalId("localID");
        MediaSection mediaSection = new MediaSection();
        mediaSample.getMediaSection().add(mediaSection);
        mediaSection.setLocalId("locallid");
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileType("img/jpg");
        mediaFile.setURI("ftp://public.download/1.jpg");
    }

    private void assign(ImpressParameter impressParameter, SimpleParameter simpleParameter) {
        simpleParameter.setParameterID(impressParameter.getParameterKey());
        if (impressParameter.getUnit() != null) {
            simpleParameter.setUnit(impressParameter.getUnit());
        }
        //
        if (impressParameter.getImpressParameterOption() != null && !impressParameter.getImpressParameterOption().isEmpty()) {
            ImpressParameterOption impressParameterOption = impressParameter.getImpressParameterOption().get(random.nextInt(impressParameter.getImpressParameterOption().size()));
            simpleParameter.setValue(impressParameterOption.getName());
        } else {

            if (impressParameter.getValueType() == null || impressParameter.getValueType().equals("")) {
                logger.error("impressParameter {} has no value type", impressParameter.getParameterKey());
                return;
            }

            if (impressParameter.getValueType().equals("INT")) {
                if (impressParameter.isSetQcMaximum() && impressParameter.isSetQcMinimum() && impressParameter.getQcMaximum() > 0.0f) {
                    simpleParameter.setValue(getRandomIntValue(impressParameter.getQcMaximum(), impressParameter.getQcMinimum()));
                } else {
                    simpleParameter.setValue(getRandomIntValue(10, 0));
                }
                return;
            }
            if (impressParameter.getValueType().equals("FLOAT")) {
                if (impressParameter.isSetQcMaximum() && impressParameter.isSetQcMinimum()
                        && impressParameter.getQcMaximum() > 0.0f) {
                    simpleParameter.setValue(getRandomFloatValue(impressParameter.getQcMaximum(), impressParameter.getQcMinimum()));
                } else {
                    simpleParameter.setValue(getRandomFloatValue(1.0f, 0.0f));
                }
                return;
            }
            if (impressParameter.getValueType().equals("TEXT")) {
                simpleParameter.setValue("TEXT");
                return;
            }
            if (impressParameter.getValueType().equals("IMAGE")) {
                simpleParameter.setValue("ftp://images/image.jpg");
                return;
            }
            if (impressParameter.getValueType().equals("DATETIME")) {
                simpleParameter.setValue(DatatypeConverter.printDateTime(DatatypeConverter.now()));
                return;
            }
            if (impressParameter.getValueType().equals("DATE")) {
                simpleParameter.setValue(DatatypeConverter.printDate(DatatypeConverter.now()));
                return;
            }
            if (impressParameter.getValueType().equals("TIME")) {
                simpleParameter.setValue(DatatypeConverter.printTime(DatatypeConverter.now()));
                return;
            }


            if (impressParameter.getValueType().equals("BOOL")) {
                simpleParameter.setValue(Boolean.toString(false));
            } else {
                simpleParameter.setValue("a value");
            }
        }
    }

    public void generateProcedure(ImpressProcedure impressProcedure) {

        //
        this.getProcedure().setProcedureID(impressProcedure.getProcedureKey());
        //
        for (ImpressParameter impressParameter : impressProcedure.getImpressParameter()) {
            if (!impressParameter.isIsDerived()) {
                switch (impressParameter.getType()) {
                    case SIMPLE_PARAMETER:
                        SimpleParameter simpleParameter = new SimpleParameter();
                        this.assign(impressParameter, simpleParameter);
                        this.getProcedure().getSimpleParameter().add(simpleParameter);
                        break;
                    case ONTOLOGY_PARAMETER:
                        OntologyParameter ontologyParameter = new OntologyParameter();
                        this.assign(impressParameter, ontologyParameter);
                        this.getProcedure().getOntologyParameter().add(ontologyParameter);
                        break;
                    case SERIES_PARAMETER:
                        SeriesParameter seriesParameter = new SeriesParameter();
                        this.assign(impressParameter, seriesParameter);
                        this.getProcedure().getSeriesParameter().add(seriesParameter);
                        break;
                    case MEDIA_PARAMETER:
                        MediaParameter mediaParameter = new MediaParameter();
                        this.assign(impressParameter, mediaParameter);
                        this.getProcedure().getMediaParameter().add(mediaParameter);
                        break;
                    case MEDIA_SAMPLE_PARAMETER:
                        MediaSampleParameter mediaSampleParameter = new MediaSampleParameter();
                        this.assign(impressParameter, mediaSampleParameter);
                        this.getProcedure().getMediaSampleParameter().add(mediaSampleParameter);
                        break;
                    case SERIES_MEDIA_PARAMETER:
                        SeriesMediaParameter seriesMediaParameter = new SeriesMediaParameter();
                        this.assign(impressParameter, seriesMediaParameter);
                        this.getProcedure().getSeriesMediaParameter().add(seriesMediaParameter);
                        break;
                    case PROCEDURE_METADATA:
                        ProcedureMetadata procedureMetadata = new ProcedureMetadata();
                        this.assign(impressParameter, procedureMetadata);
                        this.getProcedure().getProcedureMetadata().add(procedureMetadata);
                        break;
                }
            }
        }
    }

    /*
     * consider that the xmldocument data has been first been initialized on the
     * constructor
     */
    public void generateExamples(CentreILARcode centreILARcode, String experimentID, String specimenID) throws JAXBException {
        if (!(new File("data/tests/").exists())) {
            new File("data/tests/").mkdirs();
        }
        //
        List<ImpressPipelineContainer> containers = this.impressHibernateManager.query("from ImpressPipelineContainer", ImpressPipelineContainer.class);
        //
        if (containers != null && !containers.isEmpty()) {
            ImpressPipelineContainer impressPipelineContainer = containers.get(0);
            String project = ExampleGenerator.projects[random.nextInt(ExampleGenerator.projects.length)];
            for (ImpressPipeline impressPipeline : impressPipelineContainer.getPipeline()) {
                for (ImpressProcedure procedure : impressPipeline.getImpressProcedure()) {
                    this.generateProcedure(procedure);
                    this.serialize("data/tests/" + procedure.getProcedureKey() + ".xml");
                    this.initializeXMLDocumentDataStructure(centreILARcode, project, impressPipeline.getPipelineKey(), experimentID, specimenID);
                }
            }
        }
    }

    public CentreILARcode getCentreILARcode(String pipelineKey) {
        switch (pipelineKey) {
            case "IMPC_001":
                return CentreILARcode.H;
            case "HRWL_001":
                return CentreILARcode.H;
            case "TCP_001":
                return CentreILARcode.TCP;
            case "ICS_001":
                return CentreILARcode.ICS;
            case "JAX_001":
                return CentreILARcode.J;
            case "UCD_001":
                return CentreILARcode.UCD;
            default:
                return CentreILARcode.H;
        }
    }

    public String getProject(String pipelineKey) {
        switch (pipelineKey) {
            case "IMPC_001":
                return "BaSH";
            case "HRWL_001":
                return "MRC";
            case "TCP_001":
                return "NorCOMM2";
            case "ICS_001":
                return "Phenomin";
            case "JAX_001":
                return "JAX";
            case "UCD_001":
                return "DTCC";
            default:
                return "";
        }
    }

    public void generateExamples() throws JAXBException {
        if (!(new File("data/tests/").exists())) {
            new File("data/tests/").mkdirs();
        }
        //
        List<ImpressPipelineContainer> containers = this.impressHibernateManager.query("from ImpressPipelineContainer", ImpressPipelineContainer.class);
        //
        if (containers != null && !containers.isEmpty()) {
            ImpressPipelineContainer impressPipelineContainer = containers.get(0);
            for (ImpressPipeline impressPipeline : impressPipelineContainer.getPipeline()) {
                logger.info("generating pipeline {}", impressPipeline.getPipelineKey());
                for (ImpressProcedure procedure : impressPipeline.getImpressProcedure()) {
                    if (!(new File("data/tests/" + procedure.getProcedureKey() + ".xml").exists())) {
                        logger.info("generating procedure {}", procedure.getProcedureKey());
                        this.initializeXMLDocumentDataStructure(this.getCentreILARcode(impressPipeline.getPipelineKey()),
                                this.getProject(impressPipeline.getPipelineKey()),
                                impressPipeline.getPipelineKey(),
                                "experiment_" + DatatypeConverter.printDate(DatatypeConverter.now()),
                                "mouse_" + random.nextInt(1000));
                        this.generateProcedure(procedure);
                        this.serialize("data/tests/" + procedure.getProcedureKey() + ".xml");
                    }
                }
            }
        }

    }

    public void serialize(String filename) throws JAXBException {
        if (this.centreProcedureSet != null && this.centreProcedureSet.getCentre() != null && this.centreProcedureSet.getCentre().size() > 0) {
            XMLUtils.marshall("org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure", "http://www.mousephenotype.org/dcc/exportlibrary/datastructure/core/procedure http://www.mousephenotype.org/dcc/exportlibrary/datastructure/core/procedure/procedure_definition.xsd", this.centreProcedureSet, filename);
        } else {
            logger.error("empty procedure");
        }
    }
}
