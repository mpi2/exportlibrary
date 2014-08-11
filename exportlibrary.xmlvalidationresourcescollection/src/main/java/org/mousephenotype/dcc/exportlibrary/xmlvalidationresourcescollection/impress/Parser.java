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
import java.util.HashMap;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.*;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress.utils.ArrayOfHashOfString;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress.utils.HashOfString;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress.utils.Instantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class Parser {

    protected static final Logger logger = LoggerFactory.getLogger(Parser.class);

    public static ImpressPipeline getImpressPipeline(String pipelineKey) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashOfString hashOfString = new HashOfString((org.apache.xerces.dom.ElementNSImpl) Resource.getPipeline(pipelineKey));
        ImpressPipeline impressPipeline = new ImpressPipeline();
        Instantiator.getInstance(ImpressPipeline.class, impressPipeline, hashOfString.getHashMap());
        return impressPipeline;

    }

    public static ImpressProcedure getImpressProcedure(String procedureKey, String pipelineKey) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashOfString hashOfString = new HashOfString((org.apache.xerces.dom.ElementNSImpl) Resource.getProcedure(procedureKey, pipelineKey));
        ImpressProcedure impressProcedure = new ImpressProcedure();
        Instantiator.getInstance(ImpressProcedure.class, impressProcedure, hashOfString.getHashMap());
        return impressProcedure;
    }

    public static ImpressParameter getImpressParameter(String parameterKey) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashOfString hashOfString = new HashOfString((org.apache.xerces.dom.ElementNSImpl) Resource.getParameter(parameterKey));
        ImpressParameter impressParameter = new ImpressParameter();
        Instantiator.getInstance(ImpressParameter.class, impressParameter, hashOfString.getHashMap());
        return impressParameter;
    }

    public static void linkProcedures2pipeline(ImpressPipeline impressPipeline) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ArrayOfHashOfString procedures = new ArrayOfHashOfString((org.apache.xerces.dom.ElementNSImpl) Resource.getProcedures(impressPipeline.getPipelineKey()));
        logger.info("{} procedures for pipeline {}", procedures.getHashMapArrayList().size(), impressPipeline.getPipelineName());
        ImpressProcedure impressProcedure = new ImpressProcedure();
        for (HashMap<String, String> rawProcedure : procedures.getHashMapArrayList()) {
            Instantiator.getInstance(ImpressProcedure.class, impressProcedure, rawProcedure);
            impressPipeline.getImpressProcedure().add(impressProcedure);
            impressProcedure = new ImpressProcedure();
        }
    }

    public static void linkParameters2procedure(ImpressProcedure impressProcedure) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ArrayOfHashOfString parameters = new ArrayOfHashOfString((org.apache.xerces.dom.ElementNSImpl) Resource.getParameters(impressProcedure.getProcedureKey()));
        logger.info("{} parameters for procedure {}", parameters.getHashMapArrayList().size(), impressProcedure.getProcedureName());
        ImpressParameter impressParameter = new ImpressParameter();
        for (HashMap<String, String> rawParameter : parameters.getHashMapArrayList()) {
            Instantiator.getInstance(ImpressParameter.class, impressParameter, rawParameter);
            impressProcedure.getImpressParameter().add(impressParameter);
            impressParameter = new ImpressParameter();
        }
    }

    public static void linkParameterOptions2parameter(ImpressParameter impressParameter) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ArrayOfHashOfString parameterOptions = new ArrayOfHashOfString((org.apache.xerces.dom.ElementNSImpl) Resource.getParameterOptions(impressParameter.getParameterKey()));
        logger.trace("{} parameterOptions for parameter {}", parameterOptions.getHashMapArrayList().size(), impressParameter.getParameterName());
        ImpressParameterOption impressParameterOption = new ImpressParameterOption();
        for (HashMap<String, String> rawParameterOption : parameterOptions.getHashMapArrayList()) {
            Instantiator.getInstance(ImpressParameterOption.class, impressParameterOption, rawParameterOption);
            impressParameter.getImpressParameterOption().add(impressParameterOption);
            impressParameterOption = new ImpressParameterOption();
        }
    }

    public static void linkOntologyParameterOptions2Parameter(ImpressParameter impressParameter) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object rawOptions = Resource.getParameterOntologyOptions(impressParameter.getParameterKey());
        ArrayOfHashOfString ontologyParameterOptions = new ArrayOfHashOfString((org.apache.xerces.dom.ElementNSImpl) rawOptions);
        logger.trace("{} ontologyParameterOptions for parameter {}", ontologyParameterOptions.getHashMapArrayList().size(), impressParameter.getParameterName());
        ImpressOntologyParameterOption impressOntologyParameterOption = new ImpressOntologyParameterOption();
        for (HashMap<String, String> rawOntologyParameterOption : ontologyParameterOptions.getHashMapArrayList()) {
            Instantiator.getInstance(ImpressOntologyParameterOption.class, impressOntologyParameterOption, rawOntologyParameterOption);
            impressParameter.getImpressOntologyParameterOption().add(impressOntologyParameterOption);
            impressOntologyParameterOption = new ImpressOntologyParameterOption();
        }

    }

    public static void linkParameterIncrements2parameter(ImpressParameter impressParameter) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ArrayOfHashOfString parameterIncrements = new ArrayOfHashOfString((org.apache.xerces.dom.ElementNSImpl) Resource.getParameterIncrements(impressParameter.getParameterKey()));
        logger.trace("{} parameterOptions for parameter {}", parameterIncrements.getHashMapArrayList().size(), impressParameter.getParameterName());
        ImpressParameterIncrement impressParameterIncrement = new ImpressParameterIncrement();
        for (HashMap<String, String> rawParameterIncrement : parameterIncrements.getHashMapArrayList()) {
            Instantiator.getInstance(ImpressParameterIncrement.class, impressParameterIncrement, rawParameterIncrement);
            impressParameter.getImpressParameterIncrement().add(impressParameterIncrement);
            impressParameterIncrement = new ImpressParameterIncrement();
        }
    }
}
