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

import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;

/**
 *
 * @author julian
 */
public class EntityController {

    private ValidationSet validationSet;
    private ValidationReportSet validationReportSet;
    //
    private CentreProcedureSet centreProcedureSet;
    private CentreSpecimenSet centreSpecimenSet;

    public EntityController() {
        this.validationReportSet = new ValidationReportSet();
        this.validationSet = new ValidationSet();
    }

    /**
     * @return the validationSet
     */
    public ValidationSet getValidationSet() {
        return validationSet;
    }

    /**
     * @param validationSet the validationSet to set
     */
    public void setValidationSet(ValidationSet validationSet) {
        this.validationSet = validationSet;
    }

    /**
     * @return the validationReportSet
     */
    public ValidationReportSet getValidationReportSet() {
        return validationReportSet;
    }

    /**
     * @param validationReportSet the validationReportSet to set
     */
    public void setValidationReportSet(ValidationReportSet validationReportSet) {
        this.validationReportSet = validationReportSet;
    }

    /**
     * @return the centreProcedureSet
     */
    public CentreProcedureSet getCentreProcedureSet() {
        return centreProcedureSet;
    }

    /**
     * @param centreProcedureSet the centreProcedureSet to set
     */
    public void setCentreProcedureSet(CentreProcedureSet centreProcedureSet) {
        this.centreProcedureSet = centreProcedureSet;
    }

    /**
     * @return the centreSpecimenSet
     */
    public CentreSpecimenSet getCentreSpecimenSet() {
        return centreSpecimenSet;
    }

    /**
     * @param centreSpecimenSet the centreSpecimenSet to set
     */
    public void setCentreSpecimenSet(CentreSpecimenSet centreSpecimenSet) {
        this.centreSpecimenSet = centreSpecimenSet;
    }
}
