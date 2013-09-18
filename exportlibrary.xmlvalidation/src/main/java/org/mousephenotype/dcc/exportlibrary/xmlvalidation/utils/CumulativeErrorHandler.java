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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ExceptionLevel;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class CumulativeErrorHandler implements ErrorHandler {

    protected static final Logger logger = LoggerFactory.getLogger(CumulativeErrorHandler.class);
    private final List<ValidationException> warningExceptions = new ArrayList<ValidationException>();
    private final List<ValidationException> errorExceptions = new ArrayList<ValidationException>();
    private final List<ValidationException> fatalExceptions = new ArrayList<ValidationException>();

    public List<ValidationException> getWarningExceptions() {
        return Collections.unmodifiableList(this.warningExceptions);
    }
    
    public List<Validation> getWarningValidations(){
        List<Validation> warningValidations = new ArrayList<Validation>();
        for(ValidationException ex:this.getWarningExceptions()){
            warningValidations.add(ex.getValidation());
        }
        return warningValidations;
    }
    
    public List<Validation> getErrorValidations(){
        List<Validation> errorValidations = new ArrayList<Validation>();
        for(ValidationException ex:this.getErrorExceptions()){
            errorValidations.add(ex.getValidation());
        }
        return errorValidations;
    }
    
    public List<Validation> getFatalValidations(){
        List<Validation> fatalValidations = new ArrayList<Validation>();
        for(ValidationException ex:this.getFatalExceptions()){
            fatalValidations.add(ex.getValidation());
        }
        return fatalValidations;
    }

    public List<ValidationException> getErrorExceptions() {
        return Collections.unmodifiableList(this.errorExceptions);
    }

    public List<ValidationException> getFatalExceptions() {
        return Collections.unmodifiableList(this.fatalExceptions);
    }

    @Override
    public void warning(ValidationException exception) {
        exception.setLevel(ExceptionLevel.WARN);
        this.warningExceptions.add(exception);
    }

    @Override
    public void error(ValidationException exception) {
        exception.setLevel(ExceptionLevel.ERROR);
        this.errorExceptions.add(exception);
    }

    @Override
    public void fatalError(ValidationException exception) {
        exception.setLevel(ExceptionLevel.FATAL);
        this.fatalExceptions.add(exception);
    }
    
    public boolean exceptionsFound(){
        return !(warningExceptions.isEmpty() &&
                errorExceptions.isEmpty() &&
                fatalExceptions.isEmpty());
    }
}
