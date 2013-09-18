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

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ValidationReportSet;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.imits.IMITSBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.mgi.MGIBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.statuscodes.StatusCodesBrowser;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.utils.ValidationException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class CentreSpecimenSetValidator extends Validator<CentreSpecimenSet> {

    private static final double MAX_STAGE = 20.0;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CentreSpecimenSetValidator.class);
    private static final String PROJECTS_FILENAME = "projects.txt";
    private static List<String> projects = null;

    static {
        try {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(PROJECTS_FILENAME);
            CentreSpecimenSetValidator.projects = Arrays.asList(propertiesConfiguration.getStringArray("projects"));
        } catch (ConfigurationException ex) {
            logger.error("{} not found", PROJECTS_FILENAME, ex);
        }

        if (CentreSpecimenSetValidator.projects == null || CentreSpecimenSetValidator.projects.size() < 1) {
            logger.error("cannot load projects file");
        }

    }

    public CentreSpecimenSetValidator(CentreSpecimenSet centreset,
            ValidationSet validationSet,
            ValidationReportSet validationReportSet,
            Map<IOParameters.VALIDATIONRESOURCES_IDS, Incarnator<?>> xmlValidationResources) {
        super(centreset, validationSet, validationReportSet, xmlValidationResources);

    }

    @Override
    public void validateWithHandler() {
        for (CentreSpecimen centreSpecimen : this.centreset.getCentre()) {
            for (Specimen specimen : centreSpecimen.getMouseOrEmbryo()) {
                this.testSpecimenBackground(specimen);
                this.testSpecimenDatesWithinLimits(specimen);
                this.testSpecimenStatusCode(specimen);
                this.checkValidProject(specimen);
            }
        }
    }
    
    private void checkValidProject(Specimen specimen) {
        if (projects != null && projects.size() > 0 && !projects.contains(specimen.getProject())) {
            this.errorHandler.warning(new ValidationException("Project " + specimen.getProject() + " does not exist", "CentreSpecimenSetValidator_SpecimenAttributes_ProjectNotFound", specimen));
        }
    }

    
    

    public void testSpecimenStatusCode(Specimen specimen) {
        if (specimen.getStatusCode() != null && !specimen.getStatusCode().getValue().isEmpty()) {
            if (!this.validateParameterStatus(specimen.getStatusCode().getValue())) {
                this.errorHandler.error(new ValidationException("Specimen " + specimen.getSpecimenID() + " has a no valid status code " + specimen.getStatusCode().getValue(), "CentreSpecimenSetValidator_testSpecimenStatusCode", specimen));
            }
        }
    }

    public void testSpecimenBackground(Specimen specimen) {
        logger.info("checking specimenbackground {}", specimen.getSpecimenID());
        if (specimen.isIsBaseline()) {
            if (specimen.getStrainID() == null) {
                this.errorHandler.error(new ValidationException("Specimen " + specimen.getSpecimenID() + " marked as baseline but has no strainID", "CentreSpecimenSetValidator_testSpecimenBackground", specimen));
            }
            if (specimen.getStrainID() != null && !isInMGI(specimen.getStrainID())) {
                this.errorHandler.error(new ValidationException("Specimen " + specimen.getSpecimenID() + " marked as baseline but has no strainID and is not in MGI", "CentreSpecimenSetValidator_testSpecimenBackground", specimen));
            }
            /*            if (specimen.isIsBaseline() && specimen.getColonyID() != null) {
             }*/
        } else {//if a knockout mouse 

            if (specimen.getColonyID() == null) {
                this.errorHandler.error(new ValidationException("The colonyID for specimen " + specimen.getSpecimenID() + "has to be provided ", "CentreSpecimenSetValidator_testSpecimenBackground", specimen));
            }
            if (specimen.getColonyID() != null
                    && !isInPhenotypeAttemptColonyName(specimen.getProductionCentre() != null ? specimen.getProductionCentre() : specimen.getPhenotypingCentre(), specimen.getColonyID())) {
                this.errorHandler.error(new ValidationException("The colonyID " + specimen.getColonyID() + " of specimen " + specimen.getSpecimenID() + " is not in iMITS on any of these statuses Cre Excision Complete, Phenotype Attempt Registered, Phenotyping Started, Phenotyping Completed", "CentreSpecimenSetValidator_testSpecimenBackground", specimen));
            }
        }
    }

    public void testSpecimenDatesWithinLimits(Specimen specimen) {
        if (specimen.getClass().equals(Mouse.class)) {
            Mouse mouse = (Mouse) specimen;
            Calendar dateFromImits = getDateFromImits(specimen.getColonyID());

            if (dateFromImits != null && mouse.getDOB().before(dateFromImits)) {
                this.errorHandler.error(new ValidationException("Mouse " + mouse.getStrainID() + " date of birth is before the date specified in imits", "CentreSpecimenSetValidator_testSpecimenDatesWithinLimits", specimen));
            }
            Calendar now = DatatypeConverter.now();
            if (mouse.getDOB() == null) {
                this.errorHandler.error(new ValidationException("Mouse " + mouse.getSpecimenID() + " date of birth is Null", "CentreSpecimenSetValidator_testSpecimenDatesWithinLimits", specimen));
            }
            if (mouse.getDOB() != null && mouse.getDOB().after(now)) {
                this.errorHandler.error(new ValidationException("Mouse " + mouse.getSpecimenID() + " has a data of birth that is after " + DatatypeConverter.printDateTime(now), "CentreSpecimenSetValidator_testSpecimenDatesWithinLimits", specimen));
            }

        } else {//is embryo
            Embryo embryo = (Embryo) specimen;
            double numericalStage = -1;
            try {
                numericalStage = this.getNumericalStage(embryo.getStage());
            } catch (NumberFormatException ex) {
                this.errorHandler.error(new ValidationException("Embryo " + embryo.getSpecimenID() + " stage " + embryo.getStage() + "cannot be cast to numerical", "CentreSpecimenSetValidator_testSpecimenDatesWithinLimits", specimen));
                return;
            }
            if (numericalStage > MAX_STAGE) {
                this.errorHandler.error(new ValidationException("Embryo " + embryo.getSpecimenID() + " stage " + embryo.getStage() + " is greater than " + Double.toString(MAX_STAGE), "CentreSpecimenSetValidator_testSpecimenDatesWithinLimits", specimen));
            }
        }
    }

    public double getNumericalStage(String stage) throws NumberFormatException {
        return Double.valueOf(stage.substring(1));//removes the first E
    }

    public Calendar getDateFromImits(String colonyID) {
        return null;
    }

    private boolean isInMGI(String strainID) {
        try {
            return ((MGIBrowser) this.xmlValidationResources.get(IOParameters.VALIDATIONRESOURCES_IDS.MGIBrowser)).exists(strainID);
        } catch (JAXBException ex) {
            logger.error("cannot access MGI", ex);
        } catch (FileNotFoundException ex) {
            logger.error("cannot access MGI", ex);
        } catch (Exception ex) {
            logger.error("cannot access MGI", ex);
        }
        return false;

    }

    private boolean isInPhenotypeAttemptColonyName(CentreILARcode centreILARcode, String colonyID) {
        try {
            logger.trace("check if {},{} is on imits", centreILARcode, colonyID);
            return ((IMITSBrowser) this.xmlValidationResources.get(IOParameters.VALIDATIONRESOURCES_IDS.Imits)).isInPhenotypeAttemptColonyName(centreILARcode, colonyID);
        } catch (Exception ex) {
            logger.error("cannot access Imits", ex);
        }
        return false;
    }

    private boolean validateParameterStatus(String value) {
        return ((StatusCodesBrowser) this.xmlValidationResources.get(IOParameters.VALIDATIONRESOURCES_IDS.Statuscodes)).exists(value);
    }
}
