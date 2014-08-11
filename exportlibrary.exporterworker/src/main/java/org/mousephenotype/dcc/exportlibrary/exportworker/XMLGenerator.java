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
package org.mousephenotype.dcc.exportlibrary.exportworker;

import javax.xml.bind.JAXBException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.utils.xml.XMLUtils;

public class XMLGenerator {

    private static final String SPECIMEN_CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen";
    private static final String PROCEDURE_CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure";

    public void serialize(CentreSpecimenSet centreSpecimenSet, String filename) throws JAXBException {
        XMLUtils.marshall(SPECIMEN_CONTEXT_PATH, centreSpecimenSet, filename);
    }

    public static void marshallCentreProcedureSet(CentreProcedureSet centreProcedureSet, String filename) throws JAXBException {
        XMLUtils.marshall(PROCEDURE_CONTEXT_PATH, centreProcedureSet, filename);
    }

    public void serialize(CentreProcedureSet centreProcedureSet, String filename) throws JAXBException {
        XMLUtils.marshall(PROCEDURE_CONTEXT_PATH, centreProcedureSet, filename);
    }
}
