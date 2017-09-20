/**
 * Copyright (C) 2014 Duncan Sneddon <d.sneddon at har.mrc.ac.uk>
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
package org.mousephenotype.dcc.exportlibrary.exportworker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Housing;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Line;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Embryo;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Mouse;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.traverser.CentreProcedureSetTraverser;
import org.mousephenotype.dcc.exportlibrary.datastructure.traverser.CentreSpecimenSetTraverser;
import org.mousephenotype.dcc.exportlibrary.exportcommunication.CentreContainer;
import org.mousephenotype.dcc.exportlibrary.exportcommunication.DataReference;
import org.mousephenotype.dcc.exportlibrary.exportcommunication.SpecimenReference;
import org.mousephenotype.dcc.exportlibrary.exportworker.traversal.HJIDRemover;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

/**
 * This project will export data according to a list that will be provided on
 * the command line
 *
 */
public class ExportWorker {
    
    private static final Logger logger = LoggerFactory.getLogger(ExportWorker.class);
    private static final String OPT_HELP = "h";
    private static final String OPT_CONNECTION_PROPERTIES = "p";
    private static final String OPT_EXPORT_PROP_FILE = "c";
    private static final String OPT_BATCH_SIZE = "s";
    private static final String OPT_EMBRYO_ONLY = "e";
    private static final Options OPTIONS = new Options();
    // used when showing usage information, or help message
    private static final int NUM_CHARS_PER_ROW = 120;
    private static int MAX_OBJECTS = 500;
    private static String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private static String SPECIMEN_CONTEXT = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen";
    private static String EXPERIMENT_CONTEXT = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure";
    // messages to show for each of the command line switches

    static {
        OPTIONS.addOption(OPT_HELP, false, "Show help message on how to use the system.");
        OPTIONS.addOption(OPT_CONNECTION_PROPERTIES, true, "The properties files controlling the connection");
        OPTIONS.addOption(OPT_EXPORT_PROP_FILE, true, "The JSON file contining the information that will control the export.");
        OPTIONS.addOption(OPT_BATCH_SIZE, true, "The maximum number of enteries in a single file");
        OPTIONS.addOption(OPT_EMBRYO_ONLY, false, "Select if embryo only export");
    }
    private HibernateManager hm;
    private final HJIDRemover hjidRemover;
    private EntityManager rawEM;
    
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    public ExportWorker() {
        hjidRemover = new HJIDRemover();
    }
    
    public static void main(String[] args) {
        ExportWorker exportWorker = new ExportWorker();
        exportWorker.setup(args);
    }
    
    private void setup(String[] args) {
        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(OPTIONS, args);
            
            if (cmd.hasOption(OPT_HELP)) { // Show help
                showHelp();
                close(0);
            }
            if (cmd.hasOption(OPT_CONNECTION_PROPERTIES) && cmd.hasOption(OPT_EXPORT_PROP_FILE)) {
                if (cmd.hasOption(OPT_BATCH_SIZE)) {
                    String o = cmd.getOptionValue(OPT_BATCH_SIZE);
                    try {
                        MAX_OBJECTS = Integer.valueOf(o);
                    } catch (NumberFormatException nfe) {
                        logger.error("The number specified could not be parsed. Using default = {}", MAX_OBJECTS);
                    }
                }
                File configFile = getReadableFile(cmd.getOptionValue(OPT_CONNECTION_PROPERTIES));
                File instructionFile = getReadableFile(cmd.getOptionValue(OPT_EXPORT_PROP_FILE));
                if (configFile == null || instructionFile == null) {
                    close(1);
                }
                generateExportFromData(configFile, instructionFile,cmd.hasOption(OPT_EMBRYO_ONLY));
            } else {
                logger.error("There must be a connection file and a data file specified");
                close(1);
            }
        } catch (ParseException ex) {
            logger.error("Could not parse options");
            close(1);
            
        }
        logger.info("All done!");
        close(0);
    }

    /**
     * Controlling method for the process.
     *
     * @param configFile
     * @param instructionFile
     */
    private void generateExportFromData(File configFile, File instructionFile,boolean embryoOnly) {
        // Set up the hibernate code
        rawEM = getEntityManagerFromHibernate(configFile);
        CentreContainer downloadInstructions = parseInstructionsFromFile(instructionFile);
        
        if (!downloadInstructions.getSpecimenReference().isEmpty()) {
            logger.info("Writing Specimen Files");
            processSpecimens(rawEM, downloadInstructions, embryoOnly);
        }
        if (!downloadInstructions.getDataReference().isEmpty()) {
            logger.info("Writing Experiment Files");
            processProcedureData(rawEM, downloadInstructions);
        }
        rawEM.close();
    }
    
    private void processProcedureData(EntityManager rawEM, CentreContainer downloadInstructions) {
        TypedQuery<Line> lineQuery = rawEM.createQuery("Select l From Line l JOIN l.procedure as p where p.hjid=:hjid", Line.class);
        TypedQuery<Experiment> experimentQuery = rawEM.createQuery("Select e from Experiment e JOIN e.procedure as p where p.hjid = :hjid", Experiment.class);
        TypedQuery<Housing> housingQuery = rawEM.createQuery("Select h from Housing h join h.procedure as p where p.hjid=:hjid", Housing.class);
        
        TypedQuery<CentreProcedure> lineCP = rawEM.createQuery("Select cp From CentreProcedure cp JOIN cp.line as l JOIN l.procedure as p where p.hjid=:hjid", CentreProcedure.class);
        TypedQuery<CentreProcedure> experimentCP = rawEM.createQuery("Select cp From CentreProcedure cp JOIN cp.experiment as e JOIN e.procedure as p where p.hjid=:hjid", CentreProcedure.class);
        TypedQuery<CentreProcedure> housingCP = rawEM.createQuery("Select cp From CentreProcedure cp JOIN cp.housing as h JOIN h.procedure as p where p.hjid=:hjid", CentreProcedure.class);
        
        CentreProcedureSet cps = new CentreProcedureSet();
        int count = 1;
        int filecount = 1;
        XMLGenerator g = new XMLGenerator();
        for (DataReference d : downloadInstructions.getDataReference()) {
            switch (d.getType()) {
                case EXPERIMENT:
                    Experiment e = experimentQuery.setParameter("hjid", d.getHjid()).getSingleResult();
                    CentreProcedure ecp = experimentCP.setParameter("hjid", d.getHjid()).getSingleResult();
                    CentreProcedure aecp = getCentreProcedure(ecp, cps, downloadInstructions.getCentreILAR());
                    aecp.getExperiment().add(e);
                    break;
                case LINE:
                    Line l = lineQuery.setParameter("hjid", d.getHjid()).getSingleResult();
                    CentreProcedure lcp = lineCP.setParameter("hjid", d.getHjid()).getSingleResult();
                    CentreProcedure alcp = getCentreProcedure(lcp, cps, downloadInstructions.getCentreILAR());
                    alcp.getLine().add(l);
                    break;
                case HOUSING:
                    Housing h = housingQuery.setParameter("hjid", d.getHjid()).getSingleResult();
                    CentreProcedure hcp = housingCP.setParameter("hjid", d.getHjid()).getSingleResult();
                    CentreProcedure ahcp = getCentreProcedure(hcp, cps, downloadInstructions.getCentreILAR());
                    ahcp.getHousing().add(h);
                    break;
                default:
                    logger.info("Don't know thich type this is");
                    break;
            }
            if (count % MAX_OBJECTS == 0) {
                logger.info("Writing file containing {} procedures", MAX_OBJECTS);
                writeCentreProcedure(cps, g, filecount, downloadInstructions);
                filecount++;
            }
            count++;
        }
        // Write the final CPS
        writeCentreProcedure(cps, g, filecount, downloadInstructions);
    }

    /**
     * Looks for an existing centre procedure that has the same properties as
     * the submitted one or adds and returns the submitting one.
     *
     * @param cp
     * @param cps
     * @return
     */
    private CentreProcedure getCentreProcedure(CentreProcedure cp, CentreProcedureSet cps, CentreILARcode centreILAR) {
        for (CentreProcedure c : cps.getCentre()) {
            if (c.getPipeline().equalsIgnoreCase(cp.getPipeline()) && c.getProject().equalsIgnoreCase(cp.getProject())) {
                return c;
            }
        }
        CentreProcedure newCP = new CentreProcedure();
        newCP.setCentreID(centreILAR);
        newCP.setPipeline(cp.getPipeline());
        newCP.setProject(cp.getProject());
        cps.getCentre().add(newCP);
        return newCP;
    }
    
    private void processSpecimens(EntityManager rawEM, CentreContainer downloadInstructions, boolean embryoOnly) {
        XMLGenerator g = new XMLGenerator();
        
        TypedQuery<Mouse> mouseQuery = rawEM.createQuery("Select s from Mouse s where s.hjid=:hjid", Mouse.class);
        TypedQuery<Embryo> embryoQuery = rawEM.createQuery("Select s from Embryo s where s.hjid=:hjid", Embryo.class);
        
        CentreSpecimenSet css = new CentreSpecimenSet();
        CentreSpecimen cs = new CentreSpecimen();
        cs.setCentreID(downloadInstructions.getCentreILAR());
        // This is just faff
        ArrayList<CentreSpecimen> csl = new ArrayList<CentreSpecimen>();
        csl.add(cs);
        css.setCentre(csl);
        
        int count = 1;
        int filecount = 1;
        ArrayList<Specimen> specimenList = new ArrayList<Specimen>();
        logger.info("There are {} specimens", downloadInstructions.getSpecimenReference().size());
        for (SpecimenReference sp : downloadInstructions.getSpecimenReference()) {
            if (count % MAX_OBJECTS == 0 || count == downloadInstructions.getDataReference().size()) {
                cs.setMouseOrEmbryo(specimenList);
                writeSpecimenSet(css, cs, g, filecount, downloadInstructions);
                filecount++;
                cs.unsetMouseOrEmbryo(); // empty list
                specimenList.clear();
            }
            if (sp.isIsMouse() && !embryoOnly) { // miss out the adults
                Mouse s = mouseQuery.setParameter("hjid", sp.getSpecimenID()).getSingleResult();
                specimenList.add(s);
            } else if (!sp.isIsMouse()){ // only embryo
                Embryo s = embryoQuery.setParameter("hjid", sp.getSpecimenID()).getSingleResult();
                specimenList.add(s);
            }
            count++;
        }
        // Write the remaining specimen to file.
        logger.info("There are {} specimens exported", count);
        cs.setMouseOrEmbryo(specimenList);
        writeSpecimenSet(css, cs, g, filecount, downloadInstructions);
    }
    
    public EntityManager getEntityManagerFromHibernate(File configFile) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(configFile));
            
            hm = new HibernateManager(props, PERSISTENCE_UNITNAME);
            return hm.getEntityManager();
        } catch (IOException ex) {
            logger.info("IOException reading properties file. Closing ....", ex);
            close(1);
            return null;
        }
    }
    
    private void close(int par) {
        if (par == 1) {
            showHelp();
        }
        if (hm != null) {
            hm.close();
        }
        System.exit(par);
    }
    
    private void showHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(NUM_CHARS_PER_ROW, "\n\njava -jar program.jar", "Data Export Worker - Does what it is told and writes files.", OPTIONS, null, true);
    }
    
    private File getReadableFile(String path) {
        File f = new File(path);
        if (f.exists()) {
            if (f.isFile()) {
                if (f.canRead()) {
                    logger.info("Will use supplied properties file '{}'.", path);
                } else {
                    logger.error("The supplied properties file '{}' is unreadable.", path);
                    f = null;
                }
            } else {
                logger.error("The supplied properties path '{}' is not a file.", path);
                f = null;
            }
        } else {
            logger.error("The supplied properties path '{}' does not exists.", path);
            f = null;
        }
        return f;
    }
    
    private CentreContainer parseInstructionsFromFile(File instructionFile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(instructionFile, CentreContainer.class);
        } catch (JsonParseException ex) {
            logger.error("Could not parse JSON", ex);
        } catch (JsonMappingException ex) {
            logger.error("Could not understand JSON", ex);
        } catch (IOException ex) {
            logger.error("Error reading file", ex);
        }
        return null;
    }

    /**
     * Helper method to write to file.
     *
     * @param cps
     * @param downloadInstructions
     * @param g
     * @param filecount
     */
    private void writeCentreProcedure(CentreProcedureSet cps, XMLGenerator g, int filecount, CentreContainer downloadInstructions) {
        try {
            CentreProcedureSetTraverser traverser = new CentreProcedureSetTraverser();
            traverser.run(hjidRemover, cps);
            String fN = downloadInstructions.getCentreILAR()+"."+formatter.format(downloadInstructions.getCreationTimestamp()) +"."+ filecount + ".experiment.xml";
            g.serialize(cps, fN);
            // All CentreProcedures containing Experiment, Line and Housing lists
            for (CentreProcedure c : cps.getCentre()) {
                c.unsetExperiment();
                c.unsetHousing();
                c.unsetLine();
            }
            cps.unsetCentre();
        } catch (JAXBException ex) {
            logger.error("Cound not write xml file for Experiment");
        }
    }

    /**
     * Helper method to write to file.
     *
     * @param cs
     * @param css
     * @param g
     * @param filecount
     * @param downloadInstructions
     */
    private void writeSpecimenSet(CentreSpecimenSet css, CentreSpecimen cs, XMLGenerator g, int filecount, CentreContainer downloadInstructions) {
        try {
            CentreSpecimenSetTraverser traverser = new CentreSpecimenSetTraverser();
            traverser.run(hjidRemover, css);
            String fn = downloadInstructions.getCentreILAR()+"."+formatter.format(downloadInstructions.getCreationTimestamp()) +"."+filecount + ".specimen.xml";
            g.serialize(css, fn);
        } catch (JAXBException ex) {
            logger.error("Could not write XML file");
        }
    }
    
    public void _testExperimentFileWriting(EntityManager em, CentreContainer downloadInstructions) {
        processProcedureData(em, downloadInstructions);
    }
}
