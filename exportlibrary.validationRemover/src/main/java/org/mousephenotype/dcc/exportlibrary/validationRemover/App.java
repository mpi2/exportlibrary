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
package org.mousephenotype.dcc.exportlibrary.validationRemover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.io.console.Argument;
import org.mousephenotype.dcc.utils.io.console.ConsoleReader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends ConsoleReader {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final String contextPath = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private HibernateManager hibernateManager;
    private Reader reader;
    private Properties properties;
    private ValidationRemover validationRemover;
    public static final int PARSE_ARGS_FAIL = 100;
    private static final int DB_ERROR_CONNECTION = 103;

    public static String sayHello() {
        logger.info("running hello world");
        return "hello";
    }

    public App(List<Argument<?>> arguments) {
        super(arguments);
    }

    public boolean instantiateHibernateManager(String persistencePropertiesFileName) {
        if (!new File(persistencePropertiesFileName).exists()) {
            logger.info("file does not exist");
            return false;
        }
        try {
            reader = new Reader(persistencePropertiesFileName);
        } catch (ConfigurationException ex) {
            logger.error("error reading configuration file", ex);
            return false;
        }

        properties = reader.getProperties();

        try {
            this.hibernateManager = new HibernateManager(properties, contextPath);
        } catch (Exception ex) {
            logger.error("error loading ", ex);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        Argument<Long> trackingIDArgument = new Argument<>(Long.class, "t", "trackingID", "trackingID", false, "the id for the file by the tracker");
        Argument<String> persistencePropertiesFileNameArgument = new Argument<>(String.class, "d", "databaseProp", "databaseProp", false, "");

        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(trackingIDArgument);
        arguments.add(persistencePropertiesFileNameArgument);

        App app = new App(arguments);
        app.setValues(args);
        if (app.exceptionThrown) {
            System.exit(PARSE_ARGS_FAIL);
        }
        if (args.length < 2 || args.length > 4) {
            System.exit(PARSE_ARGS_FAIL);
        }
        if (!app.parsingOK()) {
            App.logger.error("parameters not parsed correctly", app.usage());
            System.exit(PARSE_ARGS_FAIL);
        }
        logger.info("parameters parsed correctly");

        if (!app.instantiateHibernateManager(persistencePropertiesFileNameArgument.getRawValue())) {
            System.exit(PARSE_ARGS_FAIL);
        }

        app.validationRemover = new ValidationRemover(app.hibernateManager);
        try {
            logger.info("removing validations");
            app.validationRemover.run(trackingIDArgument.getValue());
        } catch (Exception ex) {
            logger.error("error running validation remover");
            System.exit(DB_ERROR_CONNECTION);
        }



    }

    @Override
    public String example() {
        return "java -jar app.jar -t 1 -d live.mysql.properties";
    }

    @Override
    public int getMinimumOptionalParameters() {
        return 1;
    }
}
