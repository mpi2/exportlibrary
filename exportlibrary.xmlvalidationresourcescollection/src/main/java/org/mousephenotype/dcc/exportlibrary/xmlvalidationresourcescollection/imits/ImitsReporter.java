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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.imits;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import org.apache.commons.configuration.ConfigurationException;
import org.mousephenotype.dcc.exportlibrary.external.imits_reports.MicroinjectionAttemptReport;
import org.mousephenotype.dcc.exportlibrary.external.imits_reports.MicroinjectionAttemptReports;
import org.mousephenotype.dcc.exportlibrary.external.imits_reports.PhenotypeAttemptReport;
import org.mousephenotype.dcc.exportlibrary.external.imits_reports.PhenotypeAttemptReports;

import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.*;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 * 
 * THIS CLASS MAKES USE OF A SQL ENGINE, WONT WORK ON XML DOCUMENTS
 * 
 */
public class ImitsReporter {

    private static final Logger logger = LoggerFactory.getLogger(ImitsReporter.class);
    private HibernateManager hibernateManager;
    private static final String QUERIES_FILENAME = "report_phenotypicAttempts.lsql";
    private final Reader reader;
    private final Properties queries;
    //
    private MicroinjectionAttempts MicroinjectionAttempts;
    private PhenotypeAttempts PhenotypeAttempts;

    public ImitsReporter(HibernateManager hibernateManager) throws FileNotFoundException, IOException, ConfigurationException {
        this.hibernateManager = hibernateManager;
        this.reader = new Reader(QUERIES_FILENAME);
        queries = this.reader.getProperties();
    }

   

    private void generateMicroinjectionAttemptReport(Long hjid) {
        if (this.MicroinjectionAttempts == null) {
            MicroinjectionAttempts = hibernateManager.load(MicroinjectionAttempts.class, hjid);
        }
        //
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("hjid", hjid);
        //
        List totalAttemptsPerProductionCentre = hibernateManager.aggregateResults(queries.getProperty("microinjectionAttempt_totalAttempts"), parameters);
        //
        Iterator it = totalAttemptsPerProductionCentre.iterator();
        //
        Object[] queryRawResults = null;
        BigInteger result = null;
        Object productionCentre = null;
        //
        
        MicroinjectionAttemptReports attemptReports = new MicroinjectionAttemptReports();
        
        MicroinjectionAttemptReport report = null;
        
        while (it.hasNext()) {
            report = new MicroinjectionAttemptReport();
            queryRawResults = (Object[]) it.next();
            productionCentre = queryRawResults[0];
            result = BigInteger.valueOf((Long) queryRawResults[1]);
            //
            report.setProductionCentre((ImitsProductionCentre) productionCentre);
            report.setTotalAttempts(result);
            //
            parameters.put("productionCentre", productionCentre);
            result = hibernateManager.count(queries.getProperty("microinjectionAttempt_nullColonyBckgStrain"), parameters);
            report.setNullColonyBackgroundStrainname(result);
            //
            result = hibernateManager.count(queries.getProperty("microinjectionAttempt_nullMouseAlleleSymbol"), parameters);
            report.setNullMouseAlleleSymbol(result);
            //
            attemptReports.getMicroinjectionAttemptReport().add(report);
        }
        hibernateManager.persist(attemptReports);

    }

    private void generatePhenotypeAttemptReport(Long hjid) {
        if (this.PhenotypeAttempts == null) {
            PhenotypeAttempts = hibernateManager.load(PhenotypeAttempts.class, hjid);
        }
        //

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("hjid", hjid);
        //
        List totalAttemptsPerProductionCentre = hibernateManager.aggregateResults(queries.getProperty("phenotypicAttempt_totalAttempts"), parameters);

        Iterator it = totalAttemptsPerProductionCentre.iterator();
        //
        Object[] queryRawResults = null;
        BigInteger result = null;
        Object productionCentre = null;

        PhenotypeAttemptReport report = null;
        PhenotypeAttemptReports attemptReports = new PhenotypeAttemptReports();

        while (it.hasNext()) {
            report = new PhenotypeAttemptReport();
            queryRawResults = (Object[]) it.next();
            productionCentre = queryRawResults[0];
            result = BigInteger.valueOf((Long) queryRawResults[1]);
            //
            report.setProductionCentre((ImitsProductionCentre) productionCentre);
            report.setTotalAttempts(result);
            //
            parameters.put("productionCentre", productionCentre);
            result = hibernateManager.count(queries.getProperty("phenotypicAttempt_nullBkgrdStrain"), parameters);
            report.setNullPhenotypeColonyBackgroundStrainNames(result);
            //
            result = hibernateManager.count(queries.getProperty("phenotypicAttempt_nullMGI"), parameters);
            report.setNullGeneMGIIDs(result);
            
            //
            result = hibernateManager.count(queries.getProperty("phenotypicAttempt_nullColonyBackgroundStrainMGIIDs"), parameters);
            report.setNullColonyBackgroundStrainMGIIDs(result);
            //
            result = hibernateManager.count(queries.getProperty("phenotypicAttempt_nullMarkerSymbol"), parameters);
            report.setNullMarkerSymbols(result);
            //
            result = hibernateManager.count(queries.getProperty("phenotypicAttempt_microInjectionAttemptColonyName"), parameters);
            report.setNullMicroInjectionAttemptColonyNames(result);
            attemptReports.getPPhenotypeAttemptReport().add(report);
        }
        
        
        hibernateManager.persist(attemptReports);

    }

   

    public void run(Long microinjection_hjid, Long phenotype_hjid) {
        this.generateMicroinjectionAttemptReport(microinjection_hjid);
        this.generatePhenotypeAttemptReport(phenotype_hjid);
    }

    /**
     * @return the MicroinjectionAttempts
     */
    public MicroinjectionAttempts getMicroinjectionAttempts() {
        return MicroinjectionAttempts;
    }

    /**
     * @param MicroinjectionAttempts the MicroinjectionAttempts to set
     */
    public void setMicroinjectionAttempts(MicroinjectionAttempts MicroinjectionAttempts) {
        this.MicroinjectionAttempts = MicroinjectionAttempts;
    }

    /**
     * @return the PhenotypeAttempts
     */
    public PhenotypeAttempts getPhenotypeAttempts() {
        return PhenotypeAttempts;
    }

    /**
     * @param PhenotypeAttempts the PhenotypeAttempts to set
     */
    public void setPhenotypeAttempts(PhenotypeAttempts PhenotypeAttempts) {
        this.PhenotypeAttempts = PhenotypeAttempts;
    }
}
