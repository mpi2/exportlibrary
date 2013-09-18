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
package org.mousephenotype.dcc.exportlibrary.fullTraverser;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
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
    private static final String persistenceUnitname = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private HibernateManager hibernateManager;
    private Reader reader;
    private FullTraverser fullTraverser;

    public static String sayHello() {
        logger.info("running hello world");
        return "hello";
    }

    private App(List<Argument<?>> arguments) {
        super(arguments);
    }

    private void instantiateHibernateManager(String propertiesFilename) {
        Properties properties;
        logger.info("loading hibernate configuration from {}", propertiesFilename);
        try {
            reader = new Reader(propertiesFilename);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            System.exit(-1);
        }
        properties = reader.getProperties();

        hibernateManager = new HibernateManager(properties, persistenceUnitname);
    }

    
    public void run(String persistencePropertiesFileName, Calendar date){
        this.instantiateHibernateManager(persistencePropertiesFileName);
        this.fullTraverser = new FullTraverser(hibernateManager);
        this.fullTraverser.run(date);
    }
    
    public static void main(String[] args) {
        Argument<String> persistencePropertiesFileNameArgument = new Argument<>(String.class, "p", "databaseProp", "databaseProp", false, "database properties filename");
        Argument<Calendar> dateArgument = new Argument<>(Calendar.class, "d", "date", "date", true, "file type can be specimen or procedure");
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(dateArgument);
        arguments.add(persistencePropertiesFileNameArgument);

        App app = new App(arguments);
        app.setValues(args);

        if (app.exceptionThrown) {
            System.exit(-1);
        }
        if (persistencePropertiesFileNameArgument.getRawValue() != null) {
            if (new File(persistencePropertiesFileNameArgument.getRawValue()).exists()) {
                app.run(persistencePropertiesFileNameArgument.getRawValue(), null);
                
            } else {
                logger.error("file {} does not exist", persistencePropertiesFileNameArgument.getRawValue());
                System.exit(-1);
            }
        } else {
            logger.error("{}", app.example());
            System.exit(-1);
        }

    }

    @Override
    public String example() {
        return "";
    }

    @Override
    public int getMinimumOptionalParameters() {
        return 1;
    }
}
