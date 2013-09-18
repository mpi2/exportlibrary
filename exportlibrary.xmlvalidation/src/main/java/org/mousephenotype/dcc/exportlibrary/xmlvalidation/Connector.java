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
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.HibernateException;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;


/**
 *
 * @author julian
 */
public class Connector {

    protected final boolean toXml;
    public String contextPath;
    public String xmlFilename;
    public String persistenceUnitName;
    public HibernateManager hibernateManager;

    public Connector(String contextPath, String xmlFilename) {
        this.contextPath = contextPath;
        this.xmlFilename = xmlFilename;
        toXml = true;
    }

    public Connector(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
        toXml = false;
    }

    public Connector(String persistenceUnitName, Properties properties)  throws HibernateException {
        toXml = false;
        this.persistenceUnitName = persistenceUnitName;
        this.hibernateManager = new HibernateManager(properties, persistenceUnitName);
    }

    public HibernateManager getHibernateManager() {
        return this.hibernateManager;
    }

    public void closeHibernateManager() throws HibernateException {
        if (this.hibernateManager != null) {
            this.hibernateManager.close();
        }
    }

    //http://stackoverflow.com/questions/8826329/java-passing-class-with-annotation-to-generic-method
    //if(this.checkType(clazz))throw new Exception("Type" + clazz.getName() + "not addmited for reading");
    public static <T> boolean validType(Class<T> clazz) {
        if (clazz.getAnnotation(XmlRootElement.class) != null) {
            return true;
        }
        return false;
    }
}
