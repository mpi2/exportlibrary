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
package org.mousephenotype.dcc.exportlibrary.xmlserialization;

import java.io.File;
import org.junit.Assert;


import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ITTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ITTest.class);

    private int execute(String[] args) throws Exception {
        String __EXECUTABLE__ = "target/exportlibrary.xmlserialization-1.2.0-jar-with-dependencies.jar";
        File jar = new File(__EXECUTABLE__);
        String[] execArgs = new String[args.length + 3];
        System.arraycopy(args, 0, execArgs, 3, args.length);
        execArgs[0] = "java";
        execArgs[1] = "-jar";
        execArgs[2] = jar.getCanonicalPath();
        Process p = Runtime.getRuntime().exec(execArgs);
        p.waitFor();
        return p.exitValue();
    }

    @Test
    public void testDependenciesIncluded() {
        try {
            assertEquals(100, execute(new String[]{}));
        } catch (Exception ex) {
            Assert.fail();
            logger.error("libraries not included", ex);
        }
    }

    @Test
    public void testTrackerLocalProcedures() {
        String[] params = new String[]{"-t", "10", "-p", "data/procedures.xml", "-d", "local", "-r", "2012-09-04T12:50:00Z"};
        try {
            assertEquals(0, execute(params));
        } catch (Exception ex) {
            logger.error("error executing tracker local procedures", ex);
        }
        //Assert.assertTrue(new File("hsqldb").exists());
    }

    @Ignore
    @Test
    public void testTrackerLocalSpecimens() {
        String[] params = new String[]{"-t", "10", "-s", "data/specimens.xml", "-d", "local", "-r", "2012-09-04T12:50:00Z"};
        try {
            assertEquals(0, execute(params));
        } catch (Exception ex) {
            logger.error("", ex);
        }
        // Assert.assertTrue(new File("hsqldb").exists());
    }

    @Ignore
    @Test
    public void testTrackerDCCProcedures() {
        String[] params = new String[]{"-t", "10", "-p", "data/procedures.xml", "-d", "dcc", "-r", "2012-09-04T12:50:00Z"};
        try {
            assertEquals(0, execute(params));
        } catch (Exception ex) {
            logger.error("error executing tracker local procedures", ex);
        }

    }

    @Ignore
    @Test
    public void testTrackerDCCSpecimens() {
        String[] params = new String[]{"-t", "10", "-s", "data/specimens.xml", "-d", "dcc", "-r", "2012-09-04T12:50:00Z"};
        try {
            assertEquals(0, execute(params));
        } catch (Exception ex) {
            logger.error("", ex);
        }

    }
}
