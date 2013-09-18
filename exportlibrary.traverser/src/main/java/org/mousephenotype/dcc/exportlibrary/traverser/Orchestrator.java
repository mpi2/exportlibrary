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

import java.io.IOException;
import java.util.Calendar;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ResourceVersion;
import org.mousephenotype.dcc.exportlibrary.datastructure.traverser.CentreProcedureSetTraverser;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class Orchestrator {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Orchestrator.class);
    private final HibernateManager hibernateManager;
    private final CentreProcedureSetLoader centreProcedureSetLoader;
    private final CentreProcedureSetTraverser centreProcedureSetTraverser;
    private final SubmissionSetLoader submissionSetLoader;

    public Orchestrator(HibernateManager hibernateManager, SubmissionSetLoader submissionSetLoader) {
        this.hibernateManager = hibernateManager;

        this.centreProcedureSetLoader = new CentreProcedureSetLoader(hibernateManager);
        this.centreProcedureSetTraverser = new CentreProcedureSetTraverser();
        this.submissionSetLoader = submissionSetLoader;
    }

    public String getValidFilename(CentreILARcode centreILARcode) {
        return centreILARcode.toString() + "_valid.csv";
    }

    public String getInvalidFilename(CentreILARcode centreILARcode) {
        return centreILARcode.toString() + "_invalid.csv";
    }

    public static void run(HibernateManager hibernateManager, Calendar submissionDate, SubmissionSetLoader submissionSetLoader) throws HibernateException, IOException {
        ResourceVersion resourceVersion = new ResourceVersion();
        for (CentreILARcode centreILARcode : CentreILARcode.values()) {
            Orchestrator orchestrator = new Orchestrator(hibernateManager, submissionSetLoader);
            orchestrator.run(centreILARcode, resourceVersion, submissionDate, false);
        }
    }

    public static void run(HibernateManager hibernateManager, SubmissionSetLoader submissionSetLoader) throws HibernateException, IOException {
        ResourceVersion resourceVersion = new ResourceVersion();
        for (CentreILARcode centreILARcode : CentreILARcode.values()) {
            Orchestrator orchestrator = new Orchestrator(hibernateManager, submissionSetLoader);
            orchestrator.run(centreILARcode, null, null, resourceVersion, false);
        }
    }

    public void run(Long trackerID, ResourceVersion resourceVersion) throws HibernateException, IOException {
        this.submissionSetLoader.load(trackerID);
        CentreProcedureSet centreProcedureSet = this.submissionSetLoader.getCentreProcedureSet();
        if (centreProcedureSet.isSetCentre()) {
            logger.info("running traveser for {} centreprocedures", centreProcedureSet.getCentre().size());
            for (CentreProcedure centreProcedure : centreProcedureSet.getCentre()) {
                logger.info("{} experiments for centre {}", centreProcedure.getExperiment().size(), centreProcedure.getCentreID());
                logger.info("{} lines for centre {}", centreProcedure.getLine().size(), centreProcedure.getCentreID());
                logger.info("{} housing for centre {}",centreProcedure.getLine().size(), centreProcedure.getCentreID());
            }
            this.orchestrate(centreProcedureSet, resourceVersion, false, null, null);
        }
    }

    public void run(CentreILARcode centreILARcode, ResourceVersion resourceVersion, Calendar submissionDate, boolean toFile) throws HibernateException, IOException {
        logger.info("retriving centre procedures for {}", centreILARcode.toString());
        CentreProcedureSet centreProcedureSet = this.centreProcedureSetLoader.load(centreILARcode, submissionDate);
        if (centreProcedureSet.isSetCentre()) {
            logger.info("{} centreProcedures loaded", centreProcedureSet.getCentre().size());
            this.orchestrate(centreProcedureSet, resourceVersion, toFile, null, null);
        }
    }

    public void run(CentreILARcode centreILARcode, String validFilename, String inValidFilename, ResourceVersion resourceVersion, boolean toFile) throws HibernateException, IOException {
        //1./retrieve all the experiments for a centre
        logger.info("retriving centre procedures for {}", centreILARcode.toString());
        CentreProcedureSet centreProcedureSet = this.centreProcedureSetLoader.load(centreILARcode);
        if (centreProcedureSet.isSetCentre()) {
            logger.info("{} centreProcedures loaded", centreProcedureSet.getCentre().size());
            this.orchestrate(centreProcedureSet, resourceVersion, toFile, validFilename, inValidFilename);
        }
    }

    private void orchestrate(CentreProcedureSet centreProcedureSet, ResourceVersion resourceVersion, boolean toFile, String validFilename, String inValidFilename) throws HibernateException, IOException {
        CommandImpl validationResultsExtractor = new CommandImpl(hibernateManager);
        
        Calendar start =  DatatypeConverter.now();
        logger.info("traversing the structure. Started at {} ", DatatypeConverter.printDateTime(start));
        this.centreProcedureSetTraverser.run(validationResultsExtractor, centreProcedureSet);
        Calendar finish = DatatypeConverter.now();
        logger.info("traversing the structure. Finished at {}", DatatypeConverter.printDateTime(finish));
        logger.info("traverse lasted for {}", DatatypeConverter.getDuration(start, finish));

        //3./produce report
        Reporter reporter = new Reporter(validationResultsExtractor, this.hibernateManager);
        //reporter.persistExperiments(validFilename, inValidFilename);

        logger.info("compiling results");
        reporter.compileResults(resourceVersion,DatatypeConverter.getMilisDuration(start, finish));
        logger.info("updating history");
        reporter.updateHistory();
        
        //
        if (toFile) {
            reporter.persistExperiments(validFilename, inValidFilename);
        }
    }
}
