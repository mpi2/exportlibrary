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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation;

import java.util.Properties;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;
import javax.xml.bind.JAXBException;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class Serializer<T> extends Connector {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Serializer.class);

    public Serializer(String contextPath, String xmlFilename) {
        super(contextPath, xmlFilename);
    }

    public Serializer(HibernateManager hibernateManager) {
        super(hibernateManager);
    }

    public Serializer(String persistenceUnitName, Properties properties) {
        super(persistenceUnitName, properties);
    }

    private void marshall(T object) throws JAXBException {
        logger.info("marshalling {}", xmlFilename);
        XMLUtils.marshall(contextPath, object, xmlFilename);
    }

    private void persist(T object) throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, RuntimeException {
        this.hibernateManager.persist(object);
    }

    public void serialize(T object, Class<T> clazz) throws JAXBException, IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException, RuntimeException, Exception {
        if (toXml) {
            if (Connector.validType(clazz)) {
                this.marshall(object);
            } else {
                logger.error("class {} cannot be serializeda as a xml document");
                throw new Exception("class " + clazz.getName() + "cannot be serializeda as a xml document");
            }
        } else {
            this.persist(object);
        }
    }
}
