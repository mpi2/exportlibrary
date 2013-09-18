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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.statuscodes;

import java.io.FileNotFoundException;
import java.util.Properties;
import javax.xml.bind.JAXBException;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.Incarnator;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.statuscodes.StatusCode;


import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.statuscodes.Statuscodes;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

public class StatusCodesBrowser extends Incarnator<Statuscodes> {

    public Statuscodes getStatuscodes() {
        if (object == null) {
            object = new Statuscodes();
        }
        return this.getObject();
    }

    public StatusCodesBrowser(String contextPath, String xmlFilename) throws Exception {
        super(contextPath, xmlFilename);
        this.load(Statuscodes.class);
    }

    public StatusCodesBrowser(HibernateManager hibernateManager) throws JAXBException, FileNotFoundException, Exception {
        super(hibernateManager);
        this.load(Statuscodes.class);
    }

    public StatusCodesBrowser(String persistenceUnitName, Properties properties)  throws JAXBException, FileNotFoundException, Exception {
        super(persistenceUnitName, properties);
        this.load(Statuscodes.class);
    }

    public boolean exists(String statusCodeValue) {
        for (StatusCode statusCode : this.getStatuscodes().getStatusCode()) {
            if (statusCodeValue.startsWith(statusCode.getCode())) {
                return true;
            }
        }
        return false;
    }

    
}
