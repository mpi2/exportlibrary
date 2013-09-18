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
package org.mousephenotype.dcc.exportlibrary.datastructure.traverser;

import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Dimension;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Housing;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Line;
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

public class CentreProcedureSetTraverser {

    private void traverseProcedure(Command command, Procedure procedure) {
        command.execute(procedure);
        if (procedure.isSetMediaParameter()) {
            for (MediaParameter mediaParameter : procedure.getMediaParameter()) {
                command.execute(mediaParameter);
                if (mediaParameter.isSetParameterAssociation()) {
                    for (ParameterAssociation parameterAssociation : mediaParameter.getParameterAssociation()) {
                        command.execute(mediaParameter);
                        for (Dimension dimension : parameterAssociation.getDim()) {
                            command.execute(dimension);
                        }
                    }
                }
                if (mediaParameter.isSetProcedureMetadata()) {
                    for (ProcedureMetadata procedureMetadata : mediaParameter.getProcedureMetadata()) {
                        command.execute(procedureMetadata);
                    }
                }
                if (mediaParameter.isSetParameterStatus()) {
                    command.execute(mediaParameter.getParameterStatus());
                }
            }
        }

        if (procedure.isSetMediaSampleParameter()) {
            for (MediaSampleParameter mediaSampleParameter : procedure.getMediaSampleParameter()) {
                command.execute(mediaSampleParameter);
                for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
                    command.execute(mediaSample);
                    for (MediaSection mediaSection : mediaSample.getMediaSection()) {
                        command.execute(mediaSection);
                        for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                            command.execute(mediaFile);
                            if (mediaFile.isSetProcedureMetadata()) {
                                for (ProcedureMetadata procedureMetadata : mediaFile.getProcedureMetadata()) {
                                    command.execute(procedureMetadata);
                                }
                            }
                            for (ParameterAssociation parameterAssociation : mediaFile.getParameterAssociation()) {
                                command.execute(parameterAssociation);
                                for (Dimension dimension : parameterAssociation.getDim()) {
                                    command.execute(dimension);
                                }
                            }
                        }
                    }
                }
                if (mediaSampleParameter.isSetParameterStatus()) {
                    command.execute(mediaSampleParameter.getParameterStatus());
                }
            }
        }
        if (procedure.isSetOntologyParameter()) {
            for (OntologyParameter ontologyParameter : procedure.getOntologyParameter()) {
                command.execute(ontologyParameter);
                if (ontologyParameter.isSetParameterStatus()) {
                    command.execute(ontologyParameter.getParameterStatus());
                }
            }

        }
        if (procedure.isSetProcedureMetadata()) {
            for (ProcedureMetadata procedureMetadata : procedure.getProcedureMetadata()) {
                command.execute(procedureMetadata);
                if (procedureMetadata.isSetParameterStatus()) {
                    command.execute(procedureMetadata.getParameterStatus());
                }
            }
        }
        if (procedure.isSetSeriesMediaParameter()) {
            for (SeriesMediaParameter seriesMediaParameter : procedure.getSeriesMediaParameter()) {
                command.execute(seriesMediaParameter);
                if (seriesMediaParameter.isSetProcedureMetadata()) {
                    for (ProcedureMetadata procedureMetadata : seriesMediaParameter.getProcedureMetadata()) {
                        command.execute(procedureMetadata);
                        if (procedureMetadata.isSetParameterStatus()) {
                            command.execute(procedureMetadata.getParameterStatus());
                        }
                    }
                }
                for (SeriesMediaParameterValue seriesMediaParameterValue : seriesMediaParameter.getValue()) {
                    command.execute(seriesMediaParameterValue);
                    if (seriesMediaParameterValue.isSetProcedureMetadata()) {
                        for (ProcedureMetadata procedureMetadata : seriesMediaParameterValue.getProcedureMetadata()) {
                            command.execute(procedureMetadata);
                        }
                    }
                }
                if (seriesMediaParameter.isSetParameterStatus()) {
                    command.execute(seriesMediaParameter.getParameterStatus());
                }
            }
        }
        if (procedure.isSetSimpleParameter()) {
            for (SimpleParameter simpleParameter : procedure.getSimpleParameter()) {
                command.execute(simpleParameter);
                if (simpleParameter.isSetParameterStatus()) {
                    command.execute(simpleParameter.getParameterStatus());
                }
            }
        }

        if (procedure.isSetSeriesParameter()) {
            for (SeriesParameter seriesParameter : procedure.getSeriesParameter()) {
                command.execute(seriesParameter);
                for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                    command.execute(seriesParameterValue);
                }
                if (seriesParameter.isSetParameterStatus()) {
                    command.execute(seriesParameter.getParameterStatus());
                }
            }
        }
    }

    private void traverseProcedureAndBreak(Command command, Procedure procedure) {
        if (command.executeAndReturn(procedure)) {
            return;
        }
        if (procedure.isSetMediaParameter()) {
            for (MediaParameter mediaParameter : procedure.getMediaParameter()) {
                if (command.executeAndReturn(mediaParameter)) {
                    return;
                }
                if (mediaParameter.isSetParameterAssociation()) {
                    for (ParameterAssociation parameterAssociation : mediaParameter.getParameterAssociation()) {
                        if (command.executeAndReturn(mediaParameter)) {
                            return;
                        }
                        for (Dimension dimension : parameterAssociation.getDim()) {
                            if (command.executeAndReturn(dimension)) {
                                return;
                            }
                        }
                    }
                }
                if (mediaParameter.isSetProcedureMetadata()) {
                    for (ProcedureMetadata procedureMetadata : mediaParameter.getProcedureMetadata()) {
                        if (command.executeAndReturn(procedureMetadata)) {
                            return;
                        }
                    }
                }
                if (mediaParameter.isSetParameterStatus()) {
                    if (command.executeAndReturn(mediaParameter.getParameterStatus())) {
                        return;
                    }
                }
            }
        }

        if (procedure.isSetMediaSampleParameter()) {
            for (MediaSampleParameter mediaSampleParameter : procedure.getMediaSampleParameter()) {
                if (command.executeAndReturn(mediaSampleParameter)) {
                    return;
                }
                for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
                    if(command.executeAndReturn(mediaSample)){
                        return;
                    }
                    for (MediaSection mediaSection : mediaSample.getMediaSection()) {
                        if(command.executeAndReturn(mediaSection)){
                            return;
                        }
                        for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                            if(command.executeAndReturn(mediaFile)){
                                return;
                            }
                            if (mediaFile.isSetProcedureMetadata()) {
                                for (ProcedureMetadata procedureMetadata : mediaFile.getProcedureMetadata()) {
                                    if(command.executeAndReturn(procedureMetadata)){
                                        return;
                                    }
                                }
                            }
                            for (ParameterAssociation parameterAssociation : mediaFile.getParameterAssociation()) {
                                if(command.executeAndReturn(parameterAssociation)){
                                    return;
                                }
                                for (Dimension dimension : parameterAssociation.getDim()) {
                                    if(command.executeAndReturn(dimension)){
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                if (mediaSampleParameter.isSetParameterStatus()) {
                    if(command.executeAndReturn(mediaSampleParameter.getParameterStatus())){
                        return;
                    }
                }
            }
        }
        if (procedure.isSetOntologyParameter()) {
            for (OntologyParameter ontologyParameter : procedure.getOntologyParameter()) {
                if(command.executeAndReturn(ontologyParameter)){
                    return;
                }
                if (ontologyParameter.isSetParameterStatus()) {
                    if(command.executeAndReturn(ontologyParameter.getParameterStatus())){
                        return;
                    }
                }
            }

        }
        if (procedure.isSetProcedureMetadata()) {
            for (ProcedureMetadata procedureMetadata : procedure.getProcedureMetadata()) {
                if(command.executeAndReturn(procedureMetadata)){
                    return;
                }
                if (procedureMetadata.isSetParameterStatus()) {
                    if(command.executeAndReturn(procedureMetadata.getParameterStatus())){
                        return;
                    }
                }
            }
        }
        if (procedure.isSetSeriesMediaParameter()) {
            for (SeriesMediaParameter seriesMediaParameter : procedure.getSeriesMediaParameter()) {
                if(command.executeAndReturn(seriesMediaParameter)){
                    return;
                }
                if (seriesMediaParameter.isSetProcedureMetadata()) {
                    for (ProcedureMetadata procedureMetadata : seriesMediaParameter.getProcedureMetadata()) {
                        if(command.executeAndReturn(procedureMetadata)){
                            return;
                        }
                        if (procedureMetadata.isSetParameterStatus()) {
                            if(command.executeAndReturn(procedureMetadata.getParameterStatus())){
                                return;
                            }
                        }
                    }
                }
                for (SeriesMediaParameterValue seriesMediaParameterValue : seriesMediaParameter.getValue()) {
                    if(command.executeAndReturn(seriesMediaParameterValue)){
                        return;
                    }
                    if (seriesMediaParameterValue.isSetProcedureMetadata()) {
                        for (ProcedureMetadata procedureMetadata : seriesMediaParameterValue.getProcedureMetadata()) {
                            if(command.executeAndReturn(procedureMetadata)){
                                return;
                            }
                        }
                    }
                }
                if (seriesMediaParameter.isSetParameterStatus()) {
                    if(command.executeAndReturn(seriesMediaParameter.getParameterStatus())){
                        return;
                    }
                }
            }
        }
        if (procedure.isSetSimpleParameter()) {
            for (SimpleParameter simpleParameter : procedure.getSimpleParameter()) {
                if(command.executeAndReturn(simpleParameter)){
                    return;
                }
                if (simpleParameter.isSetParameterStatus()) {
                    if(command.executeAndReturn(simpleParameter.getParameterStatus())){
                        return;
                    }
                }
            }
        }

        if (procedure.isSetSeriesParameter()) {
            for (SeriesParameter seriesParameter : procedure.getSeriesParameter()) {
                if(command.executeAndReturn(seriesParameter)){
                    return;
                }
                for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                    if(command.executeAndReturn(seriesParameterValue)){
                        return;
                    }
                }
                if (seriesParameter.isSetParameterStatus()) {
                    if(command.executeAndReturn(seriesParameter.getParameterStatus())){
                        return;
                    }
                }
            }
        }
    }

    public void runAndBreak(Command command, CentreProcedureSet centreProcedureSet) {
        if (centreProcedureSet.isSetCentre()) {
            for (CentreProcedure centreProcedure : centreProcedureSet.getCentre()) {
                if (command.executeAndReturn(centreProcedure)) {
                    return;
                }
                if (centreProcedure.isSetExperiment()) {
                    for (Experiment experiment : centreProcedure.getExperiment()) {
                        if (command.executeAndReturn(experiment)) {
                            return;
                        }
                        if (experiment.isSetProcedure()) {
                            this.traverseProcedureAndBreak(command, experiment.getProcedure());

                        }
                        if (experiment.isSetStatusCode()) {
                            for (StatusCode statusCode : experiment.getStatusCode()) {
                                if (command.executeAndReturn(statusCode)) {
                                    return;
                                }
                            }
                        }

                    }
                }
                if (centreProcedure.isSetLine()) {
                    for (Line line : centreProcedure.getLine()) {
                        if (command.executeAndReturn(line)) {
                            return;
                        }
                        if (line.isSetProcedure()) {
                            this.traverseProcedureAndBreak(command, line.getProcedure());
                        }
                        if (line.isSetStatusCode()) {
                            for (StatusCode statusCode : line.getStatusCode()) {
                                if (command.executeAndReturn(statusCode)) {
                                    return;
                                }
                            }
                        }

                    }
                }
                if (centreProcedure.isSetHousing()) {
                    for (Housing housing : centreProcedure.getHousing()) {
                        if (command.executeAndReturn(command)) {
                            return;
                        }
                        if (housing.isSetProcedure()) {
                            this.traverseProcedureAndBreak(command, housing.getProcedure());
                        }
                    }
                }
            }

        }
    }

    public void run(Command command, CentreProcedureSet centreProcedureSet) {
        if (centreProcedureSet.isSetCentre()) {
            for (CentreProcedure centreProcedure : centreProcedureSet.getCentre()) {
                command.execute(centreProcedure);
                if (centreProcedure.isSetExperiment()) {
                    for (Experiment experiment : centreProcedure.getExperiment()) {
                        command.execute(experiment);
                        if (experiment.isSetProcedure()) {
                            this.traverseProcedure(command, experiment.getProcedure());

                        }
                        if (experiment.isSetStatusCode()) {
                            for (StatusCode statusCode : experiment.getStatusCode()) {
                                command.execute(statusCode);
                            }
                        }

                    }
                }
                if (centreProcedure.isSetLine()) {
                    for (Line line : centreProcedure.getLine()) {
                        command.execute(line);
                        if (line.isSetProcedure()) {
                            this.traverseProcedure(command, line.getProcedure());
                        }
                        if (line.isSetStatusCode()) {
                            for (StatusCode statusCode : line.getStatusCode()) {
                                command.execute(statusCode);
                            }
                        }

                    }
                }
                if (centreProcedure.isSetHousing()) {
                    for (Housing housing : centreProcedure.getHousing()) {
                        command.execute(command);
                        if (housing.isSetProcedure()) {
                            this.traverseProcedure(command, housing.getProcedure());
                        }
                    }
                }
            }

        }
    }
}
