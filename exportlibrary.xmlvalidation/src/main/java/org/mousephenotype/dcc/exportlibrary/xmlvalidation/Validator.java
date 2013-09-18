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

import java.util.List;
import java.util.Map;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReport;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.utils.CumulativeErrorHandler;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.utils.ValidationException;

/**
 *
 * @author julian
 */
public abstract class  Validator<T> {

    public final T centreset;
    //
    public final CumulativeErrorHandler errorHandler;
    //
    public ValidationSet validationSet;
    //
    public ValidationReportSet validationReportSet;
    //
    public Map<IOParameters.VALIDATIONRESOURCES_IDS, Incarnator<?>> xmlValidationResources;

    public Validator(T centreset, ValidationSet validationSet, ValidationReportSet validationReportSet, Map<IOParameters.VALIDATIONRESOURCES_IDS, Incarnator<?>> xmlValidationResources) {
        this.centreset = centreset;
        //
        this.errorHandler = new CumulativeErrorHandler();
        //
        this.validationSet = validationSet;
        //
        this.validationReportSet = validationReportSet;
        //
        this.xmlValidationResources = xmlValidationResources;
    }
    
    public abstract void validateWithHandler();

    public List<ValidationException> getWarningExceptions() {
        return this.errorHandler.getWarningExceptions();
    }

    public List<ValidationException> getErrorExceptions() {
        return this.errorHandler.getErrorExceptions();
    }

    public List<ValidationException> getFatalExceptions() {
        return this.errorHandler.getFatalExceptions();
    }

    public boolean exceptionsFound() {
        return errorHandler.exceptionsFound();
    }

    public void compileValidationSet() {
        for (Validation validation : this.errorHandler.getWarningValidations()) {
            if (!this.validationSet.getValidation().contains(validation)) {
                
                this.validationSet.getValidation().add(validation);
            }
        }
        for (Validation validation : this.errorHandler.getErrorValidations()) {
            if (!this.validationSet.getValidation().contains(validation)) {
                this.validationSet.getValidation().add(validation);
            }
        }
        for (Validation validation : this.errorHandler.getFatalValidations()) {
            if (!this.validationSet.getValidation().contains(validation)) {
                this.validationSet.getValidation().add(validation);
            }
        }
    }

    public void addValidationReport(ValidationReport validationReport) {
        this.validationReportSet.getValidationReport().add(validationReport);
    }
}
