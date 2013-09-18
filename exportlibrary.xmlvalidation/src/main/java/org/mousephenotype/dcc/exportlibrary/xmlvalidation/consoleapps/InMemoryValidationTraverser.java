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
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Dimension;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaFile;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSample;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSampleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSection;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.OntologyParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ParameterAssociation;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesMediaParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesMediaParameterValue;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameterValue;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;

public class InMemoryValidationTraverser {

    private final CentreProcedureSet centreProcedureSet;
    private final ValidationSet validationSet;

    public InMemoryValidationTraverser(CentreProcedureSet centreProcedureSet, ValidationSet validationSet) {
        this.centreProcedureSet = centreProcedureSet;
        this.validationSet = validationSet;
    }

    

    

    public Map<Experiment, List<Validation>> run(Command command) {
        Map<Experiment, List<Validation>> validationPerExperiment = new HashMap<Experiment, List<Validation>>();
        
        if (this.centreProcedureSet.getCentre() != null) {
            for (CentreProcedure centreProcedure : this.centreProcedureSet.getCentre()) {
                //not getting validation exceptions for centreProcedure (centreID, pipelineProject)
                if (centreProcedure.getExperiment() != null && centreProcedure.getExperiment().size() > 0) {
                    for (Experiment experiment : centreProcedure.getExperiment()) {
                        validationPerExperiment.put(experiment, new ArrayList<Validation>());
                        validationPerExperiment.get(experiment).addAll(command.execute(experiment));
                        if (experiment.getProcedure() != null) {
                            validationPerExperiment.get(experiment).addAll(command.execute(experiment.getProcedure()));
                            //MEDIAPARAMETER
                            if (experiment.getProcedure().isSetMediaParameter()) {
                                for (MediaParameter mediaParameter : experiment.getProcedure().getMediaParameter()) {
                                    validationPerExperiment.get(experiment).addAll(command.execute(mediaParameter));
                                    if (mediaParameter.isSetParameterAssociation()) {
                                        for (ParameterAssociation parameterAssociation : mediaParameter.getParameterAssociation()) {
                                            validationPerExperiment.get(experiment).addAll(command.execute(mediaParameter));
                                            for (Dimension dimension : parameterAssociation.getDim()) {
                                                validationPerExperiment.get(experiment).addAll(command.execute(dimension));
                                            }
                                        }
                                    }
                                    if (mediaParameter.isSetProcedureMetadata()) {
                                        for (ProcedureMetadata procedureMetadata : mediaParameter.getProcedureMetadata()) {
                                            validationPerExperiment.get(experiment).addAll(command.execute(procedureMetadata));
                                        }
                                    }
                                }

                            }
                            //MEDIASAMPLEPARAMETER
                            if (experiment.getProcedure().isSetMediaSampleParameter()) {
                                for (MediaSampleParameter mediaSampleParameter : experiment.getProcedure().getMediaSampleParameter()) {
                                    validationPerExperiment.get(experiment).addAll(command.execute(mediaSampleParameter));
                                    for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
                                        validationPerExperiment.get(experiment).addAll(command.execute(mediaSample));
                                        for (MediaSection mediaSection : mediaSample.getMediaSection()) {
                                            validationPerExperiment.get(experiment).addAll(command.execute(mediaSection));
                                            for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                                                validationPerExperiment.get(experiment).addAll(command.execute(mediaFile));
                                                if (mediaFile.isSetProcedureMetadata()) {
                                                    for (ProcedureMetadata procedureMetadata : mediaFile.getProcedureMetadata()) {
                                                        validationPerExperiment.get(experiment).addAll(command.execute(procedureMetadata));
                                                    }
                                                }
                                                for (ParameterAssociation parameterAssociation : mediaFile.getParameterAssociation()) {
                                                    validationPerExperiment.get(experiment).addAll(command.execute(parameterAssociation));
                                                    for (Dimension dimension : parameterAssociation.getDim()) {
                                                        validationPerExperiment.get(experiment).addAll(command.execute(dimension));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //ONTOLOGYPARAMETER
                            if (experiment.getProcedure().isSetOntologyParameter()) {
                                for (OntologyParameter ontologyParameter : experiment.getProcedure().getOntologyParameter()) {
                                    validationPerExperiment.get(experiment).addAll(command.execute(ontologyParameter));
                                }
                            }
                            //PROCEDUREMETADATA
                            if (experiment.getProcedure().isSetProcedureMetadata()) {
                                for (ProcedureMetadata procedureMetadata : experiment.getProcedure().getProcedureMetadata()) {
                                    validationPerExperiment.get(experiment).addAll(command.execute(procedureMetadata));
                                }
                            }
                            //SERIESMEDIAPARAMETER
                            if (experiment.getProcedure().isSetSeriesMediaParameter()) {
                                for (SeriesMediaParameter seriesMediaParameter : experiment.getProcedure().getSeriesMediaParameter()) {
                                    validationPerExperiment.get(experiment).addAll(command.execute(seriesMediaParameter));
                                    if (seriesMediaParameter.isSetProcedureMetadata()) {
                                        for (ProcedureMetadata procedureMetadata : seriesMediaParameter.getProcedureMetadata()) {
                                            validationPerExperiment.get(experiment).addAll(command.execute(procedureMetadata));
                                        }
                                    }
                                    for (SeriesMediaParameterValue seriesMediaParameterValue : seriesMediaParameter.getValue()) {
                                        validationPerExperiment.get(experiment).addAll(command.execute(seriesMediaParameterValue));
                                        if (seriesMediaParameterValue.isSetProcedureMetadata()) {
                                            for (ProcedureMetadata procedureMetadata : seriesMediaParameterValue.getProcedureMetadata()) {
                                                validationPerExperiment.get(experiment).addAll(command.execute(procedureMetadata));
                                            }
                                        }
                                    }
                                }
                            }
                            //SERIESPARAMETER
                            if (experiment.getProcedure().isSetSeriesParameter()) {
                                for (SeriesParameter seriesParameter : experiment.getProcedure().getSeriesParameter()) {
                                    validationPerExperiment.get(experiment).addAll(command.execute(seriesParameter));
                                    for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                                        validationPerExperiment.get(experiment).addAll(command.execute(seriesParameterValue));
                                    }
                                }
                            }
                            //SIMPLEPARAMETER
                            if (experiment.getProcedure().isSetSimpleParameter()) {
                                for (SimpleParameter simpleParameter : experiment.getProcedure().getSimpleParameter()) {
                                    validationPerExperiment.get(experiment).addAll(command.execute(simpleParameter));
                                }
                            }

                           
                        }

                    }
                }
            }

        }
        return validationPerExperiment;
    }
    
    public static void main(String[] args){
        ValidationSet validationSet =  new ValidationSet();
        InMemoryValidationTraverser traverser = new InMemoryValidationTraverser(new CentreProcedureSet(),validationSet);
        Map<Experiment, List<Validation>> validationPerExperiment;
       validationPerExperiment= traverser.run(new Commandor(validationSet));
        
    }
}
