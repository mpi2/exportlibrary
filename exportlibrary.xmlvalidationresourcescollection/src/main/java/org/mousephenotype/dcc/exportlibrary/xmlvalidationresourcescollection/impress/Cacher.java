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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress;

import java.lang.reflect.InvocationTargetException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.converters.DatatypeConverter;

import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameter;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressPipeline;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressPipelineContainer;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressProcedure;

import org.mousephenotype.impress.soap.server.ArrayOfString;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class Cacher {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Cacher.class);
    private ImpressPipelineContainer impressPipelineContainer;

    public void loadFromWS() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        logger.info("loading impress from ws");

        this.impressPipelineContainer = new ImpressPipelineContainer();
        this.getImpressPipelineContainer().setQueried(DatatypeConverter.now());

        logger.debug("loading pipelineKeys from IMPReSS ws");
        ArrayOfString pipelineKeys = Resource.getPipelineKeys();
        logger.debug("pipelinekeys loaded");
        logger.info("{} pipelines loaded", pipelineKeys.getItem().size());
        for (String pipelineKey : pipelineKeys.getItem()) {
            logger.debug("loading attributes for pipeline {}", pipelineKey);
            getImpressPipelineContainer().getPipeline().add(Parser.getImpressPipeline(pipelineKey));
            logger.debug("pipelinekey {} attributes loaded");
        }
        logger.info("querying for pipelines");

        for (ImpressPipeline impressPipeline : getImpressPipelineContainer().getPipeline()) {
            logger.debug("linking procedures to pipelinekey {}", impressPipeline.getPipelineKey());
            Parser.linkProcedures2pipeline(impressPipeline);
            logger.debug("pipelinekey {} procedures loaded", impressPipeline.getPipelineKey());
            for (ImpressProcedure impressProcedure : impressPipeline.getImpressProcedure()) {
                logger.debug("linking parameters 2 (pipelinekey,procedurekey) ({},{})", impressPipeline.getPipelineKey(), impressProcedure.getProcedureKey());
                Parser.linkParameters2procedure(impressProcedure);
                logger.debug("parameters loaded");
                for (ImpressParameter impressParameter : impressProcedure.getImpressParameter()) {
                    if (impressParameter.isIsOption()) {
                        logger.debug("linking parameterOPtions  2 (procedurekey, parameterkey) ({},{})", impressProcedure.getProcedureKey(), impressParameter.getParameterKey());
                        Parser.linkParameterOptions2parameter(impressParameter);
                        logger.debug("parameteroptions loaded");
                    } else {
                        logger.debug("{} has not parameter options", impressParameter.getParameterKey());
                    }
                    if (impressParameter.isIsIncrement()) {
                        logger.debug("linking parameterIncrements  2 (procedurekey, parameterkey) ({},{})", impressProcedure.getProcedureKey(), impressParameter.getParameterKey());
                        Parser.linkParameterIncrements2parameter(impressParameter);
                        logger.debug("parameterIncrements loaded");
                    } else {
                        logger.debug("{} has not parameterIncrements", impressParameter.getParameterKey());
                    }
                    if (impressParameter.getType().equals(ImpressParameterType.ONTOLOGY_PARAMETER)) {
                        Parser.linkOntologyParameterOptions2Parameter(impressParameter);
                    }
                }
            }
        }
    }

    /**
     * @return the impressPipelineContainer
     */
    public ImpressPipelineContainer getImpressPipelineContainer() {
        return impressPipelineContainer;
    }
}
