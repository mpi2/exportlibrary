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
import java.util.List;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Embryo;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Mouse;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.ValidationSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.traverser.Command;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

public class SpecimenCommandImpl implements Command {

    //list of every validation for a Centre
    private ValidationSet validationSet;
    private final List<CentreSpecimen> validCentreSpecimens;
    private final Multimap<CentreSpecimen, Specimen> validSpecimens;
    private final Table<CentreSpecimen, Specimen, List<Validation>> specimenValidations;
    private final Multimap<CentreSpecimen, Validation> centreSpecimenValidations;

    public Multimap<CentreSpecimen, Validation> getCentreSpecimenValidations() {
        return centreSpecimenValidations;
    }
    private final HibernateManager hibernateManager;
    private CentreSpecimen currentCentreSpecimen;
    private Specimen currentSpecimen;

    public SpecimenCommandImpl(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
        this.validationSet = new ValidationSet();
        this.validCentreSpecimens = new ArrayList<>();
        this.validSpecimens = ArrayListMultimap.create();
        this.specimenValidations = HashBasedTable.create();
        this.centreSpecimenValidations = ArrayListMultimap.create();
    }

    @Override
    public void execute(Object parameter) throws HibernateException {
        if (parameter.getClass().equals(CentreSpecimen.class)) {
            this.currentCentreSpecimen = (CentreSpecimen) parameter;
            this.getValidation(parameter, "centreSpecimen");
            return;
        }

        if (parameter.getClass().equals(Specimen.class)) {
            this.currentSpecimen = (Specimen) parameter;
            this.getValidation(parameter, "specimen");
            return;
        }

        if (parameter.getClass().equals(Mouse.class)) {
            this.currentSpecimen = (Mouse) parameter;
            this.getValidation(parameter, "specimen");
            return;
        }
        if (parameter.getClass().equals(Embryo.class)) {
            this.currentSpecimen = (Embryo) parameter;
            this.getValidation(parameter, "specimen");
        }
    }

    public ValidationSet getValidationSet() {
        return this.validationSet;
    }
    private boolean specimenValidationClean = false;

    //mark every experiment under centreProcedure as wrong
    private void cleanSpecimenValidations() {
        for (CentreSpecimen centreSpecimen : this.centreSpecimenValidations.keySet()) {
            if (!this.specimenValidations.containsRow(centreSpecimen)) {
                for (Specimen specimen : centreSpecimen.getMouseOrEmbryo()) {
                    this.specimenValidations.put(centreSpecimen, specimen, (List) this.centreSpecimenValidations.get(centreSpecimen));
                }
            }
        }
        specimenValidationClean = true;
    }

    public Table<CentreSpecimen, Specimen, List<Validation>> getSpecimenValidations() {
        if (!specimenValidationClean) {
            this.cleanSpecimenValidations();
        }
        return this.specimenValidations;
    }
    
    public Multimap<CentreSpecimen, Specimen> getValidSpecimens() {
        
        return this.validSpecimens;
    }

    private void getValidation(Object parameter, String containerAttribute) {
        StringBuilder sb = new StringBuilder("from Validation validation inner join fetch validation.");
        sb.append(containerAttribute);//centreSpecimen            
        sb.append(" contained where contained = :contained");//centreProcedure
        ImmutableMap<String, Object> build = ImmutableMap.<String, Object>builder().put("contained", parameter).build();
        List<Validation> results = this.hibernateManager.query(sb.toString(), build, Validation.class);
        if (results != null && !results.isEmpty()) {
            this.validationSet.getValidation().addAll(results);
            if (parameter.getClass().equals(CentreSpecimen.class)) {
                this.validCentreSpecimens.remove((CentreSpecimen) parameter);
                
                    this.centreSpecimenValidations.get(currentCentreSpecimen).addAll(results);
                
            } else {
                this.validSpecimens.remove(currentCentreSpecimen, currentSpecimen);
                if (!this.specimenValidations.contains(currentCentreSpecimen, currentSpecimen)) {
                    this.specimenValidations.put(currentCentreSpecimen, currentSpecimen, results);
                } else {
                    this.specimenValidations.get(currentCentreSpecimen, currentSpecimen).addAll(results);
                }
                if (!this.centreSpecimenValidations.containsKey(currentCentreSpecimen)) {
                    this.centreSpecimenValidations.putAll(currentCentreSpecimen, results);
                } else {
                    this.centreSpecimenValidations.get(currentCentreSpecimen).addAll(results);
                }
            }

        } else {
            if (parameter.getClass().equals(CentreSpecimen.class)) {
                this.validCentreSpecimens.add((CentreSpecimen) parameter);
            }
            if (parameter.getClass().getSuperclass().equals(Specimen.class) ) {
                this.validSpecimens.get(currentCentreSpecimen).add((Specimen) parameter);
            }
        }
    }

    @Override
    public boolean executeAndReturn(Object parameter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
