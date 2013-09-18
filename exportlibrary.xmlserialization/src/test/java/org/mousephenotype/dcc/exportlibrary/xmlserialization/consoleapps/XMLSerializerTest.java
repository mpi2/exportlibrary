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
package org.mousephenotype.dcc.exportlibrary.xmlserialization.consoleapps;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class XMLSerializerTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLSerializerTest.class);

    @Ignore
    @Test
    public void testProcedures() {
        String[] args = new String[]{"-t", "10", "-p", "data/procedures.xml", "-d", "src/test/resources/derby.properties", "-r", "2012-09-04T12:50:00Z"};


        try {
            XMLSerializer.main(args);
        } catch (Exception ex) {
            logger.error("",ex);
            Assert.fail();
            
        }
    }
    @Ignore
    @Test
    public void testMultipleProcedures(){
        //String[] args = new String[]{"-q", "'*.xml'", "-d", "/home/julian/databases/procedures/procedures.derby.properties", "-n",  "true"};
        
        
String[] args = new String[]{"-d", "/home/julian/executions/xmlvalidation/duncan2.mysql.properties", "-n",  "true"};

        try {

            XMLSerializer.main(args);
        } catch (Exception ex) {
            logger.error("",ex);
            Assert.fail();
            
        }
    }
    /*
     * @Test public void testSpecimens() { String[] args = new String[]{"-t",
     * "10", "-s", "data/specimens.xml", "-d", "local"};
     *
     * XMLSerializer.main(args);
     *
     * Properties properties = XMLSerializerTest.loadProperties();
     * properties.remove("hibernate.hbm2ddl.auto");
     *
     * PersistenceManager persistenceManager =
     * PersistenceManager.getInstance(PersistenceManager.PERSISTENCE_UNITNAME,
     * properties); List<SubmissionSet> submissionsets =
     * persistenceManager.query("from Submissionset", SubmissionSet.class);
     *
     * Assert.assertNotNull(submissionsets);
     *
     * Properties properties = null; try { properties =
     * PersistenceManager.loadProperties("src/test/resources/hsqldb.properties");
     * } catch (FileNotFoundException ex) { logger.error("",ex); Assert.fail();
     * } catch (IOException ex) { logger.error("",ex); Assert.fail(); }
     * Assert.assertNotNull(properties);
     * properties.remove("hibernate.hbm2ddl.auto"); PersistenceManager
     * persistenceManager =
     * PersistenceManager.getInstance(PersistenceManager.PERSISTENCE_UNITNAME,
     * properties);
     *
     *
     *
     * }
     */
}
