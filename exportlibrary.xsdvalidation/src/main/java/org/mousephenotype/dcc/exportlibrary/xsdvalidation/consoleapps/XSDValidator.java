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
package org.mousephenotype.dcc.exportlibrary.xsdvalidation.consoleapps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;


import org.mousephenotype.dcc.exportlibrary.xsdvalidation.controls.ProcedureResultsXMLValidityChecker;
import org.mousephenotype.dcc.exportlibrary.xsdvalidation.controls.SpecimenResultsXMLValidityChecker;
import org.mousephenotype.dcc.exportlibrary.xsdvalidation.controls.XMLFileSplitter;
import org.mousephenotype.dcc.exportlibrary.xsdvalidation.controls.XMLValidityChecker;
import org.mousephenotype.dcc.utils.io.console.Argument;
import org.mousephenotype.dcc.utils.io.console.ConsoleReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author julian
 * @author Duncan
 */
public class XSDValidator extends ConsoleReader {

    private static final Logger logger = LoggerFactory.getLogger(XSDValidator.class);
    private SpecimenResultsXMLValidityChecker specimenResultsXMLValidityChecker;
    private ProcedureResultsXMLValidityChecker procedureResultsXMLValidityChecker;
    private static boolean specimenHasIssues = false;
    private static boolean proceduresHasIssues = false;

    private void logExceptions(XMLValidityChecker xmlValidityChecker, String filename) {
        if (xmlValidityChecker.errorsFound()) {
            for (SAXParseException exception : xmlValidityChecker.getExceptionMessages()) {
                XSDValidator.logger.error("{} [ {} : {} ]  || {} ||", new Object[]{exception.getSystemId(),
                            exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage()});
            }
            XSDValidator.logger.error("{} errors found", xmlValidityChecker.getExceptionMessages().size());
            String location = new File(".").getAbsolutePath();
            location = location.substring(0, location.length() - 1);
            XSDValidator.logger.error("please find the logs at {}exec_logs/", location);
        } else {
            XSDValidator.logger.info("{} is valid", filename);
        }
    }

    private void validateSpecimen(String specimenResultsFilename) throws SAXException, IOException {
        XSDValidator.logger.info("validating {}", specimenResultsFilename);
        this.specimenResultsXMLValidityChecker = new SpecimenResultsXMLValidityChecker();
        this.specimenResultsXMLValidityChecker.attachErrorHandler();
        this.specimenResultsXMLValidityChecker.checkWithHandler(specimenResultsFilename);
        specimenHasIssues = this.specimenResultsXMLValidityChecker.errorsFound();
        this.logExceptions(this.specimenResultsXMLValidityChecker, specimenResultsFilename);
    }

    private void validateProcedureResults(String procedureResultsFilename) throws SAXException, IOException {
        XSDValidator.logger.info("validating {}", procedureResultsFilename);
        this.procedureResultsXMLValidityChecker = new ProcedureResultsXMLValidityChecker();
        this.procedureResultsXMLValidityChecker.attachErrorHandler();
        this.procedureResultsXMLValidityChecker.checkWithHandler(procedureResultsFilename);
        proceduresHasIssues = this.procedureResultsXMLValidityChecker.errorsFound();
        this.logExceptions(this.procedureResultsXMLValidityChecker, procedureResultsFilename);
    }

    public void run(String specimenResultsFilename, String procedureResultsFilename) {
        try {
            this.validateSpecimen(specimenResultsFilename);
        } catch (SAXParseException ex) {
            logger.error("error reading specimen xsd", ex);
        } catch (SAXException ex) {
            logger.error("error reading specimen xsd", ex);
        } catch (IOException ex) {
            logger.error("error reading specimen xsd", ex);
        }
        try {
            this.validateProcedureResults(procedureResultsFilename);
        } catch (SAXParseException ex) {
            logger.error("error reading procedure xsd", ex);

        } catch (SAXException ex) {
            logger.error("error reading procedure xsd", ex);
        } catch (IOException ex) {
            logger.error("error reading procedure xsd", ex);
        }
    }

    public XSDValidator(List<Argument<?>> arguments) {
        super(arguments);
    }

    private static boolean fileExists(String filename) {
        if (new File(filename).exists()) {
            return true;
        } else {
            XSDValidator.logger.error("{} No such file or directory", filename);
        }
        return false;
    }

    public static void main(String[] args) {


        Argument<String> specimenResultsFilenameArgument = new Argument<String>(String.class, "s", "specimens", "specimenFilename", true, "the file containing the specimens");
        Argument<String> procedureResultsFilenameArgument = new Argument<String>(String.class, "p", "procedures", "procedureFilename", true, "the file containing the experimental results");
        Argument<Integer> breakFile = new Argument<Integer>(Integer.class, "c", "chunk", "The size of the chunks", true, "Split this file into files containing this number of experiments or specimens");

        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(specimenResultsFilenameArgument);
        arguments.add(procedureResultsFilenameArgument);
        arguments.add(breakFile);

        XSDValidator validator = new XSDValidator(arguments);

        validator.setValues(args);

        if (args.length < 1) {

            XSDValidator.logger.error(validator.usage());
            return;
        }


        if (validator.parsingOK() && args.length > 4) {
            if (XSDValidator.fileExists(specimenResultsFilenameArgument.getValue())) {
                if (XSDValidator.fileExists(procedureResultsFilenameArgument.getValue())) {
                    validator.run(specimenResultsFilenameArgument.getValue(), procedureResultsFilenameArgument.getValue());
                } else {
                    XSDValidator.logger.error("file {} does not exist", procedureResultsFilenameArgument.getValue());
                    XSDValidator.logger.error(validator.usage());
                }
            } else {
                XSDValidator.logger.error("file {} does not exist", specimenResultsFilenameArgument.getValue());
                XSDValidator.logger.error(validator.usage());
            }
            return;
        }


        if (validator.parsingOK()) {
            if (specimenResultsFilenameArgument.getValue() != null) {
                if (XSDValidator.fileExists(specimenResultsFilenameArgument.getValue())) {
                    try {
                        validator.validateSpecimen(specimenResultsFilenameArgument.getValue());
                    } catch (SAXParseException ex) {
                        logger.error("error reading specimen xsd", ex);
                    } catch (SAXException ex) {
                        logger.error("error reading specimen xsd", ex);
                    } catch (IOException ex) {
                        logger.error("error reading specimen xsd", ex);
                    }
                } else {
                    XSDValidator.logger.error("file {} does not exist", specimenResultsFilenameArgument.getValue());
                    XSDValidator.logger.error(validator.usage());
                }
                // This part of the code will break very large files into the specified number of specimens
                if (breakFile.getValue() != null && XSDValidator.fileExists(specimenResultsFilenameArgument.getValue())) {
                    if (specimenHasIssues) {
                        logger.error("Files that contain xsd errors cannot be split");
                    } else if (breakFile.getValue() instanceof Integer && breakFile.getValue() > 1) {
                        XMLFileSplitter filesplitter = new XMLFileSplitter();
                        try {
                            filesplitter.splitSpecimens(specimenResultsFilenameArgument.getValue(), breakFile.getValue());
                        } catch (JAXBException ex) {
                            logger.error("There was an issue marshalling the xml into objects");
                        } catch (IOException ioe) {
                            logger.error("There was an error processing the files");
                        }
                    } else {
                        logger.error("File can only be split into of positive interger sizes");
                    }
                }
            }
            if (procedureResultsFilenameArgument.getValue() != null) {
                if (XSDValidator.fileExists(procedureResultsFilenameArgument.getValue())) {
                    try {
                        validator.validateProcedureResults(procedureResultsFilenameArgument.getValue());
                    } catch (SAXParseException ex) {
                        logger.error("error reading procedure xsd", ex);
                    } catch (SAXException ex) {
                        logger.error("error reading procedure xsd", ex);
                    } catch (IOException ex) {
                        logger.error("error reading procedure xsd", ex);
                    }
                } else {
                    XSDValidator.logger.error("file {} does not exist", procedureResultsFilenameArgument.getValue());
                    XSDValidator.logger.error(validator.usage());
                }
                // This part of the code will break very large files into the specified number of chunks
                if (proceduresHasIssues) {
                    logger.error("Files that contain xsd errors cannot be split");
                } else if (breakFile.getValue() != null && XSDValidator.fileExists(procedureResultsFilenameArgument.getValue())) {
                    if (breakFile.getValue() instanceof Integer && breakFile.getValue() > 1) {
                        XMLFileSplitter filesplitter = new XMLFileSplitter();
                        try {
                            filesplitter.splitProcedures(procedureResultsFilenameArgument.getValue(), breakFile.getValue());
                        } catch (JAXBException ex) {
                            logger.error("Processing the Experiment file into chunks");
                        } catch (IOException ioe) {
                            logger.error("There was an error processing the files");
                        }
                    } else {
                        logger.error("File can only be split into chunks of positive interger sizes");
                    }
                }
            }
        } else {
            XSDValidator.logger.error(validator.usage());
        }
    }

    @Override
    public String example() {
        StringBuilder sb = new StringBuilder("example \n java -jar ");
        String jarFilename = "jarfile.jar";
        try {
            jarFilename = XSDValidator.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            jarFilename = jarFilename.substring(jarFilename.lastIndexOf("/") == -1 ? 0 : jarFilename.lastIndexOf("/") + 1, jarFilename.length());
        } catch (Exception e) {
        }

        sb.append(jarFilename);
        sb.append(" -s specimensFile.xml ");
        sb.append(" -p procedureResultsFile.xml ");

        sb.append(" \nor\n");
        sb.append(jarFilename);
        sb.append(" -s specimensFile.xml ");
        sb.append("\nor\n");
        sb.append(jarFilename);
        sb.append(" -p procedureResultsFile.xml \n");

        sb.append("\n\n");
        sb.append(" For very large files use the -c flag to break the files into managable chunks. e.g. \n");
        sb.append(jarFilename);
        sb.append(" -p procedureResultsFile.xml -c 4000 (Recomended size)\n");
        return sb.toString();
    }

    @Override
    public int getMinimumOptionalParameters() {
        return 1;
    }
}
