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
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.VALIDATIONRESOURCES_IDS;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress.Caster;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress.ImpressBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.statuscodes.StatusCodesBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.support.CentreProcedureSetValidatorSupport;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.support.RepeatedParameterInfo;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.support.SpecimenWSclient;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.utils.ValidationException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressOntologyParameterOption;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameter;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterIncrement;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterOption;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType;
import static org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType.MEDIA_PARAMETER;
import static org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType.MEDIA_SAMPLE_PARAMETER;
import static org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType.ONTOLOGY_PARAMETER;
import static org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType.PROCEDURE_METADATA;
import static org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType.SERIES_MEDIA_PARAMETER;
import static org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType.SERIES_PARAMETER;
import static org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType.SIMPLE_PARAMETER;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressProcedure;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class CentreProcedureSetValidator extends Validator<CentreProcedureSet> {
    //

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(CentreProcedureSetValidator.class);
    //
    private static final String PROJECTS_FILENAME = "projects.txt";
    //
    private final SpecimenWSclient specimenWSclient;
    private static List<String> projects = null;
    private boolean[] validPipelines;

    static {
        try {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(PROJECTS_FILENAME);
            CentreProcedureSetValidator.projects = Arrays.asList(propertiesConfiguration.getStringArray("projects"));
        } catch (ConfigurationException ex) {
            logger.error("{} not found", PROJECTS_FILENAME, ex);
        }

        if (CentreProcedureSetValidator.projects == null || CentreProcedureSetValidator.projects.size() < 1) {
            logger.error("cannot load projects file");
        }

    }

    public CentreProcedureSetValidator() {
        super(null, null, null, null);
        this.specimenWSclient = null;
    }

    //Map<IOParameters.IOS_PROPERTIES, String> values = new EnumMap<IOParameters.IOS_PROPERTIES, String>(IOParameters.IOS_PROPERTIES.class);
    public CentreProcedureSetValidator(CentreProcedureSet centreset, ValidationSet validationSet,
            ValidationReportSet validationReportSet, Map<VALIDATIONRESOURCES_IDS, Incarnator<?>> xmlValidationResources,
            SpecimenWSclient specimenWSclient) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException, LockTimeoutException, PersistenceException, JAXBException, Exception {
        super(centreset, validationSet, validationReportSet, xmlValidationResources);
        this.specimenWSclient = specimenWSclient;
        this.setupXMLresources();


    }

    private void setupXMLresources() throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException, LockTimeoutException, PersistenceException, JAXBException, FileNotFoundException, Exception {
        validPipelines = this.startValidationStructure();
    }

    @Override
    public void validateWithHandler() {
        int i = 0;

        for (CentreProcedure centre : this.centreset.getCentre()) {
            if (validPipelines[i++]) {
                this.checkCentreProcedureAttributes(centre);
                this.checkCentreHousing(centre);
                this.checkCentreLine(centre);
                this.checkCentreExperiment(centre);
            }
        }
    }

    private boolean[] startValidationStructure() throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException, LockTimeoutException, PersistenceException, JAXBException, FileNotFoundException, Exception {
        validPipelines = new boolean[this.centreset.getCentre().size()];
        int i = 0;
        for (CentreProcedure centre : this.centreset.getCentre()) {
            validPipelines[i] =
                    ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).loadPipeline(centre.getPipeline());
            if (!validPipelines[i++]) {
                this.errorHandler.fatalError(new ValidationException("Pipeline " + centre.getPipeline() + " is not valid for centreProcedure " + centre.getCentreID(), "CentreProcedureSetValidator_invalidPipelineID", centre));
            }
        }
        return validPipelines;
    }

    private void checkCentreHousing(CentreProcedure centre) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        logger.info("reached checkCentreHousing");
    }

    private void checkLineAttributes(Line line, CentreProcedure centre) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        logger.info("reached checkLineAttributes");
    }

    private void checkCentreLine(CentreProcedure centre) {

        if (centre.isSetLine()) {
            for (Line line : centre.getLine()) {
                this.checkLineAttributes(line, centre);
                this.checkColonySubmittedToDCC(line, centre);
                if (line.isSetStatusCode()) {
                    for (StatusCode statusCode : line.getStatusCode()) {
                        if (statusCode.getValue() != null) {
                            if (!validateParameterStatus(statusCode.getValue())) {
                                this.errorHandler.error(new ValidationException("statusCode " + statusCode.getValue() + " is not registered in the database", "CentreProcedureSetValidator_lineStatusInvalid", line));
                            }
                        } else {
                            this.errorHandler.error(new ValidationException("line [" + line.getColonyID() + "] with null statusCode  ", "CentreProcedureSetValidator_line_nullStatusCode", line));
                        }

                    }
                } else {
                    if (line.isSetProcedure()) {
                        this.checkProcedure(centre, line.getProcedure());
                    } else {
                        this.errorHandler.error(new ValidationException("line [" + line.getColonyID() + "] with no status codes, no procedures", "CentreProcedureSetValidator_linenullStatusCodenullProcedures", line));
                    }
                }
            }
        } else {
            if (!centre.isSetExperiment() && !centre.isSetHousing()) {
                this.errorHandler.error(new ValidationException("centre [" + centre.getCentreID() + "] with no line procedures", "CentreProcedureSetValidator_noExperiments", centre));
            }
        }

    }

    private void checkCentreExperiment(CentreProcedure centre) {
        if (centre.isSetExperiment()) {
            //
            for (Experiment experiment : centre.getExperiment()) {
                //
                this.checkExperimentAttributes(experiment, centre);
                //
                this.checkSpecimensSubmittedToDCC(experiment, centre.getCentreID());
                //
                if (experiment.isSetStatusCode()) {
                    StringBuilder sb = new StringBuilder();
                    for (StatusCode statusCode : experiment.getStatusCode()) {
                        if (statusCode.getValue() != null) {
                            sb.append(statusCode.getValue());
                            sb.append(" ");

                            if (!validateParameterStatus(statusCode.getValue())) {
                                this.errorHandler.error(new ValidationException("statusCode " + statusCode.getValue() + " is not registered in the database", "CentreProcedureSetValidator_experimentStatusInvalid", experiment));
                            }
                        } else {
                            this.errorHandler.error(new ValidationException("experiment [" + experiment.getExperimentID() + "] with null statusCode  ", "CentreProcedureSetValidator_nullStatusCode", experiment));
                        }
                    }
                    logger.info("experiment {} has status codes {}", experiment.getExperimentID(), sb.toString());
                } else {
                    if (experiment.isSetProcedure()) {
                        this.checkProcedure(centre, experiment.getProcedure());
                    } else {
                        this.errorHandler.error(new ValidationException("experiment [" + experiment.getExperimentID() + "] with no status codes, no procedures", "CentreProcedureSetValidator_nullStatusCodenullProcedures", experiment));
                    }
                }
            }
        } else {
            if (!centre.isSetLine() && !centre.isSetHousing()) {
                this.errorHandler.error(new ValidationException("centre [" + centre.getCentreID() + "] with no experiments", "CentreProcedureSetValidator_noExperiments", centre));
            }
        }
    }

    private ImpressBrowser impressBrowser() {
        return (ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser);
    }

    private void checkProcedure(CentreProcedure centre, Procedure procedure) {

        ImpressProcedure impressProcedure = this.impressBrowser().getImpressProcedure(centre.getPipeline(), procedure.getProcedureID());
        //
        Set<String> requiredParameterIDs = this.impressBrowser().getRequiredParameterIDs(centre.getPipeline(), procedure.getProcedureID());
        Set<String> allParameterIDs = this.impressBrowser().getAllParameterIDs(centre.getPipeline(), procedure.getProcedureID());
        //
        Set<String> currentParameters = null;
        //
        if (impressProcedure == null) {
            this.errorHandler.error(new ValidationException("ProcedureID " + procedure.getProcedureID() + " is not part of the pipeline " + centre.getPipeline(), "CentreProcedureSetValidator_checkProcedure", procedure));
            return;
        }
        currentParameters = this.checkProcedureParameters(impressProcedure, procedure);

        if (!currentParameters.containsAll(requiredParameterIDs)) {
            this.errorHandler.error(
                    new ValidationException("The required parameters "
                    + CentreProcedureSetValidatorSupport.getListOfRequiredNotPresentParameters(requiredParameterIDs, currentParameters) + " for " + procedure.getProcedureID() + " are missing", "CentreProcedureSetValidator_requiredNonPresentParameters", procedure));
        }
        if (Sets.difference(currentParameters, allParameterIDs).size() > 0) {
            this.errorHandler.error(new ValidationException("The parameters " + CentreProcedureSetValidatorSupport.getListOfParametersNotBelongingToThisProcedure(Sets.difference(currentParameters, allParameterIDs)) + " are not part of " + procedure.getProcedureID(), "CentreProcedureSetValidator_parametersNoBelongingToTheProcedure", procedure));
        }
        Table<String, BigInteger, List<RepeatedParameterInfo>> parameterIds = this.composeTable(procedure, impressProcedure);
        this.checkParametersWrongType(parameterIds);
        this.checkRepeatedParameters(parameterIds);
    }

    private void checkSpecimensSubmittedToDCC(Experiment experiment, CentreILARcode centre) {
        boolean exists = false;
        for (String specimenID : experiment.getSpecimenID()) {
            exists = false;
            if (this.specimenWSclient != null) {
                try {
                    exists = this.specimenWSclient.exists(centre, specimenID);
                } catch (UniformInterfaceException | ClientHandlerException ex) {

                    this.errorHandler.error(new ValidationException("cannot connect to specimen ws client.", "CentreSpecimenSetValidator_testSpecimenSubmittedToDCC", experiment));
                    logger.error("cannot connect to the web service", ex);
                    return;
                }
            }
            if (!exists) {
                this.errorHandler.error(new ValidationException("specimen " + specimenID + " has not been submitted to the DCC", "CentreSpecimenSetValidator_testSpecimenSubmittedToDCC", experiment));
            }
        }

        if (this.specimenWSclient == null) {
            this.errorHandler.error(new ValidationException("cannot initialize specimen ws client. No properties provided", "CentreSpecimenSetValidator_testSpecimenSubmittedToDCC", experiment));
        }

    }

    private ImpressParameterType getImpressParameterType(ImpressProcedure impressProcedure, String parameterID) {
        for (ImpressParameter impressParameter : impressProcedure.getImpressParameter()) {
            if (impressParameter.getParameterKey().equals(parameterID)) {
                return impressParameter.getType();
            }
        }
        logger.error("cannot find parameterType for procedure {}  parameter {}", impressProcedure.getProcedureKey(), parameterID);
        return null;
    }

    private Table<String, BigInteger, List<RepeatedParameterInfo>> composeTable(Procedure procedure, ImpressProcedure impressProcedure) {
        Table<String, BigInteger, List<RepeatedParameterInfo>> parameterIds = HashBasedTable.create();
        RepeatedParameterInfo info = null;
        for (SimpleParameter parameter : procedure.getSimpleParameter()) {
            info = new RepeatedParameterInfo(parameter, parameter.getParameterID(), parameter.getSequenceID(), ImpressParameterType.SIMPLE_PARAMETER, getImpressParameterType(impressProcedure, parameter.getParameterID()));
            RepeatedParameterInfo.put(parameterIds, info);

        }
        for (OntologyParameter parameter : procedure.getOntologyParameter()) {
            info = new RepeatedParameterInfo(parameter, parameter.getParameterID(), parameter.getSequenceID(), ImpressParameterType.ONTOLOGY_PARAMETER, getImpressParameterType(impressProcedure, parameter.getParameterID()));
            RepeatedParameterInfo.put(parameterIds, info);
        }
        for (SeriesParameter parameter : procedure.getSeriesParameter()) {
            info = new RepeatedParameterInfo(parameter, parameter.getParameterID(), parameter.getSequenceID(), ImpressParameterType.SERIES_PARAMETER, getImpressParameterType(impressProcedure, parameter.getParameterID()));
            RepeatedParameterInfo.put(parameterIds, info);
        }
        for (MediaParameter parameter : procedure.getMediaParameter()) {
            info = new RepeatedParameterInfo(parameter, parameter.getParameterID(), null, ImpressParameterType.MEDIA_PARAMETER, getImpressParameterType(impressProcedure, parameter.getParameterID()));
            RepeatedParameterInfo.put(parameterIds, info);
        }
        for (MediaSampleParameter parameter : procedure.getMediaSampleParameter()) {
            info = new RepeatedParameterInfo(parameter, parameter.getParameterID(), null, ImpressParameterType.MEDIA_SAMPLE_PARAMETER, getImpressParameterType(impressProcedure, parameter.getParameterID()));
            RepeatedParameterInfo.put(parameterIds, info);
        }
        for (SeriesMediaParameter parameter : procedure.getSeriesMediaParameter()) {
            info = new RepeatedParameterInfo(parameter, parameter.getParameterID(), null, ImpressParameterType.SERIES_MEDIA_PARAMETER, getImpressParameterType(impressProcedure, parameter.getParameterID()));
            RepeatedParameterInfo.put(parameterIds, info);
        }
        for (ProcedureMetadata parameter : procedure.getProcedureMetadata()) {
            info = new RepeatedParameterInfo(parameter, parameter.getParameterID(), parameter.getSequenceID(), ImpressParameterType.PROCEDURE_METADATA, getImpressParameterType(impressProcedure, parameter.getParameterID()));
            RepeatedParameterInfo.put(parameterIds, info);
        }
        return parameterIds;
    }

    private void checkParametersWrongType(Table<String, BigInteger, List<RepeatedParameterInfo>> parameterIds) {
        String message = null;
        for (List<RepeatedParameterInfo> list : parameterIds.values()) {
            for (RepeatedParameterInfo parameterInfo : list) {

                if ((parameterInfo.getParameterType() != null
                        && parameterInfo.getDefinedParameterType() != null
                        && !parameterInfo.getDefinedParameterType().equals(parameterInfo.getParameterType()))) {

                    message = "[" + parameterInfo.getParameterKey() + "] is defined as " + parameterInfo.getDefinedParameterType().name() + " but has been submitted as " + parameterInfo.getParameterType();

                    switch (parameterInfo.getParameterType()) {
                        case SIMPLE_PARAMETER:

                            this.errorHandler.error(new ValidationException(message, "CentreProcedureSetValidator_checkParametersWrongType", parameterInfo.getSimpleParameter()));

                            break;
                        case ONTOLOGY_PARAMETER:

                            this.errorHandler.error(new ValidationException(message, "CentreProcedureSetValidator_checkParametersWrongType", parameterInfo.getOntologyParameter()));

                            break;
                        case SERIES_PARAMETER:

                            this.errorHandler.error(new ValidationException(message, "CentreProcedureSetValidator_checkParametersWrongType", parameterInfo.getSeriesParameter()));

                            break;
                        case MEDIA_PARAMETER:

                            this.errorHandler.error(new ValidationException(message, "CentreProcedureSetValidator_checkParametersWrongType", parameterInfo.getMediaParameter()));

                            break;
                        case MEDIA_SAMPLE_PARAMETER:

                            this.errorHandler.error(new ValidationException(message, "CentreProcedureSetValidator_checkParametersWrongType", parameterInfo.getMediaSampleParameter()));

                            break;
                        case SERIES_MEDIA_PARAMETER:

                            this.errorHandler.error(new ValidationException(message, "CentreProcedureSetValidator_checkParametersWrongType", parameterInfo.getSeriesMediaParameter()));

                            break;
                        case PROCEDURE_METADATA:

                            this.errorHandler.error(new ValidationException(message, "CentreProcedureSetValidator_checkParametersWrongType", parameterInfo.getProcedureMetadata()));

                    }
                }
            }
        }
    }

    private void checkRepeatedParameters(Table<String, BigInteger, List<RepeatedParameterInfo>> parameterIds) {

        for (List<RepeatedParameterInfo> list : parameterIds.values()) {
            if (list.size() > 1) {
                for (RepeatedParameterInfo parameterInfo : list) {
                    switch (parameterInfo.getParameterType()) {
                        case SIMPLE_PARAMETER:
                            this.errorHandler.error(new ValidationException(list.get(0).getErrorMessage(list.size()), "CentreProcedureSetValidator_checkRepeatedParameters", parameterInfo.getSimpleParameter()));
                            break;
                        case ONTOLOGY_PARAMETER:
                            this.errorHandler.error(new ValidationException(list.get(0).getErrorMessage(list.size()), "CentreProcedureSetValidator_checkRepeatedParameters", parameterInfo.getOntologyParameter()));
                            break;
                        case SERIES_PARAMETER:
                            this.errorHandler.error(new ValidationException(list.get(0).getErrorMessage(list.size()), "CentreProcedureSetValidator_checkRepeatedParameters", parameterInfo.getSeriesParameter()));
                            break;
                        case MEDIA_PARAMETER:
                            this.errorHandler.error(new ValidationException(list.get(0).getErrorMessage(list.size()), "CentreProcedureSetValidator_checkRepeatedParameters", parameterInfo.getMediaParameter()));
                            break;
                        case MEDIA_SAMPLE_PARAMETER:
                            this.errorHandler.error(new ValidationException(list.get(0).getErrorMessage(list.size()), "CentreProcedureSetValidator_checkRepeatedParameters", parameterInfo.getMediaSampleParameter()));
                            break;
                        case SERIES_MEDIA_PARAMETER:
                            this.errorHandler.error(new ValidationException(list.get(0).getErrorMessage(list.size()), "CentreProcedureSetValidator_checkRepeatedParameters", parameterInfo.getSeriesMediaParameter()));
                            break;
                        case PROCEDURE_METADATA:
                            this.errorHandler.error(new ValidationException(list.get(0).getErrorMessage(list.size()), "CentreProcedureSetValidator_checkRepeatedParameters", parameterInfo.getProcedureMetadata()));
                            break;
                    }
                }

            }
        }

    }

    private Set<String> checkProcedureParameters(ImpressProcedure impressProcedure, Procedure procedure) {
        Set<String> presentParameters = Sets.newHashSetWithExpectedSize(impressProcedure.getImpressParameter().size());
        Set<String> ontologyAndSimpleParameterIds = Sets.newHashSetWithExpectedSize(procedure.getSimpleParameter().size() + procedure.getOntologyParameter().size());


        for (SimpleParameter simpleParameter : procedure.getSimpleParameter()) {
            if (simpleParameter.getParameterID().length() != simpleParameter.getParameterID().trim().length()) {
                this.errorHandler.error(new ValidationException(" parameter [" + simpleParameter.getParameterID() + "] contains blank spaces", "CentreProcedureSetValidator_checkProcedureParametersBlankSpaces", simpleParameter));
            }
            presentParameters.add(simpleParameter.getParameterID());
            //
            ontologyAndSimpleParameterIds.add(simpleParameter.getParameterID());
            //
            this.checkSimpleParameter(impressProcedure, simpleParameter);
        }
        for (OntologyParameter ontologyParameter : procedure.getOntologyParameter()) {
            if (ontologyParameter.getParameterID().length() != ontologyParameter.getParameterID().trim().length()) {
                this.errorHandler.error(new ValidationException("  parameter [" + ontologyParameter.getParameterID() + "] contains blank spaces", "CentreProcedureSetValidator_checkProcedureParametersBlankSpaces", ontologyParameter));
            }
            presentParameters.add(ontologyParameter.getParameterID());
            //
            ontologyAndSimpleParameterIds.add(ontologyParameter.getParameterID());
            //
            this.checkOntologyParameter(impressProcedure, ontologyParameter);
        }
        for (SeriesParameter seriesParameter : procedure.getSeriesParameter()) {
            if (seriesParameter.getParameterID().length() != seriesParameter.getParameterID().trim().length()) {
                this.errorHandler.error(new ValidationException(" parameter [" + seriesParameter.getParameterID() + "] contains blank spaces", "CentreProcedureSetValidator_checkProcedureParametersBlankSpaces", seriesParameter));
            }
            presentParameters.add(seriesParameter.getParameterID());
            //
            this.checkSeriesParameter(impressProcedure, seriesParameter);
        }
        for (MediaParameter mediaParameter : procedure.getMediaParameter()) {
            if (mediaParameter.getParameterID().length() != mediaParameter.getParameterID().trim().length()) {
                this.errorHandler.error(new ValidationException(" parameter [" + mediaParameter.getParameterID() + "] contains blank spaces", "CentreProcedureSetValidator_checkProcedureParametersBlankSpaces", mediaParameter));
            }
            presentParameters.add(mediaParameter.getParameterID());
            //
            this.checkMediaParameter(impressProcedure, mediaParameter, ontologyAndSimpleParameterIds);
        }

        for (MediaSampleParameter mediaSampleParameter : procedure.getMediaSampleParameter()) {
            if (mediaSampleParameter.getParameterID().length() != mediaSampleParameter.getParameterID().trim().length()) {
                this.errorHandler.error(new ValidationException(" parameter [" + mediaSampleParameter.getParameterID() + "] contains blank spaces", "CentreProcedureSetValidator_checkProcedureParametersBlankSpaces", mediaSampleParameter));
            }
            presentParameters.add(mediaSampleParameter.getParameterID());
            //
            this.checkMediaSampleParameter(impressProcedure, mediaSampleParameter, ontologyAndSimpleParameterIds);
        }
        for (SeriesMediaParameter seriesMediaParameter : procedure.getSeriesMediaParameter()) {
            if (seriesMediaParameter.getParameterID().length() != seriesMediaParameter.getParameterID().trim().length()) {
                this.errorHandler.error(new ValidationException(" parameter [" + seriesMediaParameter.getParameterID() + "] contains blank spaces", "CentreProcedureSetValidator_checkProcedureParametersBlankSpaces", seriesMediaParameter));
            }
            presentParameters.add(seriesMediaParameter.getParameterID());
            this.checkSeriesMediaParameter(impressProcedure, seriesMediaParameter, ontologyAndSimpleParameterIds);
        }

        for (ProcedureMetadata procedureMetadata : procedure.getProcedureMetadata()) {
            if (procedureMetadata.getParameterID().length() != procedureMetadata.getParameterID().trim().length()) {
                this.errorHandler.error(new ValidationException(" parameter [" + procedureMetadata.getParameterID() + "] contains blank spaces", "CentreProcedureSetValidator_checkProcedureParametersBlankSpaces", procedureMetadata));
            }
            presentParameters.add(procedureMetadata.getParameterID());
            this.checkProcedureMetadata(impressProcedure, procedureMetadata);
        }

        return presentParameters;
    }

    private boolean validateParameterStatus(String value) {
        if (this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.Statuscodes) != null) {
            return ((StatusCodesBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.Statuscodes)).exists(value);
        } else {
            return true;
        }
    }

    private void checkProcedureMetadata(ImpressProcedure impressProcedure, ProcedureMetadata procedureMetadata) {
        ImpressParameter impressParameter = this.impressBrowser().get(impressProcedure, procedureMetadata.getParameterID(), ImpressParameterType.PROCEDURE_METADATA);
        if (impressParameter == null) {
            this.errorHandler.error(new ValidationException("Metadata parameter [" + procedureMetadata.getParameterID() + "] does not exist for " + impressProcedure.getProcedureKey(), "CentreProcedureSetValidator_procedureMetadataUndefined", procedureMetadata));
            return;
        }
        if (procedureMetadata.getParameterStatus() != null && !procedureMetadata.getParameterStatus().isEmpty()) {
            if (!validateParameterStatus(procedureMetadata.getParameterStatus())) {
                this.errorHandler.error(new ValidationException("parameterStatus " + procedureMetadata.getParameterStatus() + " is not registered in the database", "CentreProcedureSetValidator_procedureMetadataParameterStatusInvalid", procedureMetadata));
            }
            return;
        }

        if (procedureMetadata.getValue() == null || procedureMetadata.getValue().isEmpty()) {
            this.errorHandler.error(new ValidationException("Value cannot be null", "CentreProcedureSetValidator_procedureMetadataNullValue", procedureMetadata));
            return;
        }

        try {
            Caster.cast(procedureMetadata.getValue(), this.impressBrowser().get(impressProcedure, procedureMetadata.getParameterID(), ImpressParameterType.PROCEDURE_METADATA).getValueType());
        } catch (NumberFormatException ex) {
            this.errorHandler.error(new ValidationException("Value for " + procedureMetadata.getParameterID() + " was " + procedureMetadata.getValue() + " but should be of type " + ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).get(impressProcedure, procedureMetadata.getParameterID(), ImpressParameterType.PROCEDURE_METADATA).getValueType(), "CentreProcedureSetValidator_procedureMetadataCastOperationFailed", procedureMetadata, ex));
        } catch (ParseException ex) {
            this.errorHandler.error(new ValidationException("Value for " + procedureMetadata.getParameterID() + " was " + procedureMetadata.getValue() + " but should be of type " + ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).get(impressProcedure, procedureMetadata.getParameterID(), ImpressParameterType.PROCEDURE_METADATA).getValueType(), "CentreProcedureSetValidator_procedureMetadataCastOperationFailed", procedureMetadata, ex));
        } catch (Exception ex) {
            this.errorHandler.error(new ValidationException("Value for " + procedureMetadata.getParameterID() + " was " + procedureMetadata.getValue() + " but should be of type " + ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).get(impressProcedure, procedureMetadata.getParameterID(), ImpressParameterType.PROCEDURE_METADATA).getValueType(), "CentreProcedureSetValidator_procedureMetadataCastOperationFailed", procedureMetadata, ex));
        }


        List<ImpressParameterOption> impressParameterOptions = impressParameter.getImpressParameterOption();
        if (impressParameterOptions == null || impressParameterOptions.size() < 1) {
            return;
        }
        boolean isValidOption = false;
        for (ImpressParameterOption impressParameterOption : impressParameterOptions) {
            if (procedureMetadata.getValue() != null && impressParameterOption.getName() != null
                    && procedureMetadata.getValue().equals(impressParameterOption.getName())) {
                isValidOption = true;
                break;
            }
        }
        if (!isValidOption) {
            this.errorHandler.error(new ValidationException(procedureMetadata.getValue() + " is not a valid option for " + procedureMetadata.getParameterID(), "CentreProcedureSetValidator_procedureMetadataNoValidOption", procedureMetadata));
        }
    }

    private void checkMediaSampleParameter(ImpressProcedure impressProcedure, MediaSampleParameter mediaSampleParameter, Set<String> ontologyAndSimpleParameterIds) {
        ImpressParameter impressParameter = this.impressBrowser().get(impressProcedure, mediaSampleParameter.getParameterID(), ImpressParameterType.MEDIA_SAMPLE_PARAMETER);
        if (impressParameter == null) {
            this.errorHandler.error(new ValidationException("Media sample parameter [" + mediaSampleParameter.getParameterID() + "] does not exist for " + impressProcedure.getProcedureKey(), "CentreProcedureSetValidator_mediaSampleParameterUndefined", mediaSampleParameter));
            return;
        }

        if (mediaSampleParameter.getParameterStatus() != null && !mediaSampleParameter.getParameterStatus().isEmpty()) {
            if (!validateParameterStatus(mediaSampleParameter.getParameterStatus())) {
                this.errorHandler.error(new ValidationException("parameterStatus " + mediaSampleParameter.getParameterStatus() + " is not registered in the database", "CentreProcedureSetValidator_mediaSampleParameterParameterStatusInvalid", mediaSampleParameter));
            }
            return;
        }


        if (mediaSampleParameter.getMediaSample() != null && mediaSampleParameter.getMediaSample().size() > 0) {
            //  List<String> mediaSampleLocalIDs = new ArrayList<String>(mediaSampleParameter.getMediaSample().size());
            //   List<String> mediaSectionLocalIDs = new ArrayList<String>();
            for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
                //     mediaSampleLocalIDs.add(mediaSample.getLocalId());
                if (mediaSample.getMediaSection() == null || mediaSample.getMediaSection().isEmpty()) {
                    this.errorHandler.error(new ValidationException("Media sample  " + mediaSample.getLocalId() + " has no mediasections", "CentreProcedureSetValidator_mediaSampleNoMediaSections", mediaSample));
                    continue;
                }
                for (MediaSection mediaSection : mediaSample.getMediaSection()) {
                    //       mediaSectionLocalIDs.add(mediaSection.getLocalId());
                    for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                        try {
                            CentreProcedureSetValidatorSupport.isMediaAvailable(mediaFile.getURI());
                        } catch (Exception ex) {
                            this.errorHandler.error(new ValidationException("Media file " + mediaFile.getURI() + " is not available for parameter " + mediaSampleParameter.getParameterID(), "CentreProcedureSetValidator_MediaSampleParameter_mediaNotAvailable", mediaFile));
                        }
                        if (!CentreProcedureSetValidatorSupport.fileTypeSupported(mediaFile.getFileType())) {
                            this.errorHandler.error(new ValidationException("Media file " + mediaFile.getFileType() + " is not supported in paramter " + mediaSampleParameter.getParameterID(), "CentreProcedureSetValidator_MediaSampleParameter_fileNoSupported", mediaFile));
                        }

                        if (mediaFile.getParameterAssociation().size() > 0) {
                            for (ParameterAssociation parameterAssociation : mediaFile.getParameterAssociation()) {
                                this.checkParameterAssociation(parameterAssociation, ontologyAndSimpleParameterIds);
                            }
                        }
                    }
                }
            }
        }
        //
    }

    private void checkSeriesMediaParameter(ImpressProcedure impressProcedure, SeriesMediaParameter seriesMediaParameter, Set<String> ontologyAndSimpleParameterIds) {
        ImpressParameter impressParameter = this.impressBrowser().get(impressProcedure, seriesMediaParameter.getParameterID(), ImpressParameterType.SERIES_MEDIA_PARAMETER);
        if (impressParameter == null) {
            this.errorHandler.error(new ValidationException("Series media parameter [" + seriesMediaParameter.getParameterID() + "] does not exist for " + impressProcedure.getProcedureKey(), "CentreProcedureSetValidator_seriesMediaParameterUndefined", seriesMediaParameter));
            return;
        }

        if (seriesMediaParameter.getParameterStatus() != null && !seriesMediaParameter.getParameterStatus().isEmpty()) {
            if (!validateParameterStatus(seriesMediaParameter.getParameterStatus())) {
                this.errorHandler.error(new ValidationException("parameterStatus " + seriesMediaParameter.getParameterStatus() + " is not registered in the database", "CentreProcedureSetValidator_seriesMediaParameterParameterStatusInvalid", seriesMediaParameter));
            }
            return;
        }

        if (seriesMediaParameter.getValue() == null || seriesMediaParameter.getValue().isEmpty()) {
            this.errorHandler.error(new ValidationException("SeriesMediaParameter has to contain values", "CentreProcedureSetValidator_seriesMediaParameterNullValue", seriesMediaParameter));
            return;
        }

        for (SeriesMediaParameterValue seriesMediaParameterValue : seriesMediaParameter.getValue()) {
            try {
                CentreProcedureSetValidatorSupport.isMediaAvailable(seriesMediaParameterValue.getURI());
            } catch (Exception ex) {
                this.errorHandler.error(new ValidationException("Media file " + seriesMediaParameterValue.getURI() + " is not available for parameter " + seriesMediaParameter.getParameterID(), "CentreProcedureSetValidator_seriesMediaParameter_mediaNotAvailable", seriesMediaParameterValue));
            }
            if (!CentreProcedureSetValidatorSupport.fileTypeSupported(seriesMediaParameterValue.getFileType())) {
                this.errorHandler.error(new ValidationException("Media file " + seriesMediaParameterValue.getFileType() + " is not supported in parameter " + seriesMediaParameter.getParameterID(), "CentreProcedureSetValidator_seriesMediaParameter_fileNoSupported", seriesMediaParameterValue));
            }
            if (seriesMediaParameterValue.getParameterAssociation().size() > 0) {
                for (ParameterAssociation parameterAssociation : seriesMediaParameterValue.getParameterAssociation()) {
                    this.checkParameterAssociation(parameterAssociation, ontologyAndSimpleParameterIds);
                }
            }
        }

        /*
         if increment min >0
         check that there are as many values as min ie
	
         IMPRESSPROCEDURE.PROCEDUREKEY = 'IMPC_CAL_001' and
         IMPRESSPARAMETER.PARAMETERKEY = 'IMPC_CAL_003_001'
         
         * if increment min ==0 
         you can have an unlimited number of values 
	
         if increment_string !=null
         incrementValue needs to be one of increment string

         if increment_type ==  datetime
         cast to datetime
         
         */

        List<ImpressParameterIncrement> impressParameterIncrements = impressParameter.getImpressParameterIncrement();

        if (impressParameterIncrements == null || impressParameterIncrements.size() < 1) {
            return;
        }
        if (impressParameterIncrements.size() == 1) {
            if (impressParameterIncrements.get(0).getMin() != null) {
                if (impressParameterIncrements.get(0).getMin().intValue() > 0
                        && seriesMediaParameter.getValue().size() < impressParameterIncrements.get(0).getMin().intValue()) {
                    this.errorHandler.error(new ValidationException("Series parameter " + seriesMediaParameter.getParameterID() + " does not provide " + impressParameterIncrements.get(0).getMin().intValue() + " parameters", "CentreProcedureSetValidator_seriesMediaParameterEnoughParameters", seriesMediaParameter));
                }
            }
        }
        if (impressParameterIncrements.get(0).getString() != null && !impressParameterIncrements.get(0).getString().isEmpty()) {

            boolean isValid = false;
            Map<String, Boolean> requiredIncrementValues = new HashMap<>();
            for (SeriesMediaParameterValue seriesParameterValue : seriesMediaParameter.getValue()) {
                isValid = false;
                for (ImpressParameterIncrement impressParameterIncrement : impressParameterIncrements) {
                    if (requiredIncrementValues.get(impressParameterIncrement.getString()) == null
                            || !requiredIncrementValues.get(impressParameterIncrement.getString())) {
                        requiredIncrementValues.put(impressParameterIncrement.getString(), isValid);
                    }
                    if (seriesParameterValue.getIncrementValue().equals(impressParameterIncrement.getString())) {
                        isValid = true;
                        break;
                    }
                }
                if (!isValid) {
                    this.errorHandler.error(new ValidationException(seriesParameterValue.getIncrementValue() + " is not a valid increment for " + seriesMediaParameter.getParameterID(), "CentreProcedureSetValidator_seriesMediaParameterNoValidIncrement", seriesParameterValue));
                }
            }
            for (Entry<String, Boolean> entry : requiredIncrementValues.entrySet()) {
                if (!entry.getValue()) {
                    this.errorHandler.error(new ValidationException(entry.getKey() + " is a missing increment for parameter " + seriesMediaParameter.getParameterID(), "CentreProcedureSetValidator_seriesParameterMissingIncrement", seriesMediaParameter));
                }
            }

        }

        if (impressParameterIncrements.get(0).getType() != null && impressParameterIncrements.get(0).getType().equals("datetime")) {
            for (SeriesMediaParameterValue seriesParameterValue : seriesMediaParameter.getValue()) {
                try {
                    DatatypeConverter.parseDateTime(seriesParameterValue.getIncrementValue());
                } catch (Exception ex) {
                    this.errorHandler.error(new ValidationException(seriesParameterValue.getIncrementValue() + " is not a valid increment for " + seriesMediaParameter.getParameterID(), "CentreProcedureSetValidator_seriesMediaParameterNoValidIncrementType", seriesParameterValue));
                }
            }
        }

        if (impressParameterIncrements.get(0).getType() != null && impressParameterIncrements.get(0).getType().equals("float")) {
            for (SeriesMediaParameterValue seriesParameterValue : seriesMediaParameter.getValue()) {
                try {
                    Caster.cast(seriesParameterValue.getIncrementValue(), "FLOAT");
                } catch (Exception ex) {
                    this.errorHandler.error(new ValidationException(seriesParameterValue.getIncrementValue() + " is not a valid increment for " + seriesMediaParameter.getParameterID(), "CentreProcedureSetValidator_seriesMediaParameterNoValidIncrementType", seriesParameterValue));
                }
            }
        }
        if (impressParameterIncrements.get(0).getType() != null && impressParameterIncrements.get(0).getType().equals("float")) {
            for (SeriesMediaParameterValue seriesParameterValue : seriesMediaParameter.getValue()) {
                try {
                    Caster.cast(seriesParameterValue.getIncrementValue(), "INT");
                } catch (Exception ex) {
                    this.errorHandler.error(new ValidationException(seriesParameterValue.getIncrementValue() + " is not a valid increment for " + seriesMediaParameter.getParameterID(), "CentreProcedureSetValidator_seriesMediaParameterNoValidIncrementType", seriesParameterValue));
                }
            }
        }

    }

    private void checkParameterAssociation(ParameterAssociation parameterAssociation, Set<String> ontologyAndSimpleParameterIds) {
        if (!ontologyAndSimpleParameterIds.contains(parameterAssociation.getParameterID())) {
            this.errorHandler.error(new ValidationException("Parameter " + parameterAssociation.getParameterID() + " not in the simple or ontology parameters ", "CentreProcedureSetValidator_parameterAssociationUndefined", parameterAssociation));
        }
        if (parameterAssociation.getDim().size() > 0) {
            for (Dimension dimension : parameterAssociation.getDim()) {
                //do something
            }
        }
    }

    private void checkMediaParameter(ImpressProcedure impressProcedure, MediaParameter mediaParameter, Set<String> ontologyAndSimpleParameterIds) {
        ImpressParameter impressParameter = this.impressBrowser().get(impressProcedure, mediaParameter.getParameterID(), ImpressParameterType.MEDIA_PARAMETER);
        if (impressParameter == null) {
            this.errorHandler.error(new ValidationException("Media parameter [" + mediaParameter.getParameterID() + "] does not exist for " + impressProcedure.getProcedureKey(), "CentreProcedureSetValidator_mediaParameterUndefined", mediaParameter));
            return;
        }

        if (mediaParameter.getParameterStatus() != null && !mediaParameter.getParameterStatus().isEmpty()) {
            if (!validateParameterStatus(mediaParameter.getParameterStatus())) {
                this.errorHandler.error(new ValidationException("parameterStatus " + mediaParameter.getParameterStatus() + " is not registered in the database", "CentreProcedureSetValidator_mediaParameterParameterStatusInvalid", mediaParameter));
            }
            return;
        }


        //

        try {
            CentreProcedureSetValidatorSupport.isMediaAvailable(mediaParameter.getURI());
        } catch (Exception ex) {
            this.errorHandler.error(new ValidationException("Media parameter " + mediaParameter.getURI() + " is not available for paramter " + mediaParameter.getParameterID(), "CentreProcedureSetValidator_mediaParameter_MediaUnavailable", mediaParameter));
        }
        if (!CentreProcedureSetValidatorSupport.fileTypeSupported(mediaParameter.getFileType())) {
            this.errorHandler.error(new ValidationException("Media file " + mediaParameter.getFileType() + " is not supported in parameter " + mediaParameter.getParameterID(), "CentreProcedureSetValidator_mediaPamareter_FileNonSupported", mediaParameter));
        }

        if (mediaParameter.getParameterAssociation().size() > 0) {
            for (ParameterAssociation parameterAssociation : mediaParameter.getParameterAssociation()) {
                checkParameterAssociation(parameterAssociation, ontologyAndSimpleParameterIds);
            }
        }


    }

    private void checkSeriesParameter(ImpressProcedure impressProcedure, SeriesParameter seriesParameter) {
        ImpressParameter impressParameter = this.impressBrowser().get(impressProcedure, seriesParameter.getParameterID(), ImpressParameterType.SERIES_PARAMETER);
        if (impressParameter == null) {
            this.errorHandler.error(new ValidationException("Series parameter [" + seriesParameter.getParameterID() + "] does not exist for " + impressProcedure.getProcedureKey(), "CentreProcedureSetValidator_seriesParameterUndefined", seriesParameter));
            return;
        }

        if (seriesParameter.getParameterStatus() != null && !seriesParameter.getParameterStatus().isEmpty()) {
            if (!validateParameterStatus(seriesParameter.getParameterStatus())) {
                this.errorHandler.error(new ValidationException("parameterStatus " + seriesParameter.getParameterStatus() + " is not registered in the database", "CentreProcedureSetValidator_seriesParameterParameterStatusInvalid", seriesParameter));
            }
            return;
        }


        if (seriesParameter.getValue() == null || seriesParameter.getValue().isEmpty()) {
            this.errorHandler.error(new ValidationException("Value for " + seriesParameter.getParameterID() + " cannot be null", "CentreProcedureSetValidator_seriesParameterNullValue", seriesParameter));
            return;
        }


        for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
            if (seriesParameterValue.getIncrementStatus() != null && !seriesParameterValue.getIncrementStatus().isEmpty()) { //there is increment status
                if (seriesParameterValue.getValue() != null && !seriesParameterValue.getValue().isEmpty()) {// and there is parameter value
                    this.errorHandler.error(new ValidationException("Series parameter value for " + seriesParameter.getParameterID() + "has either a value or an incrementStatus", "CentreProcedureSetValidator_seriesParameterValueNotEmptyWithIncrementStatus", seriesParameterValue));
                }//there is increment status one, do nothing
                continue;
            }



            if (seriesParameterValue.getValue() == null || seriesParameterValue.getValue().isEmpty()) {// there is no value
                this.errorHandler.error(new ValidationException("Series parameter value for " + seriesParameter.getParameterID() + " is empty", "CentreProcedureSetValidator_seriesParameterValueEmpty", seriesParameterValue));
                continue;
            }
            if (seriesParameterValue.getValue() != null) {
                try {
                    Caster.cast(seriesParameterValue.getValue(), this.impressBrowser().get(impressProcedure, seriesParameter.getParameterID(), ImpressParameterType.SERIES_PARAMETER).getValueType());
                } catch (NumberFormatException ex) {
                    this.errorHandler.error(new ValidationException("cast type wrong for " + seriesParameterValue.getValue() + " increment " + seriesParameterValue.getIncrementValue() + ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).get(impressProcedure, seriesParameter.getParameterID(), ImpressParameterType.SERIES_PARAMETER).getValueType(), "CentreProcedureSetValidator_seriesParameterCastOperationFailed", seriesParameterValue, ex));
                } catch (ParseException ex) {
                    this.errorHandler.error(new ValidationException("cast type wrong for " + seriesParameterValue.getValue() + " increment " + seriesParameterValue.getIncrementValue() + ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).get(impressProcedure, seriesParameter.getParameterID(), ImpressParameterType.SERIES_PARAMETER).getValueType(), "CentreProcedureSetValidator_seriesParameterCastOperationFailed", seriesParameterValue, ex));
                } catch (Exception ex) {
                    this.errorHandler.error(new ValidationException("cast type wrong for " + seriesParameterValue.getValue() + " increment " + seriesParameterValue.getIncrementValue() + ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).get(impressProcedure, seriesParameter.getParameterID(), ImpressParameterType.SERIES_PARAMETER).getValueType(), "CentreProcedureSetValidator_seriesParameterCastOperationFailed", seriesParameterValue, ex));
                }
            }
        }

        List<ImpressParameterOption> impressParameterOptions = impressParameter.getImpressParameterOption();
        if (impressParameterOptions != null && impressParameterOptions.size() > 0) {
            boolean isValidOption = false;
            for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                isValidOption = false;
                for (ImpressParameterOption impressParameterOption : impressParameterOptions) {
                    if (seriesParameterValue.getValue().equals(impressParameterOption.getName())) {
                        isValidOption = true;
                        break;
                    }
                }
                if (!isValidOption) {
                    this.errorHandler.error(new ValidationException(seriesParameterValue.getValue() + " is not a valid option for " + seriesParameter.getParameterID(), "CentreProcedureSetValidator_seriesParameterNoValidOption", seriesParameterValue));
                }
            }
        }
        /*
         if increment min >0
         check that there are as many values as min
	
         if increment min ==0 
         you can have an unlimited number of values 
	
         if increment_string !=null
         incrementValue needs to be one of increment string

         if increment_type ==  datetime
         cast to datetime
         
         */

        List<ImpressParameterIncrement> impressParameterIncrements = impressParameter.getImpressParameterIncrement();

        if (impressParameterIncrements == null || impressParameterIncrements.size() < 1) {
            return;
        }
        if (impressParameterIncrements.size() == 1) {
            if (impressParameterIncrements.get(0).getMin() != null) {
                if (impressParameterIncrements.get(0).getMin().intValue() > 0
                        && seriesParameter.getValue().size() < impressParameterIncrements.get(0).getMin().intValue()) {
                    this.errorHandler.error(new ValidationException("Series parameter " + seriesParameter.getParameterID() + " does not provide " + impressParameterIncrements.get(0).getMin().intValue() + " parameters", "CentreProcedureSetValidator_seriesParameterEnoughParameters", seriesParameter));
                }
            }
        }
        if (impressParameterIncrements.get(0).getString() != null && !impressParameterIncrements.get(0).getString().isEmpty()) {

            boolean isValid = false;
            Map<String, Boolean> requiredIncrementValues = new HashMap<>();
            for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                isValid = false;
                for (ImpressParameterIncrement impressParameterIncrement : impressParameterIncrements) {
                    if (requiredIncrementValues.get(impressParameterIncrement.getString()) == null
                            || !requiredIncrementValues.get(impressParameterIncrement.getString())) {
                        requiredIncrementValues.put(impressParameterIncrement.getString(), isValid);
                    }
                    if (seriesParameterValue.getIncrementValue().equals(impressParameterIncrement.getString())) {
                        isValid = true;
                        requiredIncrementValues.put(impressParameterIncrement.getString(), isValid);
                        break;
                    }
                }
                if (!isValid) {
                    this.errorHandler.error(new ValidationException(seriesParameterValue.getIncrementValue() + " is not a valid increment for " + seriesParameter.getParameterID(), "CentreProcedureSetValidator_seriesParameterNoValidIncrement", seriesParameterValue));
                }

            }
            for (Entry<String, Boolean> entry : requiredIncrementValues.entrySet()) {
                if (!entry.getValue()) {
                    this.errorHandler.error(new ValidationException(entry.getKey() + " is a missing increment for parameter " + seriesParameter.getParameterID(), "CentreProcedureSetValidator_seriesParameterMissingIncrement", seriesParameter));
                }
            }

        }

        if (impressParameterIncrements.get(0).getType() != null && impressParameterIncrements.get(0).getType().equals("datetime")) {
            for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                try {
                    DatatypeConverter.parseDateTime(seriesParameterValue.getIncrementValue());
                } catch (Exception ex) {
                    this.errorHandler.error(new ValidationException(seriesParameterValue.getIncrementValue() + " is not a valid increment for " + seriesParameter.getParameterID(), "CentreProcedureSetValidator_seriesParameterNoValidIncrementType", seriesParameterValue));
                }
            }
        }

        if (impressParameterIncrements.get(0).getType() != null && impressParameterIncrements.get(0).getType().equals("float")) {
            for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                try {
                    Caster.cast(seriesParameterValue.getIncrementValue(), "FLOAT");
                } catch (Exception ex) {
                    this.errorHandler.error(new ValidationException(seriesParameterValue.getIncrementValue() + " is not a valid increment for " + seriesParameter.getParameterID(), "CentreProcedureSetValidator_seriesParameterNoValidIncrementType", seriesParameterValue));
                }
            }
        }
        if (impressParameterIncrements.get(0).getType() != null && impressParameterIncrements.get(0).getType().equals("float")) {
            for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                try {
                    Caster.cast(seriesParameterValue.getIncrementValue(), "INT");
                } catch (Exception ex) {
                    this.errorHandler.error(new ValidationException(seriesParameterValue.getIncrementValue() + " is not a valid increment for " + seriesParameter.getParameterID(), "CentreProcedureSetValidator_seriesParameterNoValidIncrementType", seriesParameterValue));
                }
            }
        }


    }

    private boolean checkIsEmpty(List<String> terms) {
        for (String term : terms) {
            if (term.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void checkOntologyParameter(ImpressProcedure impressProcedure, OntologyParameter ontologyParameter) {
        ImpressParameter impressParameter = this.impressBrowser().get(impressProcedure, ontologyParameter.getParameterID(), ImpressParameterType.ONTOLOGY_PARAMETER);
        if (impressParameter == null) {
            this.errorHandler.error(new ValidationException("Ontology Parameter [" + ontologyParameter.getParameterID() + "] does not exist for " + impressProcedure.getProcedureKey(), "CentreProcedureSetValidator_ontologyParameterUndefined", ontologyParameter));
        }

        if (ontologyParameter.getParameterStatus() != null && !ontologyParameter.getParameterStatus().isEmpty()) {
            if (!validateParameterStatus(ontologyParameter.getParameterStatus())) {
                this.errorHandler.error(new ValidationException("parameterStatus " + ontologyParameter.getParameterStatus() + " is not registered in the database", "CentreProcedureSetValidator_ontologyParameterStatusInvalid", ontologyParameter));
            }
            return;
        }

        if (ontologyParameter.getTerm() == null || ontologyParameter.getTerm().isEmpty() || this.checkIsEmpty(ontologyParameter.getTerm())) {
            this.errorHandler.error(new ValidationException("Terms for " + ontologyParameter.getParameterID() + " cannot be empty", "CentreProcedureSetValidator_ontologyParameterTermEmpty", ontologyParameter));
        } else {
            boolean isValid = false;
            for (String term : ontologyParameter.getTerm()) {

                isValid = false;
                for (ImpressOntologyParameterOption parameterOntologyOption : impressParameter.getImpressOntologyParameterOption()) {
                    if (term.startsWith(parameterOntologyOption.getOntologyId())) {
                        isValid = true;
                        break;
                    }
                }
                if (!isValid) {
                    this.errorHandler.error(new ValidationException("ontologyTerm " + term + " is not registered in the database", "CentreProcedureSetValidator_ontologyParameterTermInvalid", ontologyParameter));
                }
            }
        }
    }

    private void checkSimpleParameter(ImpressProcedure impressProcedure, SimpleParameter simpleParameter) {
        ImpressParameter impressParameter = this.impressBrowser().get(impressProcedure, simpleParameter.getParameterID(), ImpressParameterType.SIMPLE_PARAMETER);
        if (impressParameter == null) {
            this.errorHandler.error(new ValidationException("Simple parameter [" + simpleParameter.getParameterID() + "] does not exist for " + impressProcedure.getProcedureKey(), "CentreProcedureSetValidator_simpleParameterUndefined", simpleParameter));
            return;
        }
        if (simpleParameter.getParameterStatus() != null && !simpleParameter.getParameterStatus().isEmpty()) {

            if (!validateParameterStatus(simpleParameter.getParameterStatus())) {
                this.errorHandler.error(new ValidationException("parameterStatus " + simpleParameter.getParameterStatus() + " is not registered in the database", "CentreProcedureSetValidator_simpleParameterStatusInvalid", simpleParameter));
            }
            return;
        }
        if (simpleParameter.getValue() == null || simpleParameter.getValue().isEmpty()) {
            this.errorHandler.error(new ValidationException("Value for " + simpleParameter.getParameterID() + " cannot be empty", "CentreProcedureSetValidator_simpleParameterValueEmpty", simpleParameter));
            return;
        }


        try {
            Caster.cast(simpleParameter.getValue(), this.impressBrowser().get(impressProcedure, simpleParameter.getParameterID(), ImpressParameterType.SIMPLE_PARAMETER).getValueType());
        } catch (NumberFormatException ex) {
            this.errorHandler.error(new ValidationException("Value for " + simpleParameter.getParameterID() + " was " + simpleParameter.getValue() + " but should be of type " + ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).get(impressProcedure, simpleParameter.getParameterID(), ImpressParameterType.SIMPLE_PARAMETER).getValueType(), "CentreProcedureSetValidator_simpleParameterCastOperationFailed", simpleParameter, ex));
        } catch (ParseException ex) {
            this.errorHandler.error(new ValidationException("Value for " + simpleParameter.getParameterID() + " was " + simpleParameter.getValue() + " but should be of type " + ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).get(impressProcedure, simpleParameter.getParameterID(), ImpressParameterType.SIMPLE_PARAMETER).getValueType(), "CentreProcedureSetValidator_simpleParameterCastOperationFailed", simpleParameter, ex));
        } catch (Exception ex) {
            this.errorHandler.error(new ValidationException("Value for " + simpleParameter.getParameterID() + " was " + simpleParameter.getValue() + " but should be of type " + ((ImpressBrowser) this.xmlValidationResources.get(VALIDATIONRESOURCES_IDS.ImpressBrowser)).get(impressProcedure, simpleParameter.getParameterID(), ImpressParameterType.SIMPLE_PARAMETER).getValueType(), "CentreProcedureSetValidator_simpleParameterCastOperationFailed", simpleParameter, ex));
        }


        List<ImpressParameterOption> impressParameterOptions = impressParameter.getImpressParameterOption();
        if (impressParameterOptions == null || impressParameterOptions.size() < 1) {
            return;
        }
        boolean isValidOption = false;
        for (ImpressParameterOption impressParameterOption : impressParameterOptions) {
            if (simpleParameter.getValue().equals(impressParameterOption.getName())) {
                isValidOption = true;
                break;
            }
        }
        if (!isValidOption) {
            this.errorHandler.error(new ValidationException(simpleParameter.getValue() + " is not a valid option for " + simpleParameter.getParameterID(), "CentreProcedureSetValidator_simpleParameterNoValidOption", simpleParameter));
        }

    }

    private void checkCentreProcedureAttributes(CentreProcedure centreProcedure) {
        if (projects != null && projects.size() > 0 && !projects.contains(centreProcedure.getProject())) {
            this.errorHandler.warning(new ValidationException("Project " + centreProcedure.getProject() + " does not exist", "CentreProcedureSetValidator_centreProcedureAttributes_ProjectNotFound", centreProcedure));
        }
    }

    private void checkExperimentAttributes(Experiment experiment, CentreProcedure centreProcedure) {
        if (experiment.getDateOfExperiment() == null) {
            this.errorHandler.error(new ValidationException("Experiment date is null", "CentreProcedureSetValidator_experimentAttributes_DateNull", experiment));
        }

    }

    private void checkColonySubmittedToDCC(Line line, CentreProcedure centre) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        logger.info("reached checkColonySubmittedToDCC");
    }
}
