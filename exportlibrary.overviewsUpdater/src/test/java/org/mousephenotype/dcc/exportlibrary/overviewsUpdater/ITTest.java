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
package org.mousephenotype.dcc.exportlibrary.overviewsUpdater;

import java.io.File;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ITTest {
    private static final Logger logger = LoggerFactory.getLogger(ITTest.class);
    private int execute(String[] args) throws Exception {
	String  __EXECUTABLE__= "target/exportlibrary.overviewsUpdater-1.0.0-jar-with-dependencies.jar";
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
    public void testExecute() throws Exception {
        assertEquals(0, execute(new String[]{}));
    }
}
