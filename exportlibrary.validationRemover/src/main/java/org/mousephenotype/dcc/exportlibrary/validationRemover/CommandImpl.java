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
package org.mousephenotype.dcc.exportlibrary.validationRemover;

import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Embryo;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Mouse;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReport;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class CommandImpl extends org.mousephenotype.dcc.exportlibrary.datastructure.traverser.CommandImpl {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommandImpl.class);
    private Set<ValidationSet> validationSets = new HashSet<>();
    private Set<ValidationReportSet> validationReportSets = new HashSet<>();
    private List<Validation> validations = new ArrayList<>();
    private List<ValidationReport> validationReportsForValidEntities = new ArrayList<>();
    private final HibernateManager hibernateManager;

    public CommandImpl(HibernateManager hibernateManager) {

        this.hibernateManager = hibernateManager;
    }

    private boolean loadValidations(String containerAttribute, Serializable parameter) {
        StringBuilder sb = new StringBuilder("from Validation validation inner join fetch validation.");
        sb.append(containerAttribute);//centreProcedure            
        sb.append(" contained where contained = :contained");//centreProcedure
        ImmutableMap<String, Object> build = ImmutableMap.<String, Object>builder().put("contained", parameter).build();
        List<Validation> results = this.hibernateManager.query(sb.toString(), build, Validation.class);
        if (results != null && !results.isEmpty()) {
            logger.trace("validation exceptions found for {}", containerAttribute);
            this.validations.addAll(results);
            return true;
        } else {
            logger.trace("validation exceptions not found for {}", containerAttribute);
        }
        return false;
    }

    private ValidationSet loadValidationSets(Validation validation) {
        return this.hibernateManager.getContainer(validation, ValidationSet.class, "validation");
    }

    public boolean loadValidationSets() {
        if (this.validations.isEmpty()) {
            return false;
        }
        for (Validation validation : this.validations) {
            this.validationSets.add(this.loadValidationSets(validation));
        }
        return true;
    }

    public void removeValidationSets() {
        if (!this.validationSets.isEmpty()) {
            for (ValidationSet validationSet : this.validationSets) {
                this.hibernateManager.remove(ValidationSet.class, validationSet.getHjid());
                logger.trace("removing validationset {}", validationSet.getHjid());
            }
        }
    }

    private List<ValidationReport> loadValidationReports(Validation validation) {

        StringBuilder lexicalQuery = new StringBuilder(" from ");

        lexicalQuery.append("ValidationReport");
        lexicalQuery.append(" continent ");
        lexicalQuery.append(" inner join fetch ");
        lexicalQuery.append("continent.");
        lexicalQuery.append("validation");
        lexicalQuery.append(" ");
        lexicalQuery.append("validation");
        lexicalQuery.append(" where ");
        lexicalQuery.append("validation");
        lexicalQuery.append(" = :continent");
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("continent", validation);

        return this.hibernateManager.query(lexicalQuery.toString(), parameters, ValidationReport.class);


    }

    public boolean loadValidationReportSets() {
        List<ValidationReport> validationReports = null;
        ValidationReportSet validationReportSet = null;
        boolean found = false;
        if (!this.validations.isEmpty()) {
            found = true;
            for (Validation validation : this.validations) {
                validationReports = this.loadValidationReports(validation);
                if (validationReports != null) {
                    for (ValidationReport validationReport : validationReports) {
                        validationReportSet = this.hibernateManager.getContainer(validationReport, ValidationReportSet.class, "validationReport");
                        if (validationReportSet != null) {
                            this.validationReportSets.add(validationReportSet);
                        }
                    }
                }
                validationReports = null;
                validationReportSet = null;
            }

        } else {
            if (!validationReportsForValidEntities.isEmpty()) {
                found = true;
                for (ValidationReport validationReport : this.validationReportsForValidEntities) {
                    validationReportSet = this.hibernateManager.getContainer(validationReport, ValidationReportSet.class, "validationReport");
                    if (validationReportSet != null) {
                        this.validationReportSets.add(validationReportSet);
                    }
                    validationReportSet = null;
                }
            }
        }
        return found;
    }

    private boolean loadValidationReports(String containerAttribute, Serializable parameter) {
        StringBuilder sb = new StringBuilder("from ValidationReport validationReporter inner join fetch validationReporter.");
        sb.append(containerAttribute);//centreProcedure            
        sb.append(" contained where contained = :contained");//centreProcedure
        ImmutableMap<String, Object> build = ImmutableMap.<String, Object>builder().put("contained", parameter).build();
        List<ValidationReport> results = this.hibernateManager.query(sb.toString(), build, ValidationReport.class);
        if (results != null && !results.isEmpty()) {
            logger.trace("validationReports  found for {}", containerAttribute);
            this.validationReportsForValidEntities.addAll(results);
            return true;
        } else {
            logger.trace("validationReports not found for {}", containerAttribute);
        }
        return false;
    }

    public void removeValidationReportSets() {
        if (!this.validationReportSets.isEmpty()) {
            for (ValidationReportSet validationReportSet : this.validationReportSets) {
                this.hibernateManager.remove(ValidationReportSet.class, validationReportSet.getHjid());
                logger.trace("removing validationset {}", validationReportSet.getHjid());
            }
        }
    }

    @Override
    public boolean run(CentreProcedure centreProcedure) {
        boolean result = this.loadValidations("centreProcedure", centreProcedure);
        result = result || this.loadValidationReports("centreProcedure", centreProcedure);
        return result;
    }

    @Override
    public boolean run(Experiment experiment) {
        boolean result = this.loadValidations("experiment", experiment);
        result = result || this.loadValidationReports("experiment", experiment);
        return result;
    }

    @Override
    public boolean run(Procedure procedure) {
        boolean result = this.loadValidations("procedure", procedure);
        result = result || this.loadValidationReports("procedure", procedure);
        return result;
    }

    @Override
    public boolean run(SimpleParameter simpleParameter) {
        boolean result = this.loadValidations("simpleParameter", simpleParameter);
        result = result || this.loadValidationReports("simpleParameter", simpleParameter);
        return result;
    }

    @Override
    public boolean run(OntologyParameter ontologyParameter) {
        boolean result = this.loadValidations("ontologyParameter", ontologyParameter);
        result = result || this.loadValidationReports("ontologyParameter", ontologyParameter);
        return result;
    }

    @Override
    public boolean run(SeriesParameter seriesParameter) {
        boolean result = this.loadValidations("seriesParameter", seriesParameter);
        result = result || this.loadValidationReports("seriesParameter", seriesParameter);
        return result;
    }

    @Override
    public boolean run(SeriesParameterValue seriesParameterValue) {
        boolean result = this.loadValidations("seriesParameterValue", seriesParameterValue);
        result = result || this.loadValidationReports("seriesParameterValue", seriesParameterValue);
        return result;
    }

    @Override
    public boolean run(MediaParameter mediaParameter) {
        boolean result = this.loadValidations("mediaParameter", mediaParameter);
        result = result || this.loadValidationReports("mediaParameter", mediaParameter);
        return result;
    }

    @Override
    public boolean run(MediaSampleParameter mediaSampleParameter) {
        boolean result = this.loadValidations("mediaSampleParameter", mediaSampleParameter);
        result = result || this.loadValidationReports("mediaSampleParameter", mediaSampleParameter);
        return result;
    }

    @Override
    public boolean run(MediaSample mediaSample) {
        boolean result = this.loadValidations("mediaSample", mediaSample);
        result = result || this.loadValidationReports("mediaSample", mediaSample);
        return result;
    }

    @Override
    public boolean run(MediaSection mediaSection) {
        boolean result = this.loadValidations("mediaSection", mediaSection);
        result = result || this.loadValidationReports("mediaSection", mediaSection);
        return result;
    }

    @Override
    public boolean run(MediaFile MediaFile) {
        boolean result = this.loadValidations("MediaFile", MediaFile);
        result = result || this.loadValidationReports("MediaFile", MediaFile);
        return result;
    }

    @Override
    public boolean run(ParameterAssociation parameterAssociation) {
        boolean result = this.loadValidations("parameterAssociation", parameterAssociation);
        result = result || this.loadValidationReports("parameterAssociation", parameterAssociation);
        return result;
    }

    @Override
    public boolean run(Dimension dimension) {
        boolean result = this.loadValidations("dimension", dimension);
        result = result || this.loadValidationReports("dimension", dimension);
        return result;
    }

    @Override
    public boolean run(SeriesMediaParameter seriesMediaParameter) {
        boolean result = this.loadValidations("seriesMediaParameter", seriesMediaParameter);
        result = result || this.loadValidationReports("seriesMediaParameter", seriesMediaParameter);
        return result;
    }

    @Override
    public boolean run(SeriesMediaParameterValue seriesMediaParameterValue) {
        boolean result = this.loadValidations("seriesMediaParameterValue", seriesMediaParameterValue);
        result = result || this.loadValidationReports("seriesMediaParameterValue", seriesMediaParameterValue);
        return result;
    }

    @Override
    public boolean run(ProcedureMetadata procedureMetadata) {
        boolean result = this.loadValidations("procedureMetadata", procedureMetadata);
        result = result || this.loadValidationReports("procedureMetadata", procedureMetadata);
        return result;
    }
    //specimen

    @Override
    public boolean run(CentreSpecimen centreSpecimen) {
        boolean result = this.loadValidations("centreSpecimen", centreSpecimen);
        result = result || this.loadValidationReports("centreSpecimen", centreSpecimen);
        return result;
    }

    @Override
    public boolean run(Specimen specimen) {
        boolean result = this.loadValidations("specimen", specimen);
        result = result || this.loadValidationReports("specimen", specimen);
        return result;
    }

    @Override
    public boolean run(Mouse mouse) {
        //return this.loadValidations("mouse", mouse);
        boolean result = this.loadValidations("specimen", mouse);
        result = result || this.loadValidationReports("specimen", mouse);
        return result;

    }

    @Override
    public boolean run(Embryo embryo) {
        //return this.loadValidations("embryo", embryo);
        boolean result = this.loadValidations("specimen", embryo);
        result = result || this.loadValidationReports("specimen", embryo);
        return result;
    }
}
