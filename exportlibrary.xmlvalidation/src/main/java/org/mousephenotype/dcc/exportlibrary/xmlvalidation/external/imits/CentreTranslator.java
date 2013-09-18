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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.imits;

import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.imits.ImitsProductionCentre;

 
public class CentreTranslator {
 public static ImitsProductionCentre get(CentreILARcode centreILARcode) {
        
        if (centreILARcode.equals(CentreILARcode.BCM)) {
            return ImitsProductionCentre.BCM;
        }
      
        if (centreILARcode.equals(CentreILARcode.GMC)) {
            return ImitsProductionCentre.HMGU;
        }
        if (centreILARcode.equals(CentreILARcode.H)) {
            return ImitsProductionCentre.HARWELL;
        }
        
        if (centreILARcode.equals(CentreILARcode.ICS)) {
            return ImitsProductionCentre.ICS;
        }
        if (centreILARcode.equals(CentreILARcode.J)) {
            return ImitsProductionCentre.JAX;
        }
        
        if (centreILARcode.equals(CentreILARcode.TCP)) {
            return ImitsProductionCentre.TCP;
        }
        if (centreILARcode.equals(CentreILARcode.NING)) {
            return null;
        }
        if (centreILARcode.equals(CentreILARcode.RBRC)) {
            return null;
        }
        if (centreILARcode.equals(CentreILARcode.UCD)) {
            return ImitsProductionCentre.UCD;
        }
        if (centreILARcode.equals(CentreILARcode.WTSI)) {
            return ImitsProductionCentre.WTSI;
        }

        return null;
    }
}
