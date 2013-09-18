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
package org.mousephenotype.dcc.exportlibrary.exporter.dbloading;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.hibernate.type.StringType;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.utils.io.conf.FileReader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loader {

    private static final Logger logger = LoggerFactory.getLogger(Loader.class);
    private final HibernateManager hibernateManager;
    private static final String VALID_MUTANTS = "valid_mutants.sql";
    private static final String VALID_BASELINES = "valid_baselines.sql";
    private static final String SINGLE_COLONYID = "one_instace_for_each_colony_id.sql";

    public Loader(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    public CentreSpecimenSet getCentre(CentreILARcode centreILARcode) {
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        List<CentreSpecimen> query = this.hibernateManager.query("from CentreSpecimen where centreID = :centreID",
                ImmutableMap.<String, Object>builder().put("centreID", centreILARcode).build(),
                CentreSpecimen.class);
        centreSpecimenSet.getCentre().addAll(query);
        return centreSpecimenSet;

    }

    public CentreSpecimenSet getColony(String colonyID) {
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();

        List<CentreSpecimen> query = this.hibernateManager.query("from CentreSpecimen centreSpecimen inner join fetch centreSpecimen.mouseOrEmbryo specimen where specimen.colonyID = :colonyID",
                ImmutableMap.<String, Object>builder().put("colonyID", colonyID).build(),
                CentreSpecimen.class);
        centreSpecimenSet.getCentre().addAll(query);
        return centreSpecimenSet;
    }

    private CentreSpecimen getCentreSpecimen(CentreSpecimenSet centreSpecimenSet, CentreILARcode centreILARcode) {
        for (CentreSpecimen centreSpecimen : centreSpecimenSet.getCentre()) {
            if (centreSpecimen.getCentreID() == centreILARcode) {
                return centreSpecimen;
            }
        }
        return null;
    }

    public CentreSpecimenSet getValidMutants() throws ConfigurationException, HibernateException {
        String printFile = FileReader.printFile(VALID_MUTANTS);
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        List<Specimen> specimens = this.hibernateManager.nativeQuery(printFile, Specimen.class);
        logger.info("{} specimens retrieved", specimens.size());
        if (specimens != null && !specimens.isEmpty()) {
            CentreSpecimen aux = null;
            Table<String, Class, Object> parameters = HashBasedTable.create();

            Map<String, org.hibernate.type.Type> scalars = ImmutableMap.<String, org.hibernate.type.Type>builder().put("centreID", StringType.INSTANCE).build();
            logger.info("linking to ");
            for (Specimen specimen : specimens) {
                parameters.put("specimenHJID", Long.class, specimen.getHjid());
                List<String> nativeQuery = this.hibernateManager.nativeQuery("select CENTRESPECIMEN.CENTREID as centreID from phenodcc_raw.CENTRESPECIMEN join phenodcc_raw.SPECIMEN on CENTRESPECIMEN.HJID = SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0 where SPECIMEN.HJID = :specimenHJID", scalars, parameters);
                if (nativeQuery != null && !nativeQuery.isEmpty()) {
                    logger.trace("{} centre for specimenID {}", nativeQuery.get(0), specimen.getSpecimenID());
                    aux = this.getCentreSpecimen(centreSpecimenSet, CentreILARcode.valueOf(nativeQuery.get(0)));
                    if (aux == null) {
                        aux = new CentreSpecimen();
                        aux.setCentreID(CentreILARcode.valueOf(nativeQuery.get(0)));
                        centreSpecimenSet.getCentre().add(aux);
                    }
                    aux.getMouseOrEmbryo().add(specimen);
                } else {
                    logger.error("specimen HJID {} is not part of a centreSpecimen", specimen.getHjid());
                }
            }
        }
        return centreSpecimenSet;
    }

    public CentreSpecimenSet getValidBaselines() throws ConfigurationException, HibernateException {
        String printFile = FileReader.printFile(VALID_BASELINES);
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        List<Specimen> specimens = this.hibernateManager.nativeQuery(printFile, Specimen.class);
        logger.info("{} specimens retrieved", specimens.size());
        if (specimens != null && !specimens.isEmpty()) {
            CentreSpecimen aux = null;
            Table<String, Class, Object> parameters = HashBasedTable.create();

            Map<String, org.hibernate.type.Type> scalars = ImmutableMap.<String, org.hibernate.type.Type>builder().put("centreID", StringType.INSTANCE).build();
            logger.info("linking to ");
            for (Specimen specimen : specimens) {
                parameters.put("specimenHJID", Long.class, specimen.getHjid());
                List<String> nativeQuery = this.hibernateManager.nativeQuery("select CENTRESPECIMEN.CENTREID as centreID from phenodcc_raw.CENTRESPECIMEN join phenodcc_raw.SPECIMEN on CENTRESPECIMEN.HJID = SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0 where SPECIMEN.HJID = :specimenHJID", scalars, parameters);
                if (nativeQuery != null && !nativeQuery.isEmpty()) {
                    logger.trace("{} centre for specimenID {}", nativeQuery.get(0), specimen.getSpecimenID());
                    aux = this.getCentreSpecimen(centreSpecimenSet, CentreILARcode.valueOf(nativeQuery.get(0)));
                    if (aux == null) {
                        aux = new CentreSpecimen();
                        aux.setCentreID(CentreILARcode.valueOf(nativeQuery.get(0)));
                        centreSpecimenSet.getCentre().add(aux);
                    }
                    aux.getMouseOrEmbryo().add(specimen);
                } else {
                    logger.error("specimen HJID {} is not part of a centreSpecimen", specimen.getHjid());
                }
            }
        }
        return centreSpecimenSet;
    }

    public CentreSpecimenSet getStrain(String strainID) throws HibernateException {
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        List<CentreSpecimen> query = this.hibernateManager.query("from CentreSpecimen centreSpecimen inner join fetch centreSpecimen.mouseOrEmbryo specimen where specimen.strainID = :strainID",
                ImmutableMap.<String, Object>builder().put("strainID", strainID).build(),
                CentreSpecimen.class);
        centreSpecimenSet.getCentre().addAll(query);
        return centreSpecimenSet;
    }

    private int getCentreProcedureHashCode(CentreProcedure centreProcedure) {
        int result = 17;
        result = 37 * result + centreProcedure.getCentreID().hashCode();
        result = 37 * result + centreProcedure.getPipeline().hashCode();
        result = 37 * result + centreProcedure.getProject().hashCode();
        return result;
    }

    public CentreProcedureSet getValidExperiments(String colonyID) {
        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        Table<String, Class, Object> parameters = HashBasedTable.create();
        parameters.put("colonyID", String.class, colonyID);
        List<Experiment> experiments = this.hibernateManager.nativeQuery("select EXPERIMENT.* from EXPERIMENT EXPERIMENT join phenodcc_raw_valid.experiment_specimen exsp on EXPERIMENT.HJID = exsp.experiment_HJID where exsp.COLONYID = :colonyID",
                Experiment.class, parameters);
        if (experiments != null && !experiments.isEmpty()) {
            CentreProcedure centreProcedure = null;

            Map<Integer, CentreProcedure> centreProcedures = new HashMap<>();

            for (Experiment experiment : experiments) {
                centreProcedure = this.hibernateManager.getContainer(experiment, CentreProcedure.class, "experiment");
                if (centreProcedures.containsKey(this.getCentreProcedureHashCode(centreProcedure))) {
                    centreProcedures.get(this.getCentreProcedureHashCode(centreProcedure)).getExperiment().add(experiment);
                } else {
                    centreProcedures.put(this.getCentreProcedureHashCode(centreProcedure), centreProcedure);
                }
            }
            centreProcedureSet.getCentre().addAll(centreProcedures.values());
        }

        return centreProcedureSet;
    }

    public List<String> getColonyIDsForValidExperiments() {
        String query = "SELECT DISTINCT experiment_specimen.COLONYID\n"
                + "FROM phenodcc_raw_valid.experiment_specimen\n"
                + "WHERE NOT experiment_specimen.ISBASELINE\n"
                + "ORDER BY experiment_specimen.COLONYID";

        List<String> colonyIDs = this.hibernateManager.nativeQuery(query);

        return colonyIDs;
    }

    public List<String> getColonyIDsForValidExperimentsOnBaselineAnimals() {
        String query = "SELECT DISTINCT experiment_specimen.COLONYID\n"
                + "FROM phenodcc_raw_valid.experiment_specimen\n"
                + "WHERE experiment_specimen.ISBASELINE\n"
                + "ORDER BY experiment_specimen.COLONYID";

        List<String> colonyIDs = this.hibernateManager.nativeQuery(query);

        return colonyIDs;
    }

    public CentreSpecimenSet getSingleColonyID() throws ConfigurationException, HibernateException {
        String printFile = FileReader.printFile(SINGLE_COLONYID);
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        List<Specimen> specimens = this.hibernateManager.nativeQuery(printFile, Specimen.class);
        logger.trace("{} specimens retrieved", specimens.size());
        if (specimens != null && !specimens.isEmpty()) {
            CentreSpecimen aux = null;
            Table<String, Class, Object> parameters = HashBasedTable.create();

            Map<String, org.hibernate.type.Type> scalars = ImmutableMap.<String, org.hibernate.type.Type>builder().put("centreID", StringType.INSTANCE).build();
            logger.trace("linking to ");
            for (Specimen specimen : specimens) {
                parameters.put("specimenHJID", Long.class, specimen.getHjid());
                List<String> nativeQuery = this.hibernateManager.nativeQuery("select CENTRESPECIMEN.CENTREID as centreID from phenodcc_raw.CENTRESPECIMEN join phenodcc_raw.SPECIMEN on CENTRESPECIMEN.HJID = SPECIMEN.MOUSEOREMBRYO_CENTRESPECIMEN_0 where SPECIMEN.HJID = :specimenHJID", scalars, parameters);
                if (nativeQuery != null && !nativeQuery.isEmpty()) {
                    logger.trace("{} centre for specimenID {}", nativeQuery.get(0), specimen.getSpecimenID());
                    aux = this.getCentreSpecimen(centreSpecimenSet, CentreILARcode.valueOf(nativeQuery.get(0)));
                    if (aux == null) {
                        aux = new CentreSpecimen();
                        aux.setCentreID(CentreILARcode.valueOf(nativeQuery.get(0)));
                        centreSpecimenSet.getCentre().add(aux);
                    }
                    aux.getMouseOrEmbryo().add(specimen);
                } else {
                    logger.error("specimen HJID {} is not part of a centreSpecimen", specimen.getHjid());
                }
            }
        }
        return centreSpecimenSet;
    }
}