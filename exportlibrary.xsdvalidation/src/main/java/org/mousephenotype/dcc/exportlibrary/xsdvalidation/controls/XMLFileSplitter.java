/**
 * Copyright (C) 2013 Duncan Sneddon <d.sneddon at har.mrc.ac.uk>
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
package org.mousephenotype.dcc.exportlibrary.xsdvalidation.controls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Housing;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Line;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.utils.xml.XMLUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duncan Sneddon 
 */
public class XMLFileSplitter {

    private static final Logger logger = LoggerFactory.getLogger(XMLFileSplitter.class);
    private static final String XML_FILE_REGEX = "([a-zA-Z_]*)[.]([0-9]{4})-([0-9]{2})-([0-9]{2})[.]([0-9]{1,5})[.](specimen|experiment)[.]impc[.](xml)";
    private final Pattern fileregex;

    /**
     * Takes a very large file and splits it into much more manageable chunks.
     * The processed files are then saved under a chunks directory.
     */
    public XMLFileSplitter() {
        fileregex = Pattern.compile(XML_FILE_REGEX);
    }

    /**
     * Dismantles a CentreSpecimenSet object, splits the specimens into chunks
     * of the specified size.
     *
     * The sublists are then re-attached to their parent object and written to a
     * different file.
     *
     * @param fileLoc
     * @param chunk
     * @throws JAXBException
     * @throws IOException
     */
    public void splitSpecimens(String fileLoc, Integer chunk) throws JAXBException, IOException {
        File f = new File(fileLoc);
        CentreSpecimenSet css = XMLUtils.unmarshal(XMLUtils.CONTEXTPATH, CentreSpecimenSet.class, fileLoc);
        // Keep a reference to the parent object and then detach all the children
        List<CentreSpecimen> centres = css.getCentre();
        css.unsetCentre();
        int count = 1; // this is the file name increment
        for (CentreSpecimen cs : centres) {
            // copy and detach specimens
            List<Specimen> specimens = cs.getMouseOrEmbryo();
            cs.unsetMouseOrEmbryo();
            int numberOfSpecimens = specimens.size();
            int index = 0; // This is the pointer along the list
            if (numberOfSpecimens > chunk) {
                while (index < numberOfSpecimens) {
                    List<Specimen> subList;
                    if ((index + chunk) < numberOfSpecimens - 1) {
                        subList = specimens.subList(index, (index + chunk));
                    } else {
                        subList = specimens.subList(index, numberOfSpecimens - 1);
                    }
                    index += chunk;
                    // now recreate the object
                    cs.setMouseOrEmbryo(subList);
                    ArrayList<CentreSpecimen> tmpList = new ArrayList<CentreSpecimen>();
                    tmpList.add(cs);
                    css.setCentre(tmpList);
                    writeSpecimenFileWithIncrement(f, count++, css);
                    //now detach the objects ready for the next loop
                    css.unsetCentre();
                    cs.unsetMouseOrEmbryo();
                }
            }
        }
    }

    /**
     * Dismantles the object structure and the rebuilds it into files containing
     * the specified size of object.
     *
     * The lines and experiments will be separated into individual files. Each
     * of these will contain at upto the specified number of objects.
     *
     * @param fileLoc
     * @param chunk size
     * @throws JAXBException
     * @throws IOException
     */
    public void splitProcedures(String fileLoc, Integer chunk) throws JAXBException, IOException {
        File f = new File(fileLoc);
        CentreProcedureSet cps = XMLUtils.unmarshal(XMLUtils.CONTEXTPATH, CentreProcedureSet.class, fileLoc);
        // Keep a reference to the parent object and then detach all the children
        List<CentreProcedure> centres = cps.getCentre();
        cps.unsetCentre();
        int count = 1; // this is the file name increment
        for (CentreProcedure cp : centres) {
            // copy and detach children
            List<Housing> housing = cp.getHousing();
            List<Line> line = cp.getLine();
            List<Experiment> experiments = cp.getExperiment();
            cp.unsetExperiment();
            cp.unsetHousing();
            cp.unsetLine();

            int numberOfExperiments = experiments.size();
            int numberOfLines = line.size();
            int index = 0; // This is the pointer along the list
            //process the experiments
            if (numberOfExperiments > chunk) {
                while (index < numberOfExperiments) {
                    List<Experiment> sublist;
                    if ((index + chunk) < numberOfExperiments - 1) {
                        sublist = experiments.subList(index, (index + chunk));
                    } else {
                        sublist = experiments.subList(index, numberOfExperiments - 1);
                    }
                    index += chunk;
                    // reassemble an object
                    cp.setExperiment(sublist);
                    cp.setHousing(housing);
                    //cp.setLine(line);
                    ArrayList<CentreProcedure> tmpList = new ArrayList<CentreProcedure>();
                    tmpList.add(cp);
                    cps.setCentre(tmpList);
                    writeProcedureFileWithIncrement(f, count++, cps);
                    // detach temp objects ready for next loop
                    cps.unsetCentre();
                    cp.unsetExperiment();
                    cp.unsetHousing();
                    //cp.unsetLine();
                }
            } else {
                cp.setExperiment(experiments);
                cp.setHousing(housing);
                ArrayList<CentreProcedure> tmpList = new ArrayList<CentreProcedure>();
                tmpList.add(cp);
                cps.setCentre(tmpList);
                writeProcedureFileWithIncrement(f, count++, cps);
                // detach temp objects ready for line processing
                cps.unsetCentre();
                cp.unsetExperiment();
                cp.unsetHousing();
            }
            index = 0; // reset index
            // Now process the lines
            if (numberOfLines > chunk) {
                while (index < numberOfLines) {
                    List<Line> sublist;
                    if ((index + chunk) < numberOfLines - 1) {
                        sublist = line.subList(index, (index + chunk));
                    } else {
                        sublist = line.subList(index, numberOfLines - 1);
                    }
                    index += chunk;
                    // reassemble an object
                    cp.setHousing(housing);
                    cp.setLine(sublist);
                    ArrayList<CentreProcedure> tmpList = new ArrayList<CentreProcedure>();
                    tmpList.add(cp);
                    cps.setCentre(tmpList);
                    writeProcedureFileWithIncrement(f, count++, cps);
                    // detach temp objects ready for next loop
                    cps.unsetCentre();
                    cp.unsetHousing();
                    cp.unsetLine();
                }
            } else {
                cp.setLine(line);
                cp.setHousing(housing);
                ArrayList<CentreProcedure> tmpList = new ArrayList<CentreProcedure>();
                tmpList.add(cp);
                cps.setCentre(tmpList);
                writeProcedureFileWithIncrement(f, count++, cps);
                // detach temp objects ready for line processing
                cps.unsetCentre();
                cp.unsetLine();
                cp.unsetHousing();
            }
        }
    }

    private void writeSpecimenFileWithIncrement(File f, int count, CentreSpecimenSet css) throws JAXBException {
        File newFile = createNewFile(f, count);
        XMLUtils.marshall(XMLUtils.CONTEXTPATH, css, newFile.getAbsolutePath());
    }

    private void writeProcedureFileWithIncrement(File f, int count, CentreProcedureSet cps) throws JAXBException {
        File newFile = createNewFile(f, count);
        XMLUtils.marshall(XMLUtils.CONTEXTPATH, cps, newFile.getAbsolutePath());
    }

    /**
     * This method will create a folder called 'chunked' where the original file
     * was and then write a file to that location.
     *
     * The file will be incremented using the 'count' parameter. If the original
     * file keeps to the IMPC naming convention then the file will be
     * incremented correctly. If the files does not, the increment will be
     * placed immediately before the file ending
     *
     * @param f
     * @param count
     * @return a reference to the new file, correctly named.
     */
    private File createNewFile(File f, int count) {
        String filePath = f.getAbsolutePath().
                substring(0, f.getAbsolutePath().lastIndexOf(File.separator));
        new File(filePath + "/chunks").mkdir();

        // Now process the file name 
        String origFileName = f.getName();
        String writeLoc = filePath + "/chunks/";
        Matcher matcher = fileregex.matcher(origFileName);
        if (matcher.find()) {
            String[] split = origFileName.split("\\.");
            String increment = String.format("%02d", count);
            String filename = split[0] + "." + split[1] + "." + increment + "." + split[3] + "." + split[4] + "." + split[5];
            logger.info("Writing file " + writeLoc + filename);
            return new File(writeLoc + filename);
        } else {
            String begining = origFileName.substring(0, origFileName.lastIndexOf('.'));
            String end = origFileName.substring(origFileName.lastIndexOf('.'), origFileName.length());
            String increment = String.format("%02d", count);
            String filename = begining + "." + increment + end;
            logger.info("Writing file " + writeLoc + filename);
            return new File(writeLoc + filename);
        }
    }
}
