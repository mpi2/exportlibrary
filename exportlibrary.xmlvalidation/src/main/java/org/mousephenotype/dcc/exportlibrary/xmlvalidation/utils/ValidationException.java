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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.utils;

import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ExceptionLevel;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;


/**
 *
 * @author julian
 */
public final class ValidationException extends RuntimeException {

    Validation validation;

    private ValidationException(String message, String testID) {
        super(message);
        this.getValidation().setMessage(message);
        this.getValidation().setTestID(testID);
    }

    @Override
    public String toString(){
        return this.getValidation().getMessage();
    }
    
    public ValidationException(String message, String testID, Throwable cause) {
        super(message, cause);
        this.getValidation().setMessage(message);
        this.getValidation().setTestID(testID);
    }

    public void setLevel(ExceptionLevel level) {
        this.getValidation().setLevel(level);
    }

    public Validation getValidation() {
        if (this.validation == null) {
            this.validation = new Validation();
        }
        return this.validation;
    }

    public ValidationException(String message, String testID, CentreProcedure centreProcedure) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setCentreProcedure(centreProcedure);
        this.getValidation().setMessage(message);
    }

    public ValidationException(String message, String testID, Procedure procedure) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setProcedure(procedure);
        this.getValidation().setMessage(message);
    }

    public ValidationException(String message, String testID, SimpleParameter simpleParameter) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setSimpleParameter(simpleParameter);
        this.getValidation().setMessage(message);
    }

    public ValidationException(String message, String testID, SimpleParameter simpleParameter, Throwable cause) {
        super(message, cause);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setSimpleParameter(simpleParameter);
        this.getValidation().setMessage(message);
    }

    public ValidationException(String message, String testID, Line line) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setLine(line);
    }

    public ValidationException(String message, String testID, Housing housing) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setHousing(housing);
    }

    public ValidationException(String message, String testID, Experiment experiment) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setExperiment(experiment);
    }

    public ValidationException(String message, String testID, OntologyParameter ontologyParameter) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setOntologyParameter(ontologyParameter);
    }

    public ValidationException(String message, String testID, SeriesParameter seriesParameter) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setSeriesParameter(seriesParameter);
    }

    public ValidationException(String message, String testID, SeriesParameterValue seriesParameterValue) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setSeriesParameterValue(seriesParameterValue);
    }

    public ValidationException(String message, String testID, SeriesParameterValue seriesParameterValue, Throwable cause) {
        super(message, cause);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setSeriesParameterValue(seriesParameterValue);
    }

    public ValidationException(String message, String testID, MediaParameter mediaParameter) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setMediaParameter(mediaParameter);
    }

    public ValidationException(String message, String testID, MediaSampleParameter mediaSampleParameter) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setMediaSampleParameter(mediaSampleParameter);
    }

    public ValidationException(String message, String testID, MediaSample mediaSample) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setMediaSample(mediaSample);
    }

    public ValidationException(String message, String testID, MediaSection mediaSection) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setMediaSection(mediaSection);
    }

    public ValidationException(String message, String testID, MediaFile mediaFile) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setMediaFile(mediaFile);
    }

    public ValidationException(String message, String testID, ParameterAssociation parameterAssociation) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setParameterAssociation(parameterAssociation);
    }

    public ValidationException(String message, String testID, Dimension dimension) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setDimension(dimension);
    }

    public ValidationException(String message, String testID, SeriesMediaParameter seriesMediaParameter) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setSeriesMediaParameter(seriesMediaParameter);
    }

    public ValidationException(String message, String testID, SeriesMediaParameterValue seriesMediaParameterValue) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setSeriesMediaParameterValue(seriesMediaParameterValue);
    }

    public ValidationException(String message, String testID, ProcedureMetadata procedureMetadata) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setProcedureMetadata(procedureMetadata);
    }
    
    public ValidationException(String message, String testID, ProcedureMetadata procedureMetadata, Throwable cause) {
        super(message, cause);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setProcedureMetadata(procedureMetadata);
        this.getValidation().setMessage(message);
    }
    

    public ValidationException(String message, String testID, CentreSpecimen centreSpecimen) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setCentreSpecimen(centreSpecimen);
    }

    
    public ValidationException(String message, String testID, Specimen specimen) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setSpecimen(specimen);
    }
    
    
    public ValidationException(String message, String testID, Mouse mouse) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setMouse(mouse);
    }

    public ValidationException(String message, String testID, Embryo embryo) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setEmbryo(embryo);
    }

    public ValidationException(String message, String testID, Genotype genotype) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setGenotype(genotype);
    }

    public ValidationException(String message, String testID, ChromosomalAlteration chromosomalAlteration) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setChromosomalAlteration(chromosomalAlteration);
    }

    public ValidationException(String message, String testID, Chromosome chromosome) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setChromosome(chromosome);
    }

    public ValidationException(String message, String testID, ParentalStrain parentalStrain) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setParentalStrain(parentalStrain);
    }

    public ValidationException(String message, String testID, RelatedSpecimen relatedSpecimen) {
        super(message);
        this.getValidation().setTestID(testID);
        this.getValidation().setDatetime(DatatypeConverter.now());
        this.getValidation().setMessage(message);
        this.getValidation().setRelatedSpecimen(relatedSpecimen);
    }
}
