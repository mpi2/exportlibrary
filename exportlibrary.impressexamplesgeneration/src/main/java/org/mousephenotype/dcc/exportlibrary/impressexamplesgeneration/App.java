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
package org.mousephenotype.dcc.exportlibrary.impressexamplesgeneration;

import java.io.File;
import java.util.Properties;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    //
    private static HibernateManager impressHibernateManager;
    private static Reader reader;
    private static final String persistenceUnitname = "org.mousephenotype.dcc.exportlibrary.xmlvalidation.external";
    private static final String DEFAULT_DB_CONNECTION = "xmlvalidationresources.live.properties";
    //
    private ExampleGenerator exampleGenerator;

    public static String sayHello() {
        logger.info("running hello world");
        return "hello";
    }

    private App() {
    }

    private void setup(String connection) throws HibernateException {
        Properties properties = null;
        try {
            reader = new Reader(connection == null ? DEFAULT_DB_CONNECTION : connection);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
        }

        properties = reader.getProperties();
        properties.remove("hibernate.hbm2ddl.auto");
        impressHibernateManager = new HibernateManager(properties, persistenceUnitname);
    }

    public void run(String connection) {
        try {
            this.setup(connection);

        } catch (Exception ex) {
            logger.error("cannot connect to the database");
            System.exit(-1);
        }
        this.exampleGenerator = new ExampleGenerator(impressHibernateManager);
        try {
            this.exampleGenerator.generateExamples();
        } catch (JAXBException ex) {
            logger.error("cannot connect to the database");
        } finally {
            impressHibernateManager.close();
        }
    }

    public static void main(String[] args) {
        App app = new App();
        String connection = null;
        if (args != null && args.length > 0) {
            if (new File(args[0]).exists()) {
                connection = args[0];
            } else {
                logger.error("file {} is not accessible", args[0]);
            }
        }

        app.run(connection);

    }
}
