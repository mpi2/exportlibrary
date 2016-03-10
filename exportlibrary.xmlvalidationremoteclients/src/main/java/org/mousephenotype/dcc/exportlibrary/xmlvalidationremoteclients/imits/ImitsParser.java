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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationremoteclients.imits;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.GenotypicInformation;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.ImitsMIPlanStatus;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.ImitsMicroinjectionStatus;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.ImitsPhenotypeStatus;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.ImitsProductionCentre;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MIPlan;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MiPlans;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MicroinjectionAttempt;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MicroinjectionAttemptStatusDates;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MicroinjectionAttempts;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.MicroinjectionPlansStatusDates;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempt;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttemptStatusDates;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.PhenotypeAttempts;

import org.slf4j.LoggerFactory;

public class ImitsParser {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImitsParser.class);
    public ContainerFactory containerFactory = new ContainerFactory() {
        @Override
        public LinkedHashMap createObjectContainer() {
            return new LinkedHashMap();
        }

        @Override
        public List creatArrayContainer() {
            return new LinkedList();
        }
    };

    private LinkedList parseJsonFile(String filename) throws FileNotFoundException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        LinkedList parse;
        try (BufferedReader bf = new BufferedReader(new FileReader(filename))) {
            parse = (LinkedList) parser.parse(bf, containerFactory);
            logger.trace("{} lines in {}", parse.size(), filename);
        }
        return parse;
    }

    public PhenotypeAttempts getPhenotypeAttempts(String filename) throws FileNotFoundException, IOException, IOException, ParseException, NullPointerException {
        LinkedList jsonDocs = this.parseJsonFile(filename);

        PhenotypeAttempts phenotypeAttempts = new PhenotypeAttempts();
        PhenotypeAttempt phenotypeAttempt = null;
        GenotypicInformation genotypicInformation = new GenotypicInformation();
        PhenotypeAttemptStatusDates phenotypeAttemptStatusDates = null;
        Map<String, Object> jsonDoc = null;
        Set setOfKeys = new HashSet();

        //
        logger.trace("{} lines in jsonDoc", jsonDocs.size());
        for (Iterator<LinkedHashMap<String, Object>> it = jsonDocs.iterator(); it.hasNext();) {
            jsonDoc = it.next();
            phenotypeAttempt = new PhenotypeAttempt();
            genotypicInformation = new GenotypicInformation();
            //
            phenotypeAttempt.setImitsID(BigInteger.valueOf((Long) jsonDoc.get("id")));
            phenotypeAttempt.setPhenotypeColonyName((String) jsonDoc.get("colony_name"));
            phenotypeAttempt.setPhenotypeStatus(ImitsPhenotypeStatus.fromValue((String) jsonDoc.get("status_name")));
            phenotypeAttempt.setProductionCentre(ImitsProductionCentre.fromValue((String) jsonDoc.get("cohort_production_centre_name")));
            phenotypeAttempt.setMiPlanID(BigInteger.valueOf((Long) jsonDoc.get("mi_plan_id")));

            //
            genotypicInformation.setMarkerSymbol((String) jsonDoc.get("marker_symbol"));
            genotypicInformation.setMicroInjectionAttemptColonyName((String) jsonDoc.get("mi_attempt_colony_name"));

            if (jsonDoc.get("colony_background_strain_name") == null || ((String) jsonDoc.get("colony_background_strain_name")).equals("null")) {
                genotypicInformation.setPhenotypeColonyBackgroundStrainName((String) jsonDoc.get("mi_attempt_colony_background_strain_name"));
            } else {
                genotypicInformation.setPhenotypeColonyBackgroundStrainName((String) jsonDoc.get("colony_background_strain_name"));
            }
            genotypicInformation.setPhenotypeMouseAlleleType((String) jsonDoc.get("mouse_allele_type"));
            genotypicInformation.setMouseAlleleSymbol((String) jsonDoc.get("allele_symbol"));
            //
            phenotypeAttempt.setGenotypicInformation(genotypicInformation);


            if (jsonDoc.get("status_dates") != null) {
                for (Object key : ((LinkedHashMap) jsonDoc.get("status_dates")).keySet()) {
                    setOfKeys.add(key);
                }

                phenotypeAttemptStatusDates = new PhenotypeAttemptStatusDates();
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Phenotype Attempt Registered") != null) {
                    phenotypeAttemptStatusDates.setPhenotypeAttemptRegistered(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Phenotype Attempt Registered")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Cre Excision Started") != null) {
                    phenotypeAttemptStatusDates.setCreExcisionStarted(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Cre Excision Started")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Rederivation Started") != null) {
                    phenotypeAttemptStatusDates.setRederivationStarted(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Rederivation Started")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Rederivation Complete") != null) {
                    phenotypeAttemptStatusDates.setRederivationComplete(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Rederivation Complete")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Cre Excision Complete") != null) {
                    phenotypeAttemptStatusDates.setCreExcisionComplete(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Cre Excision Complete")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Phenotype Attempt Aborted") != null) {
                    phenotypeAttemptStatusDates.setPhenotypeAttemptAborted(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Phenotype Attempt Aborted")));
                }
                phenotypeAttempt.setStatusDates(phenotypeAttemptStatusDates);
            }

            phenotypeAttempts.getPhenotypeAttempt().add(phenotypeAttempt);
        }
        logger.trace("{} phenotypeattempts", phenotypeAttempts.getPhenotypeAttempt().size());
        logger.trace("status dates keys");
        for (Object key : setOfKeys) {
            logger.trace("{}", key);
        }
        return phenotypeAttempts;
    }

    public MicroinjectionAttempts getMicroinjectionAttempts(String filename) throws FileNotFoundException, IOException, IOException, ParseException, NullPointerException {
        LinkedList jsonDocs = this.parseJsonFile(filename);

        MicroinjectionAttempts microinjectionAttempts = new MicroinjectionAttempts();
        MicroinjectionAttempt microinjectionAttempt = null;
        MicroinjectionAttemptStatusDates statusDates = null;
        Set setOfKeys = new HashSet();

        Map<String, Object> jsonDoc = null;
        for (Iterator<LinkedHashMap<String, Object>> it = jsonDocs.iterator(); it.hasNext();) {
            jsonDoc = it.next();
            microinjectionAttempt = new MicroinjectionAttempt();

            microinjectionAttempt.setBlastStrainName((String) jsonDoc.get("blast_strain_name"));
            microinjectionAttempt.setColonyBackgroundStrainName((String) jsonDoc.get("colony_background_strain_name"));
            microinjectionAttempt.setColonyName((String) jsonDoc.get("colony_name"));
            microinjectionAttempt.setConsortiumName((String) jsonDoc.get("consortium_name"));
            microinjectionAttempt.setEsCellMarkerSymbol((String) jsonDoc.get("es_cell_marker_symbol"));
            microinjectionAttempt.setEsCellAlleleSymbol((String) jsonDoc.get("es_cell_allele_symbol"));
            microinjectionAttempt.setEsCellName((String) jsonDoc.get("es_cell_name"));
            microinjectionAttempt.setMouseAlleleSymbol((String) jsonDoc.get("mouse_allele_symbol"));
            microinjectionAttempt.setMouseAlleleSymbolSuperscript((String) jsonDoc.get("mouse_allele_symbol_superscript"));
            microinjectionAttempt.setPhenotypeAttemptsCount(BigInteger.valueOf((Long) jsonDoc.get("phenotype_attempts_count")));
            microinjectionAttempt.setPipelineName((String) jsonDoc.get("pipeline_name"));
            microinjectionAttempt.setProductionCentre(ImitsProductionCentre.fromValue((String) jsonDoc.get("production_centre_name")));
            microinjectionAttempt.setStatusName(ImitsMicroinjectionStatus.fromValue((String) jsonDoc.get("status_name")));
            microinjectionAttempt.setTestCrossStrainName((String) jsonDoc.get("test_cross_strain_name"));


            if (jsonDoc.get("status_dates") != null) {
                for (Object key : ((LinkedHashMap) jsonDoc.get("status_dates")).keySet()) {
                    setOfKeys.add(key);
                }
                statusDates = new MicroinjectionAttemptStatusDates();
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Micro-injection in progress") != null) {
                    statusDates.setMicroinjectionInProgress(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Micro-injection in progress")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Chimeras obtained") != null) {
                    statusDates.setChimerasObtained(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Chimeras obtained")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Genotype confirmed") != null) {
                    statusDates.setGenotypeConfirmed(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Genotype confirmed")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Micro-injection aborted") != null) {
                    statusDates.setMicroInjectionAborted(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Micro-injection aborted")));
                }
                microinjectionAttempt.setStatusDates(statusDates);
            }


            microinjectionAttempts.getMicroinjectionAttempt().add(microinjectionAttempt);
        }

        logger.trace("{} microinjection attempts", microinjectionAttempts.getMicroinjectionAttempt().size());
        logger.trace("microinjection attempts");
        for (Object key : setOfKeys) {
            logger.trace("{}", key);
        }

        return microinjectionAttempts;
    }

    public MiPlans getMiPlans(String filename) throws FileNotFoundException, IOException, IOException, ParseException, NullPointerException {
        LinkedList jsonDocs = this.parseJsonFile(filename);
        MiPlans miPlans = new MiPlans();
        MIPlan miPlan = null;
        MicroinjectionPlansStatusDates statusDates = null;
        logger.trace("{} lines in jsonDoc", jsonDocs.size());

        Set setOfKeys = new HashSet();
        for (Iterator<LinkedHashMap<String, Object>> it = jsonDocs.iterator(); it.hasNext();) {
            Map<String, Object> jsonDoc = it.next();
            miPlan = new MIPlan();
            miPlan.setId(BigInteger.valueOf((Long) jsonDoc.get("id")));
            miPlan.setMarkerMGIaccessionID((String) jsonDoc.get("mgi_accession_id"));
            miPlan.setProductionCentre(ImitsProductionCentre.fromValue((String) jsonDoc.get("production_centre_name")));
            try {
                miPlan.setStatus(ImitsMIPlanStatus.fromValue((String) jsonDoc.get("status_name")));
            } catch (Exception ex) {
                logger.error("{}", (String) jsonDoc.get("status_name"), ex);
            }
            if (jsonDoc.get("status_dates") != null) {
                for (Object key : ((LinkedHashMap) jsonDoc.get("status_dates")).keySet()) {
                    setOfKeys.add(key);
                }
                statusDates = new MicroinjectionPlansStatusDates();

                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Interest") != null) {
                    statusDates.setInterest(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Interest")));
                }

                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Inactive") != null) {
                    statusDates.setInactive(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Inactive")));
                }

                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Assigned") != null) {
                    statusDates.setAssigned(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Assigned")));
                }

                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Inspect - Conflict") != null) {
                    statusDates.setInspectConflict(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Inspect - Conflict")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Inspect - GLT Mouse") != null) {
                    statusDates.setInspectGLTmouse(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Inspect - GLT Mouse")));

                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Inspect - MI Attempt") != null) {
                    statusDates.setInspectMIattempt(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Inspect - MI Attempt")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("withdrawn") != null) {
                    statusDates.setWithdrawn(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("withdrawn")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Assigned - ES Cell QC Complete") != null) {
                    statusDates.setAssignedESCellQCcomplete(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Assigned - ES Cell QC Complete")));
                }
                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Assigned - ES Cell QC In Progress") != null) {
                    statusDates.setAssignedESCellQCinProgress(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Assigned - ES Cell QC In Progress")));
                }

                if (((LinkedHashMap) jsonDoc.get("status_dates")).get("Conflict") != null) {
                    statusDates.setConflict(DatatypeConverter.parseDate((String) ((LinkedHashMap) jsonDoc.get("status_dates")).get("Conflict")));
                }

                miPlan.setStatusDates(statusDates);

            }
            miPlans.getMiPlan().add(miPlan);
        }
        logger.trace("{} miplans", miPlans.getMiPlan().size());
        logger.trace("status dates keys");
        for (Object key : setOfKeys) {
            logger.trace("{}", key);
        }
        return miPlans;
    }
}
