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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.joda.time.DateTimeZone;



import org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress.Cacher;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.mgi.MGIResource;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.statuscodes.StatusCodesReader;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.io.console.Argument;
import org.mousephenotype.dcc.utils.io.console.ConsoleReader;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class XMLValidationResourcesCollector extends ConsoleReader {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLValidationResourcesCollector.class);
    //
    private final Calendar now;
    private HibernateManager outputHibernateManager;
    private HibernateManager localImitsHibernateManager;
    public static final String OUTPUT_PERSISTENCEUNITNAME = "org.mousephenotype.dcc.exportlibrary.external";
    public static final String LOCALIMITS_PERSISTENCEUNITNAME = "org.mousephenotype.dcc.imitsdatastructure";
    private static final String STATUS_CODES_CSV_FILENAME = "statusCodes.csv";
    //
    private Cacher impressCacher;
    private MGIResource mGIResource;

    static {
        String derbySystemHome = XMLValidationResourcesCollector.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.setProperty("derby.system.home", derbySystemHome.substring(0, derbySystemHome.lastIndexOf(File.separator)));
        //
        System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
    }

    public enum engine {

        MYSQL("xmlvalidationresources.mysql.properties"), DERBY("xmlvalidationresources.derby.properties"), HSQLDB("xmlvalidationresources.hsqldb.properties"),
        LOCAL_IMITS("imits.live.mysql.properties");
        String configurationFile;

        engine(String configurationFile) {
            this.configurationFile = configurationFile;
        }
    };

    public XMLValidationResourcesCollector(List<Argument<?>> arguments) {
        super(arguments);
        this.now = org.joda.time.DateTime.now(DateTimeZone.UTC).toGregorianCalendar();
    }

    private void instantiatelocalImitsHibernateManager() throws FileNotFoundException, HibernateException, IOException, ConfigurationException {
        this.localImitsHibernateManager = XMLValidationResourcesCollector.getHibernateManager(engine.LOCAL_IMITS, LOCALIMITS_PERSISTENCEUNITNAME);
    }

    public void run() throws FileNotFoundException, IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ConfigurationException {

        

        logger.info("loading {} with status codes", STATUS_CODES_CSV_FILENAME);
        StatusCodesReader statusCodesReader = new StatusCodesReader(STATUS_CODES_CSV_FILENAME);
        statusCodesReader.read(now);
        logger.info("persisting statuscodes");
        this.outputHibernateManager.persist(statusCodesReader.getStatuscodes());
        //
        this.outputHibernateManager.getEntityManager().close();
        //

        logger.info("collecting from MGI");
        this.mGIResource = new MGIResource(this.outputHibernateManager);
        this.mGIResource.run(now);
        logger.info("persisting MGI");
        
        this.outputHibernateManager.persist(this.mGIResource.getStrains());
        this.outputHibernateManager.getEntityManager().close();
        //
        logger.info("collecting from impress");
        this.impressCacher = new Cacher();
        this.impressCacher.loadFromWS();
        logger.info("persisting impress");
        this.outputHibernateManager.persist(this.impressCacher.getImpressPipelineContainer());
        //

        //
        this.outputHibernateManager.close();
        logger.info("finished");
    }

    public static HibernateManager getHibernateManager(engine _engine, String persistenceUnitName) throws FileNotFoundException, HibernateException, IOException, ConfigurationException {

        Reader reader = new Reader(_engine.configurationFile);
        Properties properties = reader.getProperties();
        properties.remove("hibernate.hbm2ddl.auto");
        logger.info("instantiating hibernate manager to retrieve data from imits");
        HibernateManager hibernateManager = new HibernateManager(properties, persistenceUnitName);
        return hibernateManager;
    }

    public static boolean resourcesAvailable(engine _engine) {
        return false;
    }

    public static void main(String[] args) {

        Argument<Boolean> derbyArgument = new Argument<>(Boolean.class, "d", "derby", "derby default properties", true, "generate a derby database");
        Argument<Boolean> mysqlArgument = new Argument<>(Boolean.class, "m", "mysql", "mysql default properties", true, "populate the default mysql database");
        Argument<String> externalArgument = new Argument<>(String.class, "e", "external", "external properties file", true, "use the external properties filename");
        Argument<Boolean> cleanArgument = new Argument<>(Boolean.class, "c", "clean", "just generate ddl, no contents", true, "just generate ddl, no contents");

        //
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(derbyArgument);
        arguments.add(mysqlArgument);
        arguments.add(externalArgument);
        arguments.add(cleanArgument);

        //
        XMLValidationResourcesCollector xMLValidationResourcesCollector = new XMLValidationResourcesCollector(arguments);
        xMLValidationResourcesCollector.setValues(args);
        //
/*
         if (usernameArgument.getValue() == null || passwordArgument.getValue() == null) {
         logger.error("need to supply imits credentials");
         System.exit(-1);
         }*/
        HibernateManager hibernateManager = null;
        if (args.length < 1 || (derbyArgument.getValue() != null && derbyArgument.getValue())) {
            try {
                hibernateManager = XMLValidationResourcesCollector.getHibernateManager(engine.DERBY, OUTPUT_PERSISTENCEUNITNAME);

            } catch (IOException | HibernateException | ConfigurationException ex) {
                logger.error("", ex);
                System.exit(-1);
            }
        }

        if (mysqlArgument.getValue() != null && mysqlArgument.getValue()) {
            try {
                hibernateManager = XMLValidationResourcesCollector.getHibernateManager(engine.MYSQL, OUTPUT_PERSISTENCEUNITNAME);
            } catch (IOException | HibernateException | ConfigurationException ex) {
                logger.error("", ex);
                System.exit(-1);
            }
        }
        if (externalArgument.getValue() != null && !externalArgument.getValue().equals("")) {
            try {
                logger.info("instantiating hibernate manager to serialize results from {}", externalArgument.getValue());
                Reader reader = new Reader(externalArgument.getValue());
                Properties properties = reader.getProperties();
                hibernateManager = new HibernateManager(properties, OUTPUT_PERSISTENCEUNITNAME);
            } catch (HibernateException | ConfigurationException ex) {
                logger.error("", ex);
                System.exit(-1);
            }
        }

        if (cleanArgument.getRawValue() != null && cleanArgument.getValue()) {
            hibernateManager.close();
            System.exit(0);
            return;
        }


        xMLValidationResourcesCollector.setHibernateManager(hibernateManager);
        try {
            xMLValidationResourcesCollector.run();
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            System.exit(-1);

        } catch (HibernateException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ConfigurationException ex) {
            logger.error("", ex);
            System.exit(-1);
        } catch (IOException ex) {
            logger.error("", ex);
            System.exit(-1);
        }




    }

    @Override
    public String example() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMinimumOptionalParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @param hibernateManager the hibernateManager to set
     */
    public void setHibernateManager(HibernateManager hibernateManager) {
        this.outputHibernateManager = hibernateManager;
    }
}
