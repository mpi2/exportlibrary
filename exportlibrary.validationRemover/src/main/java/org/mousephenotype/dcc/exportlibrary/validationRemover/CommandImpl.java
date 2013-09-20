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

    public void loadValidationReportSets() {
        if (!this.validations.isEmpty()) {
            List<ValidationReport> validationReports = null;
            ValidationReportSet validationReportSet = null;
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

        }
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
        return this.loadValidations("centreProcedure", centreProcedure);
    }

    @Override
    public boolean run(Experiment experiment) {
        return this.loadValidations("experiment", experiment);
    }

    @Override
    public boolean run(Procedure procedure) {
        return this.loadValidations("procedure", procedure);
    }

    @Override
    public boolean run(SimpleParameter simpleParameter) {
        return this.loadValidations("simpleParameter", simpleParameter);
    }

    @Override
    public boolean run(OntologyParameter ontologyParameter) {
        return this.loadValidations("ontologyParameter", ontologyParameter);
    }

    @Override
    public boolean run(SeriesParameter seriesParameter) {
        return this.loadValidations("seriesParameter", seriesParameter);
    }

    @Override
    public boolean run(SeriesParameterValue seriesParameterValue) {
        return this.loadValidations("seriesParameterValue", seriesParameterValue);
    }

    @Override
    public boolean run(MediaParameter mediaParameter) {
        return this.loadValidations("mediaParameter", mediaParameter);
    }

    @Override
    public boolean run(MediaSampleParameter mediaSampleParameter) {
        return this.loadValidations("mediaSampleParameter", mediaSampleParameter);
    }

    @Override
    public boolean run(MediaSample mediaSample) {
        return this.loadValidations("mediaSample", mediaSample);
    }

    @Override
    public boolean run(MediaSection mediaSection) {
        return this.loadValidations("mediaSection", mediaSection);
    }

    @Override
    public boolean run(MediaFile MediaFile) {
        return this.loadValidations("MediaFile", MediaFile);
    }

    @Override
    public boolean run(ParameterAssociation parameterAssociation) {
        return this.loadValidations("parameterAssociation", parameterAssociation);
    }

    @Override
    public boolean run(Dimension dimension) {
        return this.loadValidations("dimension", dimension);
    }

    @Override
    public boolean run(SeriesMediaParameter seriesMediaParameter) {
        return this.loadValidations("seriesMediaParameter", seriesMediaParameter);
    }

    @Override
    public boolean run(SeriesMediaParameterValue seriesMediaParameterValue) {
        return this.loadValidations("seriesMediaParameterValue", seriesMediaParameterValue);
    }

    @Override
    public boolean run(ProcedureMetadata procedureMetadata) {
        return this.loadValidations("procedureMetadata", procedureMetadata);
    }
    //specimen

    @Override
    public boolean run(CentreSpecimen centreSpecimen) {
        return this.loadValidations("centreSpecimen", centreSpecimen);
    }

    @Override
    public boolean run(Specimen specimen) {
        return this.loadValidations("specimen", specimen);
    }

    @Override
    public boolean run(Mouse mouse) {
        //return this.loadValidations("mouse", mouse);
        return this.loadValidations("specimen", mouse);

    }

    @Override
    public boolean run(Embryo embryo) {
        //return this.loadValidations("embryo", embryo);
        return this.loadValidations("specimen", embryo);
    }
}
