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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.consoleapps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Dimension;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaFile;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSample;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSampleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSection;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.OntologyParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ParameterAssociation;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesMediaParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesMediaParameterValue;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameterValue;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;

public class Commandor implements Command {

    private final ValidationSet validationSet;
    private final Map<Experiment, List<Validation>> validationExceptionsPerExperiment;

    public Commandor(ValidationSet validationSet) {
        this.validationSet = validationSet;
        this.validationExceptionsPerExperiment = new HashMap<>();
    }

    public Map<Experiment, List<Validation>> getValidationExceptionsPerExperiment() {
        return this.validationExceptionsPerExperiment;
    }

    @Override
    public List<Validation> execute(Object parameter) {
        if (parameter.getClass().equals(CentreProcedure.class)) {
            return this.getValidation((CentreProcedure) parameter);
        }
        if (parameter.getClass().equals(Experiment.class)) {
            return this.getValidation((Experiment) parameter);
        }
        if (parameter.getClass().equals(Procedure.class)) {
            return this.getValidation((Procedure) parameter);
        }
        if (parameter.getClass().equals(SimpleParameter.class)) {
            return this.getValidation((SimpleParameter) parameter);
        }
        if (parameter.getClass().equals(OntologyParameter.class)) {
            return this.getValidation((OntologyParameter) parameter);
        }
        if (parameter.getClass().equals(SeriesParameter.class)) {
            return this.getValidation((SeriesParameter) parameter);
        }
        if (parameter.getClass().equals(SeriesParameterValue.class)) {
            return this.getValidation((SeriesParameterValue) parameter);
        }
        if (parameter.getClass().equals(MediaParameter.class)) {
            return this.getValidation((MediaParameter) parameter);
        }
        if (parameter.getClass().equals(MediaSampleParameter.class)) {
            return this.getValidation((MediaSampleParameter) parameter);
        }
        if (parameter.getClass().equals(MediaSample.class)) {
            return this.getValidation((MediaSample) parameter);
        }
        if (parameter.getClass().equals(MediaSection.class)) {
            return this.getValidation((MediaSection) parameter);
        }
        if (parameter.getClass().equals(MediaFile.class)) {
            return this.getValidation((MediaFile) parameter);
        }
        if (parameter.getClass().equals(ParameterAssociation.class)) {
            return this.getValidation((ParameterAssociation) parameter);
        }
        if (parameter.getClass().equals(Dimension.class)) {
            return this.getValidation((Dimension) parameter);
        }
        if (parameter.getClass().equals(SeriesMediaParameter.class)) {
            return this.getValidation((SeriesMediaParameter) parameter);
        }
        if (parameter.getClass().equals(SeriesMediaParameterValue.class)) {
            return this.getValidation((SeriesMediaParameterValue) parameter);
        }
        if (parameter.getClass().equals(ProcedureMetadata.class)) {
            return this.getValidation((ProcedureMetadata) parameter);
        }
        return null;
    }

    public List<Validation> getValidation(CentreProcedure centreProcedure) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetCentreProcedure() && validation.getCentreProcedure() == centreProcedure) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(Experiment experiment) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetExperiment() && validation.getExperiment() == experiment) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(Procedure procedure) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetProcedure() && validation.getProcedure() == procedure) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(SimpleParameter simpleParameter) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetSimpleParameter() && validation.getSimpleParameter() == simpleParameter) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(OntologyParameter ontologyParameter) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetOntologyParameter() && validation.getOntologyParameter() == ontologyParameter) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(SeriesParameter seriesParameter) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetSeriesParameter() && validation.getSeriesParameter() == seriesParameter) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(SeriesParameterValue seriesParameterValue) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetSeriesParameterValue() && validation.getSeriesParameterValue() == seriesParameterValue) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(MediaParameter mediaParameter) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetMediaParameter() && validation.getMediaParameter() == mediaParameter) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(MediaSampleParameter mediaSampleParameter) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetMediaSampleParameter() && validation.getMediaSampleParameter() == mediaSampleParameter) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(MediaSample mediaSample) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetMediaSample() && validation.getMediaSample() == mediaSample) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(MediaSection mediaSection) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetMediaSection() && validation.getMediaSection() == mediaSection) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(MediaFile mediaFile) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetMediaFile() && validation.getMediaFile() == mediaFile) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(ParameterAssociation parameterAssociation) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetParameterAssociation() && validation.getParameterAssociation() == parameterAssociation) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(Dimension dimension) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetDimension() && validation.getDimension() == dimension) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(SeriesMediaParameter seriesMediaParameter) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetSeriesMediaParameter() && validation.getSeriesMediaParameter() == seriesMediaParameter) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(SeriesMediaParameterValue seriesMediaParameterValue) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetSeriesMediaParameterValue() && validation.getSeriesMediaParameterValue() == seriesMediaParameterValue) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }

    public List<Validation> getValidation(ProcedureMetadata procedureMetadata) {
        List<Validation> foundValidations = new ArrayList<>();
        for (Validation validation : this.validationSet.getValidation()) {
            if (validation.isSetProcedureMetadata() && validation.getProcedureMetadata() == procedureMetadata) {
                foundValidations.add(validation);
            }
        }
        return foundValidations;
    }
}
