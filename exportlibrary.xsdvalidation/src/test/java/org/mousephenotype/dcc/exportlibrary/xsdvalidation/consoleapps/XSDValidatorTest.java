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
package org.mousephenotype.dcc.exportlibrary.xsdvalidation.consoleapps;
import org.junit.Test;

/**
 *
 * @author julian
 * @author Duncan
 */
public class XSDValidatorTest {

   
    
   
    
    private static final String testId = null;

      
    
    @Test
    public void testRun() {
        String specimensFilename = "data/specimens.xml";
        String procedureResultsFileName = "data/procedures.xml";
        XSDValidator xsdValidator = new XSDValidator(null);
        xsdValidator.run(specimensFilename, procedureResultsFileName);
    }
    
    
    
    @Test
    public void testMain(){
        String[] args = new String[]{"-p","data/procedures.xml"};
        XSDValidator xsdVal = new XSDValidator(null);
        xsdVal.main(args);
        
    }
    
    @Test
    public void testMain2attributesSameName(){
        String[] args = new String[]{"-p","data/procedures_2attributesSameName.xml"};
        XSDValidator xsdVal = new XSDValidator(null);
        xsdVal.main(args);
        
    }

    @Test
    public void testMain2(){
        String[] args = new String[]{"-s","data/specimens.xml"};
        XSDValidator xsdVal = new XSDValidator(null);
        xsdVal.main(args);
        
    }
    
}
