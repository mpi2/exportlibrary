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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.mail.MessagingException;
import javax.persistence.*;
import javax.xml.bind.JAXBException;
import org.apache.commons.configuration.ConfigurationException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.Incarnator;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameter;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressPipeline;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressPipelineContainer;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressProcedure;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.XMLValidationResourcesCollector;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress.Resource;
import org.mousephenotype.dcc.utils.net.email.EMAILUtils;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.springframework.mail.MailException;

/**
 *
 * @author julian
 */
public class ImpressBrowser extends Incarnator<ImpressPipelineContainer> {

    private XMLValidationResourcesCollector xmlValidationResourcesCollector;

    public ImpressPipelineContainer getImpressPipelineContainer() {
        if (this.getObject() == null) {
            object = new ImpressPipelineContainer();
        }
        return this.getObject();

    }

    public ImpressBrowser(String contextPath, String xmlFilename) throws Exception {
        super(contextPath, xmlFilename);
        this.update();
    }

    public ImpressBrowser(HibernateManager hibernateManager) throws Exception {
        super(hibernateManager);
        this.update();
    }

    public ImpressBrowser(String persistenceUnitName, Properties properties) throws Exception{
        super(persistenceUnitName, properties);
        this.update();
    }

    public boolean latest() {
        String whenLastModifiedFromWS = Resource.getWhenLastModified();
        DateTime whenLastModifiedDateTime = null;
        try {
            whenLastModifiedDateTime = new org.joda.time.DateTime(DatatypeConverter.parseDateTime(whenLastModifiedFromWS));
        } catch (Exception ex) {
            logger.error("error parsing date from impress web services", ex);
        }
        if (whenLastModifiedDateTime == null) {
            return false; // if this is null then you have to update!
        } else {
            List<ImpressPipelineContainer> query = this.hibernateManager.query("from ImpressPipelineContainer", ImpressPipelineContainer.class);
            if (query.size() == 1) {
                if (query.get(0).getQueried() == null) {
                    return false; //If the Queried date in the database is null then assume data empty
                }
                DateTime whenLastModifiedFromDB = new org.joda.time.DateTime(query.get(0).getQueried()).withZone(DateTimeZone.UTC);
                if (whenLastModifiedFromDB == null || whenLastModifiedDateTime.isAfter(whenLastModifiedFromDB)) {
                    return false;
                }
            } else {
                logger.error("{} impressPipelines in the database", query.size());
                return false; // If there is nothing then you need something to validate against!
            }
        }
        return true;
    }

    private void sendMessage(String content, String subject) {
        try {
            subject = "[" + InetAddress.getLocalHost().getHostName() + "] " + subject;
        } catch (UnknownHostException ex) {
            subject = subject + "on undetermined host";
        }
        EMAILUtils eMAILUtils = EMAILUtils.getEMAILUtils();
        try {
            eMAILUtils.sendEmail(new String[]{"itdcc@har.mrc.ac.uk"}, null, null, "dcc.logging@har.mrc.ac.uk", subject, content, null);
        } catch (MailException | MessagingException ex) {
            logger.error("error sending email", ex);

        }
    }

    public final void update() throws IllegalArgumentException{
        Properties properties = this.hibernateManager.getProperties();
        String persistenceUnitname = this.hibernateManager.getPersistencename();
        boolean exceptionThrown = false;
        String message = null;
        if (!latest()) {
            logger.info("updating xmlvalidationresources");
            properties.put("hibernate.hbm2ddl.auto", "create");
            HibernateManager updater = new HibernateManager(properties, persistenceUnitname);
            this.xmlValidationResourcesCollector = new XMLValidationResourcesCollector(null);
            this.xmlValidationResourcesCollector.setHibernateManager(updater);

            try {
                this.xmlValidationResourcesCollector.execute();
            } catch (FileNotFoundException ex) {
                logger.error("cannot find mgi configuration file", ex);
                exceptionThrown = true;
                message = ex.toString();
            } catch (IOException ex) {
                logger.error("IOException", ex);
                exceptionThrown = true;
                message = ex.toString();
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ConfigurationException ex) {
                logger.error("exception thrown", ex);
                exceptionThrown = true;
                message = ex.toString(); 
            } finally {
                if (exceptionThrown) {
                    this.sendMessage(message, "error updating xmlvalidationresources " + DatatypeConverter.printDateTime(DatatypeConverter.now()));
                    throw new IllegalArgumentException("There was a problem setting up the resources to carry out the validation \n\nWhat follows is the Exception:\n"+message);
                } else {
                    this.sendMessage("", "xmlvalidationresources updated successfully at " + DatatypeConverter.printDateTime(DatatypeConverter.now()));
                }
                properties.remove("hibernate.hbm2ddl.auto");
                updater.close();
            }

        }
    }

    public boolean loadPipeline(String pipelineKey) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException, LockTimeoutException, PersistenceException, JAXBException, FileNotFoundException, Exception {
        logger.info("querying for the pipeline {}", pipelineKey);

        List<ImpressPipeline> impressPipelines = this.hibernateManager.query("from ImpressPipeline i where i.pipelineKey =:pipelineKey",
                ImmutableMap.<String, Object>builder().put("pipelineKey", pipelineKey).build(), ImpressPipeline.class);
        if (impressPipelines != null && impressPipelines.size() > 0 && impressPipelines.size() < 2) {
            this.getImpressPipelineContainer().getPipeline().add(impressPipelines.get(0));
            return true;
        }
        return false;
    }

    public ImpressParameter get(ImpressProcedure impressProcedure, String parameterID, ImpressParameterType impressParameterType) {
        for (ImpressParameter impressParameter : impressProcedure.getImpressParameter()) {
            if (impressParameter.getParameterKey().equals(parameterID) && impressParameter.getType().equals(impressParameterType)) {
                return impressParameter;
            }
        }
        return null;
    }

    public ImpressPipeline getImpressPipeline(String pipelineKey) {
        for (ImpressPipeline impressPipeline : this.getImpressPipelineContainer().getPipeline()) {
            if (impressPipeline.getPipelineKey().equals(pipelineKey)) {
                return impressPipeline;
            }
        }
        return null;
    }

    public ImpressProcedure getImpressProcedure(String pipelineKey, String procedureKey) {
        for (ImpressPipeline impressPipeline : this.getImpressPipelineContainer().getPipeline()) {
            if (impressPipeline.getPipelineKey().equals(pipelineKey)) {
                for (ImpressProcedure impressProcedure : impressPipeline.getImpressProcedure()) {
                    if (impressProcedure.getProcedureKey().equals(procedureKey)) {
                        return impressProcedure;
                    }
                }
                break;
            }
        }
        return null;
    }

    public Set<String> getRequiredParameterIDs(String pipelineKey, String procedureKey) {
        Set<String> requiredParameterIDs = null;
        ImpressProcedure impressProcedure = this.getImpressProcedure(pipelineKey, procedureKey);
        if (impressProcedure != null) {
            requiredParameterIDs = Sets.newHashSetWithExpectedSize(impressProcedure.getImpressParameter().size());
            for (ImpressParameter impressParameter : impressProcedure.getImpressParameter()) {
                if (impressParameter.isIsRequired()) {
                    requiredParameterIDs.add(impressParameter.getParameterKey());
                }
            }
        }
        return requiredParameterIDs;
    }

    public Set<String> getAllParameterIDs(String pipelineKey, String procedureKey) {
        Set<String> allparameterIDs = null;
        ImpressProcedure impressProcedure = this.getImpressProcedure(pipelineKey, procedureKey);
        if (impressProcedure != null) {
            allparameterIDs = Sets.newHashSetWithExpectedSize(impressProcedure.getImpressParameter().size());
            for (ImpressParameter impressParameter : impressProcedure.getImpressParameter()) {
                allparameterIDs.add(impressParameter.getParameterKey());
            }
        }
        return allparameterIDs;
    }
}
