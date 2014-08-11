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
import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameter;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressPipeline;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressProcedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ParserTest {

    protected static final Logger logger = LoggerFactory.getLogger(ParserTest.class);
    private static final String pipelineKey = "IMPC_001";
    private static final String procedureKey = "IMPC_BLK_001";
    //private static final String parameterKey = "IMPC_OFD_005_001";
    private static final String parameterKey = "IMPC_PAT_001_001";
    private static ImpressPipeline impressPipeline;
    private static ImpressProcedure impressProcedure;
    private static ImpressParameter impressParameter;

    @BeforeClass
    public static void setup() {
        logger.info("loading pipeline {}", pipelineKey);
        try {
            impressPipeline = Parser.getImpressPipeline(pipelineKey);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(impressPipeline);

        logger.info("{} loaded", pipelineKey);
        logger.info("loading procedure {}", procedureKey);
        try {
            impressProcedure = Parser.getImpressProcedure(procedureKey, pipelineKey);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(impressProcedure);
        logger.info("{} loaded", procedureKey);

        logger.info("loading parameter {}", parameterKey);
        try {
            impressParameter = Parser.getImpressParameter(parameterKey);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(impressParameter);
        logger.info("{} loaded", parameterKey);

    }

    @Test
    public void testlinkProcedures2pipeline() {
        try {
            Parser.linkProcedures2pipeline(impressPipeline);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testlinkParameters2procedure() {
        try {
            Parser.linkParameters2procedure(impressProcedure);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testlinkParameterOptions2parameter() {
        try {
            Parser.linkParameterOptions2parameter(impressParameter);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testlinkOntologyParameterOptions2Parameter() {
        ImpressParameter IMPC_PAT_001_001 = new ImpressParameter();
        IMPC_PAT_001_001.setParameterKey("IMPC_PAT_001_001");
        
        try {
            Parser.linkOntologyParameterOptions2Parameter(IMPC_PAT_001_001);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }
}
