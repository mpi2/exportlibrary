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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.mgi;

import com.google.common.collect.ImmutableMap;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;
import javax.xml.bind.JAXBException;

import org.mousephenotype.dcc.exportlibrary.xmlvalidation.Incarnator;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.mgi.MGIStrain;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.mgi.MgiStrains;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;

/**
 *
 * @author julian
 */
public class MGIBrowser extends Incarnator<MgiStrains> {

    public MgiStrains getMGIStrains() {
        if (object == null) {
            object = new MgiStrains();
        }
        return this.getObject();
    }

    public MGIBrowser(String contextPath, String xmlFilename) throws Exception {
        super(contextPath, xmlFilename);
    }

    public MGIBrowser(HibernateManager hibernateManager) {
        super(hibernateManager);
    }

    public MGIBrowser(String persistenceUnitName, Properties properties) {
        super(persistenceUnitName, properties);
    }

    public boolean exists(String mgiid) throws JAXBException, FileNotFoundException, Exception {
        if (toXml) {
            this.load(MgiStrains.class);
            for (MGIStrain miGIStrain : this.getMGIStrains().getMgiStrain()) {
                if (mgiid.equals(miGIStrain.getMGIID())) {
                    return true;
                }
            }
        } else {
            String query = "from MGIStrain where mgiid =:mgiid";
            List<MGIStrain> mgiStrains = this.hibernateManager.query(query, ImmutableMap.<String, Object>builder().put("mgiid", mgiid).build(), MGIStrain.class);
            if (mgiStrains != null && mgiStrains.size() > 0) {
                logger.trace("mgi results found");
                this.getMGIStrains().getMgiStrain().addAll(mgiStrains);
                return true;
            }
        }
        return false;
    }
}
