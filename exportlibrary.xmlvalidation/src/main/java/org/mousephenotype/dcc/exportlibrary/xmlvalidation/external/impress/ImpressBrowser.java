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
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.JAXBException;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.Incarnator;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameter;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressPipeline;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressPipelineContainer;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressProcedure;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

/**
 *
 * @author julian
 */
public class ImpressBrowser extends Incarnator<ImpressPipelineContainer> {

    public ImpressPipelineContainer getImpressPipelineContainer() {
        if (this.getObject() == null) {
            object = new ImpressPipelineContainer();
        }
        return this.getObject();

    }

    public ImpressBrowser(String contextPath, String xmlFilename) throws Exception {
        super(contextPath, xmlFilename);
    }

    public ImpressBrowser(HibernateManager hibernateManager) {
        super(hibernateManager);
    }

    public ImpressBrowser(String persistenceUnitName, Properties properties) {
        super(persistenceUnitName, properties);
    }

    public boolean loadPipeline(String pipelineKey) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException,
            LockTimeoutException, PersistenceException, JAXBException, FileNotFoundException, Exception {
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
