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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.consoleapps;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.IOParameters.CONNECTOR_NAMES;
import org.mousephenotype.dcc.exportlibrary.xmlvalidation.XMLValidationControl;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.consoleapps.support.ValidatorSupport;
import org.mousephenotype.dcc.utils.io.console.Argument;
import org.mousephenotype.dcc.utils.io.console.ConsoleReader;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class Validator extends ConsoleReader {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Validator.class);
    //
    //
    //
    private XMLValidationControl xmlValidationControl;

    @Override
    public String example() {
        StringBuilder sb = new StringBuilder();
        sb.append("java -jar ");
        return sb.toString();
    }

    @Override
    public int getMinimumOptionalParameters() {
        return 2;
    }

    public Validator(List<Argument<?>> arguments) {
        super(arguments);
    }

    public void validate(Map<CONNECTOR_NAMES, IOParameters> ioparameters, String externalConfigurationFilename) throws JAXBException, FileNotFoundException, IllegalArgumentException, Exception {
        xmlValidationControl = new XMLValidationControl();
        xmlValidationControl.run(ioparameters, externalConfigurationFilename);
    }

    public static void main(String[] args) {
        //SUBMISSION_TRACKERID, SUBMISSIONSET_HJID, SUBMISSIONSET, CENTREPROCEDURESET_HJID, CENTRESPECIMENSET_HJID, CENTREPROCEDURESET, CENTRESPECIMENSET
        //"externalResourcesLocation can be (1) null, then local database (2) persistencefilename *.properties (3) url http (4) set of xml documents"
        //Arguments for the documents to be validated
        /*
         * INPUT
         */
        Argument<String> specimenResultsFilenameArgument = new Argument<>(String.class, "s", "specimenFilename", "specimenFilename", true, "the file containing the specimens");
        Argument<String> procedureResultsFilenameArgument = new Argument<>(String.class, "p", "procedureFilename", "procedureFilename", true, "the file containing the experimental results");
        Argument<Long> submissionIDArgument = new Argument<>(Long.class, "d", "submissionID", "submissionID", false, "the submissionID for the centreSpecimenset or for the centreprocedureset");
        Argument<Long> submissionTrackerIDArgument = new Argument<>(Long.class, "t", "submissionTrackerID", "submissionTrackerID", false, "the submission tracker ID for the centreSpecimenset or for the centreprocedureset");
        Argument<String> submissionPersistenceFilenameArgument = new Argument<>(String.class, "f", "submissionIDPersistenceFilename", "submissionIDPersistenceFilename", true, "hibernate properties to load the submission");
        /*
         * RESOURCES
         */
        //Arguments for where to find the external resources
        Argument<String> externalResourcesLocationPersistenceFilenameArgument = new Argument<>(String.class, "h", "externalResourcesLocationPersistenceFilename ", "externalResourcesLocationPersistenceFilename ", true, "hibernate properties file to control the connection with validation resources");
        Argument<Boolean> externalResourcesLocationOnLocalDatabaseArgument = new Argument<>(Boolean.class, "l", "externalResourcesLocationLocalDatabase ", "externalResourcesLocationLocalDatabase ", true, " if not present, downloads the resources as a derby database");
        //

        Argument<String> externalResourcesConfigurationArgument = new Argument<>(String.class, "e", "external", "external", true, "properties file to access remote resources. external.properties is the default name");

        /*
         * RESULT . This is not implemented yet. If from database, to database,
         * if from file to new file.
         */


        //
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(specimenResultsFilenameArgument);
        arguments.add(procedureResultsFilenameArgument);
        arguments.add(submissionIDArgument);
        arguments.add(submissionTrackerIDArgument);
        arguments.add(submissionPersistenceFilenameArgument);
        //
        arguments.add(externalResourcesLocationPersistenceFilenameArgument);
        arguments.add(externalResourcesLocationOnLocalDatabaseArgument);
        //
        arguments.add(externalResourcesConfigurationArgument);
        //
        Validator validator = new Validator(arguments);

        validator.setValues(args);
        //
        if (args.length < 2) {
            Validator.logger.error(validator.usage());
            System.exit(0);
        }
        Map<CONNECTOR_NAMES, IOParameters> ioparameters = ValidatorSupport.buidExecutionIOParameters(
                specimenResultsFilenameArgument.getRawValue(),
                procedureResultsFilenameArgument.getRawValue(),
                submissionIDArgument.getValue(),
                submissionTrackerIDArgument.getValue(),
                submissionPersistenceFilenameArgument.getRawValue(),
                externalResourcesLocationPersistenceFilenameArgument.getRawValue(),
                externalResourcesLocationOnLocalDatabaseArgument.getValue());

        try {
            validator.validate(ioparameters, externalResourcesConfigurationArgument.getValue());

        } catch (JAXBException ex) {
            logger.error("cannot load xmlfile", ex);
            System.exit(-1);
        } catch (FileNotFoundException ex) {
            logger.error("cannot find file", ex);
            System.exit(-1);
        } catch (IllegalArgumentException iae) {
            logger.error("There was a problem running the validation. This could have been caused by the process to gather the data required for validation not being correctly processed. ", iae);
            System.exit(-1);
        } catch (Exception ex) {
            logger.error("", ex);
            System.exit(-1);
        }
    }
}
