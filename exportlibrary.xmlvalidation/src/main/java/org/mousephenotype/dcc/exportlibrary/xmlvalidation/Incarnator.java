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

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.*;
import javax.xml.bind.JAXBException;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.xml.XMLUtils;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class Incarnator<T> extends Connector {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(Incarnator.class);
    //not sure if  objects should be a single object,as incarnator is for the root elements 
    //protected as children need access
    protected T object;

    public T getObject() {
        
        return object;
    }

    public Incarnator(String contextPath, String xmlFilename) throws Exception {
        super(contextPath, xmlFilename);
    }

    public Incarnator(HibernateManager hibernateManager) {
        super(hibernateManager);
    }

    public Incarnator(String persistenceUnitName, Properties properties) throws HibernateException {
        super(persistenceUnitName, properties);
    }

    public T load(Class<T> clazz) throws JAXBException, FileNotFoundException, Exception {
        if (this.toXml) {
            if (Incarnator.validType(clazz)) {
                logger.info("loading xml file {}", this.xmlFilename);
                this.object = XMLUtils.unmarshal(this.contextPath, clazz, this.xmlFilename);
                return this.object;

            } else {
                logger.error("class {} cannot be read from a xml document", clazz.getName());
                throw new Exception("class " + clazz.getName() + "cannot be read from a xml document");
            }
        } else {
            logger.info("single result HB query: from {}", clazz.getName());
            this.object = this.hibernateManager.uniqueResult(clazz);
            return this.getObject();
        }
    }

    public T load(Class<T> clazz, Long hjid) throws JAXBException, FileNotFoundException, Exception {
        if (this.toXml) {
            logger.info("loading xml file {}", this.xmlFilename);
            return this.load(clazz);
        } else {
            this.object= this.hibernateManager.load(clazz, hjid);
            return this.getObject();
        }
    }

    public <T> List<T> load(Class<T> clazz, String query, Map<String, Object> parameters) throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException,
            LockTimeoutException, PersistenceException {
        if (this.toXml) {
            //actually you can perform the query on the objects after loading them from the xml document
            logger.error("cannot query an xml object");
            return null;
        }else{
            List<T> results = this.hibernateManager.query(query,parameters,clazz);
            return results;
        }
    }

}
