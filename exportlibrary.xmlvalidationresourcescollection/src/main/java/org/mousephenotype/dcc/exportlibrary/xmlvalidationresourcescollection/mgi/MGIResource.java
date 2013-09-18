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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.mgi;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.persistence.*;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.converters.DatatypeConverter;



import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.mgi.MgiStrains;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.mousephenotype.dcc.utils.net.ftp.FTPUtils;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class MGIResource {
    //http://code.google.com/p/guava-libraries/wiki/CachesExplained

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(MGIResource.class);
    private static final String PERSISTENCE_UNIT_NAME = "org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external";
    public static final String EXTERNAL_RESOURCES_FOLDER = "external_files/";
    private static final String filename = "external_files/MGI_Strain.rpt";
    private FTPUtils ftpUtils;
    private HibernateManager hibernateManager;
    private static final String queryStrain = "SELECT strainID FROM MGIStrain s  WHERE s.strainID = :STRAINID";
    private final Properties persistenceProperties;
    private static Properties ftpProperties;
    private static final String ftpLocalPropertiesFilename = "mgi.txt";
    private MGIStrainParser mGIStrainParser;

    static {
        if (!(new File(EXTERNAL_RESOURCES_FOLDER).exists())) {
            new File(EXTERNAL_RESOURCES_FOLDER).mkdir();
        }
        try {

            Reader reader = new Reader(ftpLocalPropertiesFilename);
            ftpProperties = reader.getProperties();
        } catch (ConfigurationException ex) {
            logger.error("ftp configuration file: {} not found", ftpLocalPropertiesFilename, ex);
        }

    }

    public MGIResource(Properties properties) throws HibernateException {
        this.persistenceProperties = properties;
    }

    public MGIResource(HibernateManager hibernateManager) throws HibernateException {
        this.persistenceProperties = null;
        this.hibernateManager = hibernateManager;
    }

    protected void init(boolean downloadSource, boolean loadCache) throws FileNotFoundException, IOException {
        if (downloadSource) {
            this.triggerDownload();
            this.loadCache(downloadSource);
        }
        if (loadCache && !downloadSource) {
            this.loadCache(downloadSource);
        }
        this.startResource();
    }

    public void run(Calendar now) throws FileNotFoundException, IOException {
        this.triggerDownload();
        this.work(now);

    }

    private void triggerDownload() throws IOException {
        logger.info("downloading {}/{}", ftpProperties.getProperty("url"), ftpProperties.getProperty("fromFile"));
        ftpUtils = new FTPUtils(ftpProperties.getProperty("url"), Integer.valueOf(ftpProperties.getProperty("port")),
                ftpProperties.getProperty("username"), ftpProperties.getProperty("password"), FTPUtils.fileTypes.BINARY_FILE_TYPE, FTPUtils.fileTransferModes.STREAM_TRANSFER_MODE);
        ftpUtils.download(ftpProperties.getProperty("fromFile"), filename);
        ftpUtils.close();
    }

    private void work(Calendar now) throws FileNotFoundException {
        logger.info("new MGIStrainParser");
        this.mGIStrainParser = new MGIStrainParser(filename, now);
        logger.info("running mgiStrainParser");
        this.mGIStrainParser.run();
    }

    private void loadCache(boolean downloadSource) throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, RuntimeException, FileNotFoundException, HibernateException {
        logger.info("loading cache from {}", filename);
        //
        if (downloadSource) {
            persistenceProperties.put("hibernate.hbm2ddl.auto", "create");
        }
        this.hibernateManager = new HibernateManager(persistenceProperties, PERSISTENCE_UNIT_NAME);
        //
        this.work(DatatypeConverter.now());

        logger.info("starting store for {} entries from the mgi list", this.mGIStrainParser.getMGIStrains().getMgiStrain().size());
        //  
        this.hibernateManager.persist(this.mGIStrainParser.getMGIStrains());

    }

    public HibernateManager getHibernateManager() {
        return this.hibernateManager;
    }

    private void startResource() throws FileNotFoundException, IOException {
        persistenceProperties.remove("hibernate.hbm2ddl.auto");
        logger.info("starting persistence manager ");
        if (hibernateManager == null) {
            hibernateManager = new HibernateManager(persistenceProperties, PERSISTENCE_UNIT_NAME);
        }
    }

    public MgiStrains getStrains() {
        return this.mGIStrainParser.getMGIStrains();
    }

    public boolean findStrain(String strainID) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException,
            LockTimeoutException, PersistenceException {
        List<String> query = hibernateManager.query(queryStrain, ImmutableMap.<String, Object>builder().put("STRAINID", strainID).build(), String.class);
        return query != null && query.size() > 0;
    }

    public void close() {
        hibernateManager.close();
    }
}
