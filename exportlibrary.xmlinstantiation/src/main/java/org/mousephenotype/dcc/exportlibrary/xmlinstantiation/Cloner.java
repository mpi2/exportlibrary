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
package org.mousephenotype.dcc.exportlibrary.xmlinstantiation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedureSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.utils.xml.BeanUtils;
import org.mousephenotype.dcc.utils.xml.XMLUtils;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class Cloner {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Cloner.class);
    private static final String SPECIMEN_CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen";
    private static final String PROCEDURE_CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure";

    //write java into file
    public static void marshallCentreSpecimenSet(CentreSpecimenSet centreSpecimenSet, String filename) throws JAXBException {
        XMLUtils.marshall(SPECIMEN_CONTEXT_PATH, centreSpecimenSet, filename);
    }

    public static void marshallCentreProcedureSet(CentreProcedureSet centreProcedureSet, String filename) throws JAXBException {
        XMLUtils.marshall(PROCEDURE_CONTEXT_PATH, centreProcedureSet, filename);
    }

    //reads from file into java
    public static CentreSpecimenSet unmarshallSpecimenXMLfile(String filename) throws JAXBException, FileNotFoundException, IOException {
        return XMLUtils.unmarshal(SPECIMEN_CONTEXT_PATH, CentreSpecimenSet.class, filename);
    }

    public static CentreProcedureSet unmarshallProcedureXMLfile(String filename) throws JAXBException, FileNotFoundException, IOException {
        return XMLUtils.unmarshal(PROCEDURE_CONTEXT_PATH, CentreProcedureSet.class, filename);
    }

    public static CentreProcedureSet cloneProcedure(CentreProcedureSet centreProcedureSet) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, DatatypeConfigurationException {
        CentreProcedureSet dest = new CentreProcedureSet();
        logger.info("before cloning centre procedure {}", centreProcedureSet.getCentre().get(0).getCentreID().name());
        BeanUtils.clone(centreProcedureSet, dest);
        logger.info("{} cloned", centreProcedureSet.getCentre().get(0).getCentreID().name());
        return dest;
    }

    public static CentreSpecimenSet cloneSpecimen(CentreSpecimenSet centreSpecimenSet) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, DatatypeConfigurationException {
        CentreSpecimenSet dest = new CentreSpecimenSet();
        logger.info("before cloning centre procedure {}", centreSpecimenSet.getCentre().get(0).getCentreID().name());
        BeanUtils.clone(centreSpecimenSet, dest);
        logger.info("{} cloned", centreSpecimenSet.getCentre().get(0).getCentreID().name());
        return dest;
    }

    public static void main(String[] args) {
    }
}
