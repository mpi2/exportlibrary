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
package org.mousephenotype.dcc.exportlibrary.traverser;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
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
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.traverser.Command;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;


/* An experiment fails if 
 * its parent structure -CentreProcedure fails-
 * any of its children -specimen, procedure, parameters- fails
 */
public class CommandImpl implements Command {

    //list of every validation for a Centre
    private ValidationSet validationSet;
    //every validation for each experiment in a centreProcedure.
    private final Table<CentreProcedure, Experiment, List<Validation>> experimentValidations;
    private final Table<CentreProcedure, Line, List<Validation>> lineValidations;
    private final Table<CentreProcedure, Housing, List<Validation>> housingValidations;
    private final Multimap<CentreProcedure, Experiment> validExperiments;
    private final Multimap<CentreProcedure, Line> validLines;
    private final Multimap<CentreProcedure, Housing> validHousings;
    //every validation in a centreProcedure
    private final Multimap<CentreProcedure, Validation> centreProcedureValidations;
    private final List<CentreProcedure> validCentreProcedures;
    //
    private final HibernateManager hibernateManager;
    private Experiment currentExperiment;
    private Line currentLine;
    private Housing currentHousing;
    private CentreProcedure currentCentreProcedure;

    public CommandImpl(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
        this.validationSet = new ValidationSet();
        this.experimentValidations = HashBasedTable.create();
        this.lineValidations = HashBasedTable.create();
        this.housingValidations = HashBasedTable.create();
        this.validExperiments = ArrayListMultimap.create();
        this.validLines = ArrayListMultimap.create();
        this.validHousings = ArrayListMultimap.create();
        this.centreProcedureValidations = ArrayListMultimap.create();
        this.validCentreProcedures = new ArrayList<>();
    }

    public ValidationSet getValidationSet() {
        return this.validationSet;
    }
    private boolean experimentValidationClean = false;

    //mark every experiment under centreProcedure as wrong
    private void cleanExperimentValidations() {
        for (CentreProcedure centreProcedure : this.centreProcedureValidations.keySet()) {
            if (!this.experimentValidations.containsRow(centreProcedure)) {
                for (Experiment experiment : centreProcedure.getExperiment()) {
                    this.experimentValidations.put(centreProcedure, experiment, (List) this.centreProcedureValidations.get(centreProcedure));
                }
            }
        }
        experimentValidationClean = true;
    }

    public Table<CentreProcedure, Experiment, List<Validation>> getExperimentValidations() {
        if (!experimentValidationClean) {
            this.cleanExperimentValidations();
        }
        return experimentValidations;
    }
    private boolean lineValidationClean = false;

    //mark every experiment under centreProcedure as wrong
    private void cleanLineValidations() {
        for (CentreProcedure centreProcedure : this.centreProcedureValidations.keySet()) {
            if (!this.lineValidations.containsRow(centreProcedure)) {
                for (Line line : centreProcedure.getLine()) {
                    this.lineValidations.put(centreProcedure, line, (List) this.centreProcedureValidations.get(centreProcedure));
                }
            }
        }
        lineValidationClean = true;
    }

    public Table<CentreProcedure, Line, List<Validation>> getLineValidations() {
        if (!lineValidationClean) {
            this.cleanLineValidations();
        }
        return lineValidations;
    }
    private boolean housingValidationClean = false;

    //mark every experiment under centreProcedure as wrong
    private void cleanHousingValidations() {
        for (CentreProcedure centreProcedure : this.centreProcedureValidations.keySet()) {
            if (!this.housingValidations.containsRow(centreProcedure)) {
                for (Housing housing : centreProcedure.getHousing()) {
                    this.housingValidations.put(centreProcedure, housing, (List) this.centreProcedureValidations.get(centreProcedure));
                }
            }
        }
        housingValidationClean = true;
    }

    public Table<CentreProcedure, Housing, List<Validation>> getHousingValidations() {
        if (!housingValidationClean) {
            this.cleanHousingValidations();
        }
        return housingValidations;
    }

    public Multimap<CentreProcedure, Validation> getCentreProcedureValidations() {
        return centreProcedureValidations;
    }
    //removes every valid experiment if the centreProcedure has issues
    private boolean experimentsClean = false;

    private void cleanExperiments() {
        Set<CentreProcedure> temp = new HashSet<>(this.validExperiments.keySet().size());
        for (CentreProcedure centreProcedure : this.validExperiments.keySet()) {
            temp.add(centreProcedure);
        }
        for (CentreProcedure centreProcedure : temp) {
            if (this.centreProcedureValidations.containsKey(centreProcedure)) {
                this.validExperiments.removeAll(centreProcedure);
            }
        }
        experimentsClean = true;
    }

    public Multimap<CentreProcedure, Experiment> getValidExperiments() {
        if (!experimentsClean) {
            this.cleanExperiments();
        }
        return validExperiments;
    }

    private void cleanHousings() {
        Set<CentreProcedure> temp = new HashSet<>(this.validHousings.keySet().size());
        for (CentreProcedure centreProcedure : this.validHousings.keySet()) {
            temp.add(centreProcedure);
        }
        for (CentreProcedure centreProcedure : temp) {
            if (this.centreProcedureValidations.containsKey(centreProcedure)) {
                this.validHousings.removeAll(centreProcedure);
            }
        }
        housingsClean = true;
    }
    private boolean housingsClean = false;

    public Multimap<CentreProcedure, Housing> getValidHousings() {
        if (!housingsClean) {
            this.cleanHousings();
        }
        return validHousings;
    }
    private boolean linesClean = false;

    public Multimap<CentreProcedure, Line> getValidLines() {
        if (!linesClean) {
            this.cleanLines();
        }
        return validLines;
    }

    private void cleanLines() {
        Set<CentreProcedure> temp = new HashSet<>(this.validLines.keySet().size());
        for (CentreProcedure centreProcedure : this.validLines.keySet()) {
            temp.add(centreProcedure);
        }
        for (CentreProcedure centreProcedure : temp) {
            if (this.centreProcedureValidations.containsKey(centreProcedure)) {
                this.validLines.removeAll(centreProcedure);
            }
        }
        linesClean = true;
    }

    public List<CentreProcedure> getValidCentreProcedures() {
        return validCentreProcedures;
    }

    public void getValidation(Object parameter, String containerAttribute) throws HibernateException {
        StringBuilder sb = new StringBuilder("from Validation validation inner join fetch validation.");
        sb.append(containerAttribute);//centreProcedure            
        sb.append(" contained where contained = :contained");//centreProcedure
        ImmutableMap<String, Object> build = ImmutableMap.<String, Object>builder().put("contained", parameter).build();
        List<Validation> results = this.hibernateManager.query(sb.toString(), build, Validation.class);
        if (results != null && !results.isEmpty()) {//if there are validation issues
            //every validation for each experiment submitted by a particular centre
            this.validationSet.getValidation().addAll(results);
            if (parameter.getClass().equals(CentreProcedure.class)) {
                //every validation for a particular centreProcedure and all its children

                this.centreProcedureValidations.putAll((CentreProcedure) parameter, results);

                this.validCentreProcedures.remove((CentreProcedure) parameter);
            } else {
                if (currentExperiment != null) {
                    this.validExperiments.remove(currentCentreProcedure, currentExperiment);
                    this.validCentreProcedures.remove(currentCentreProcedure);
                    //
                    if (!this.experimentValidations.contains(currentCentreProcedure, currentExperiment)) {
                        this.experimentValidations.put(currentCentreProcedure, currentExperiment, results);
                    } else {
                        this.experimentValidations.get(currentCentreProcedure, currentExperiment).addAll(results);//this one does not contain centreProcedureErrors
                    }
                    //every validation for an experiment and its children

                    if (!this.centreProcedureValidations.containsKey(currentCentreProcedure)) {
                        this.centreProcedureValidations.putAll(currentCentreProcedure, results);
                    } else {
                        this.centreProcedureValidations.get(currentCentreProcedure).addAll(results);
                    }
                }
                if (currentLine != null) {
                    this.validLines.remove(currentCentreProcedure, currentLine);
                    this.validCentreProcedures.remove(currentCentreProcedure);
                    if (!this.lineValidations.contains(currentCentreProcedure, currentLine)) {
                        this.lineValidations.put(currentCentreProcedure, currentLine, results);
                    } else {
                        this.lineValidations.get(currentCentreProcedure, currentLine).addAll(results);
                    }
                    if (!this.centreProcedureValidations.containsKey(currentCentreProcedure)) {
                        this.centreProcedureValidations.putAll(currentCentreProcedure, results);
                    } else {
                        this.centreProcedureValidations.get(currentCentreProcedure).addAll(results);
                    }
                }
                if (currentHousing != null) {
                    this.validHousings.remove(currentCentreProcedure, currentHousing);
                    this.validCentreProcedures.remove(currentCentreProcedure);
                    if (!this.housingValidations.contains(currentCentreProcedure, currentHousing)) {
                        this.housingValidations.put(currentCentreProcedure, currentHousing, results);
                    } else {
                        this.housingValidations.get(currentCentreProcedure, currentHousing).addAll(results);
                    }
                    if (!this.centreProcedureValidations.containsKey(currentCentreProcedure)) {
                        this.centreProcedureValidations.putAll(currentCentreProcedure, results);
                    } else {
                        this.centreProcedureValidations.get(currentCentreProcedure).addAll(results);
                    }
                }
            }
        } else {//no errors on centre ProcedureLevel
            if (parameter.getClass().equals(CentreProcedure.class)) {
                this.validCentreProcedures.add((CentreProcedure) parameter);
            }

            if (parameter.getClass().equals(Experiment.class)) {
                this.validExperiments.get(currentCentreProcedure).add((Experiment) parameter);
            }
            if (parameter.getClass().equals(Line.class)) {
                this.validLines.get(currentCentreProcedure).add((Line) parameter);
            }
            if (parameter.getClass().equals(Housing.class)) {
                this.validHousings.get(currentCentreProcedure).add((Housing) parameter);
            }
        }
    }

    @Override
    public void execute(Object parameter) throws HibernateException {
        if (parameter.getClass().equals(CentreProcedure.class)) {
            this.currentCentreProcedure = (CentreProcedure) parameter;
            this.getValidation(parameter, "centreProcedure");
            return;
        }
        if (parameter.getClass().equals(Housing.class)) {
            this.currentExperiment = null;
            this.currentLine = null;
            this.currentHousing = (Housing) parameter;
            this.getValidation(parameter, "housing");
            return;
        }

        if (parameter.getClass().equals(Line.class)) {
            this.currentExperiment = null;
            this.currentHousing = null;
            this.currentLine = (Line) parameter;
            this.getValidation(parameter, "line");
            return;
        }


        if (parameter.getClass().equals(Experiment.class)) {
            this.currentLine = null;
            this.currentHousing = null;
            this.currentExperiment = (Experiment) parameter;
            this.getValidation(parameter, "experiment");
            return;
        }
        if (parameter.getClass().equals(Procedure.class)) {
            this.getValidation(parameter, "procedure");
            return;
        }
        if (parameter.getClass().equals(SimpleParameter.class)) {
            this.getValidation(parameter, "simpleParameter");
            return;
        }
        if (parameter.getClass().equals(OntologyParameter.class)) {
            this.getValidation(parameter, "ontologyParameter");
            return;
        }
        if (parameter.getClass().equals(SeriesParameter.class)) {
            this.getValidation(parameter, "seriesParameter");
            return;
        }
        if (parameter.getClass().equals(SeriesParameterValue.class)) {
            this.getValidation(parameter, "seriesParameterValue");
            return;
        }
        if (parameter.getClass().equals(MediaParameter.class)) {
            this.getValidation(parameter, "mediaParameter");
            return;
        }
        if (parameter.getClass().equals(MediaSampleParameter.class)) {
            this.getValidation(parameter, "mediaSampleParameter");
            return;
        }
        if (parameter.getClass().equals(MediaSample.class)) {
            this.getValidation(parameter, "mediaSample");
            return;
        }
        if (parameter.getClass().equals(MediaSection.class)) {
            this.getValidation(parameter, "mediaSection");
            return;
        }
        if (parameter.getClass().equals(MediaFile.class)) {
            this.getValidation(parameter, "mediaFile");
            return;
        }
        if (parameter.getClass().equals(ParameterAssociation.class)) {
            this.getValidation(parameter, "parameterAssociation");
            return;
        }
        if (parameter.getClass().equals(Dimension.class)) {
            this.getValidation(parameter, "dimension");
            return;
        }
        if (parameter.getClass().equals(SeriesMediaParameter.class)) {
            this.getValidation(parameter, "seriesMediaParameter");
            return;
        }
        if (parameter.getClass().equals(SeriesMediaParameterValue.class)) {
            this.getValidation(parameter, "seriesMediaParameterValue");
            return;
        }
        if (parameter.getClass().equals(ProcedureMetadata.class)) {
            this.getValidation(parameter, "procedureMetadata");
        }

    }

    @Override
    public boolean executeAndReturn(Object parameter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
