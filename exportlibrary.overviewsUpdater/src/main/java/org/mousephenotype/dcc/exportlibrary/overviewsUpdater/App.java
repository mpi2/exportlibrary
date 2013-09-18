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
package org.mousephenotype.dcc.exportlibrary.overviewsUpdater;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.io.console.Argument;
import org.mousephenotype.dcc.utils.io.console.ConsoleReader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends ConsoleReader {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static HibernateManager mysqlHibernateManager;
    private static HibernateManager mysqlValidationHibernateManager;
    private static Reader reader;
    private static final String DB_PROPERTIES_FILENAME = "live.mysql.properties";
    private static final String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private static final String DB_PROPERTIES_FILENAME_4_VALIDATION_RESOURCES = "live.validation.mysql.properties";
    private static final String PERSISTENCE_UNITNAME_VALIDATION_RESOURCES = "org.mousephenotype.dcc.exportlibrary.external";
    private Orchestrator orchestrator;
    private static Properties imitsClientProperties;

    public static String sayHello() {
        logger.info("running hello world");
        return "hello";
    }

    public App(List<Argument<?>> arguments) {
        super(arguments);
    }

    private void setup(String phenodccRawPropertiesFilename, String xmlvalidationresourcesFilename) {
        Properties properties = null;
        try {
            reader = new Reader(phenodccRawPropertiesFilename == null ? DB_PROPERTIES_FILENAME : phenodccRawPropertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
        }
        properties = reader.getProperties();

        try {
            mysqlHibernateManager = new HibernateManager(properties, PERSISTENCE_UNITNAME);
        } catch (HibernateException ex) {
            logger.error("", ex);
            System.exit(-1);
        }
        //
        try {
            reader = new Reader("conf/external.properties");
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            System.exit(-1);
        }
        imitsClientProperties = reader.getProperties();
        //
        try {
            reader = new Reader(xmlvalidationresourcesFilename == null ? DB_PROPERTIES_FILENAME_4_VALIDATION_RESOURCES : xmlvalidationresourcesFilename);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
        }
        properties = reader.getProperties();

        try {
            mysqlValidationHibernateManager = new HibernateManager(properties, PERSISTENCE_UNITNAME_VALIDATION_RESOURCES);
        } catch (HibernateException ex) {
            logger.error("", ex);
            System.exit(-1);
        }
        this.orchestrator = new Orchestrator(mysqlHibernateManager, mysqlValidationHibernateManager, imitsClientProperties.getProperty("imits.username"), imitsClientProperties.getProperty("imits.password"));
    }

    private void tearDown() {
        if (mysqlHibernateManager != null) {
            try {
                mysqlHibernateManager.close();
            } catch (HibernateException ex) {
                logger.error("", ex);
                System.exit(-1);
            }
        }
        if (mysqlValidationHibernateManager != null) {
            try {
                mysqlValidationHibernateManager.close();
            } catch (HibernateException ex) {
                logger.error("", ex);
                System.exit(-1);
            }
        }
    }

    public void run() {
        try {
            this.orchestrator.run();
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
            this.tearDown();
        }
    }

    public static void main(String[] args) {

        Argument<String> phenodccRawPropertiesFilenameArgument = new Argument<>(String.class, "r", "trackingID", "trackingID", false, "the id for the file by the tracker");
        Argument<String> xmlvalidationresourcesFilenameArgument = new Argument<>(String.class, "x", "databaseProp", "databaseProp", false, "");

        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(phenodccRawPropertiesFilenameArgument);
        arguments.add(xmlvalidationresourcesFilenameArgument);

        App app = new App(arguments);
        if (args != null) {
            app.setValues(args);
        }
        if (app.exceptionThrown) {
            System.exit(-1);
        }

        if (args.length < 2) {
            app.setup(null, null);
        } else {

            app.setup(phenodccRawPropertiesFilenameArgument.getRawValue(), xmlvalidationresourcesFilenameArgument.getRawValue());
        }
        app.run();
        System.exit(0); //if everything goes OK for integration tests.
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
