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

import java.util.Map;

/**
 *
 * @author julian
 */
public class IOParameters {

    private IOS ios = null;
    private Map<IOS_PROPERTIES, String> values = null;
    private Long hjid = null;
    private DOCUMENT_SRCS doc_srcs = null;

    public IOParameters() {
    }

    public enum IOS {

        XML_FILE() {
            @Override
            public IOS_PROPERTIES[] getPropertyKeys() {
                return new IOS_PROPERTIES[]{IOS_PROPERTIES.CONTEXTPATH, IOS_PROPERTIES.XMLFILENAME};
            }
        }, EXTERNAL_DB_PROPERTIES() {
            @Override
            public IOS_PROPERTIES[] getPropertyKeys() {
                return new IOS_PROPERTIES[]{IOS_PROPERTIES.PERSISTENCEUNITNAME, IOS_PROPERTIES.PROPERTIES_FILENAME};
            }
        }, DEFAULT_DB_PROPERTIES() {
            @Override
            public IOS_PROPERTIES[] getPropertyKeys() {
                return new IOS_PROPERTIES[]{IOS_PROPERTIES.PERSISTENCEUNITNAME, IOS_PROPERTIES.PROPERTIES_FILENAME};
            }
        }, TEST_DB_PROPERTIES() {
            @Override
            public IOS_PROPERTIES[] getPropertyKeys() {
                return new IOS_PROPERTIES[]{IOS_PROPERTIES.PERSISTENCEUNITNAME, IOS_PROPERTIES.PROPERTIES_FILENAME};
            }
        }, WEB_SERVICES() {
            @Override
            public IOS_PROPERTIES[] getPropertyKeys() {
                return new IOS_PROPERTIES[]{IOS_PROPERTIES.URL};
            }
        }, JNDI() {
            @Override
            public IOS_PROPERTIES[] getPropertyKeys() {
                return new IOS_PROPERTIES[]{IOS_PROPERTIES.URL};
            }
        };

        ;

        public abstract IOS_PROPERTIES[] getPropertyKeys();
    };

    public enum VALIDATIONRESOURCES_IDS {

        ImpressBrowser, MGIBrowser, Imits, Statuscodes
    };

    /*
     * THOSE ARE THE DOCUMENTS THAT CAN BE VALIDATED
     */
    public enum DOCUMENTS {

        CENTRESPECIMENSET, CENTREPROCEDURESET
    }

    /*
     * THOSE ARE THE SOURCES TO BE USED TO INSTANTIATE A DOCUMENT
     * (centrespecimenset or centreprocedureset)
     */
    public enum DOCUMENT_SRCS {

        CENTREPROCEDURESET, CENTRESPECIMENSET, SUBMISSIONSET, CENTREPROCEDURESET_HJID, CENTRESPECIMENSET_HJID, SUBMISSION_TRACKERID, SUBMISSIONSET_HJID
    }

    public enum CONNECTOR_NAMES {

        centreProcedureSetIncarnator, centreSpecimenSetIncarnator, submissionIncarnator, validationSetSerializer, validationReportSetSerializer,
        resources, impressBrowser, mgiBrowser, imitsBrowser
    };

    public enum IMITS_PROPERTIES {
        
        imitsUsername("imits.username"),
        imitsPassword("imits.password");
        String propertyName;
        static String propertyCode ="imits";

        private IMITS_PROPERTIES(String propertyName_) {
            propertyName = propertyName_;
        }

        public String getPropertyName() {
            return this.propertyName;
        }
        public static String getPropertyCode(){
            return propertyCode;
        }
    }
    
    

    public enum IOS_PROPERTIES {

        CONTEXTPATH, XMLFILENAME, PERSISTENCEUNITNAME, PROPERTIES_FILENAME, URL
    };

    public enum COMS {

        INPUT, RESOURCE, RESULT
    };

    /**
     * @return the ios
     */
    public IOS getIos() {
        return ios;
    }

    /**
     * @param ios the ios to set
     */
    public void setIos(IOS ios) {
        this.ios = ios;
    }

    /**
     * @return the values
     */
    public Map<IOS_PROPERTIES, String> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(Map<IOS_PROPERTIES, String> values) {
        this.values = values;
    }

    /**
     * @return the hjid
     */
    public Long getHjid() {
        return hjid;
    }

    /**
     * @param hjid the hjid to set
     */
    public void setHjid(Long hjid) {
        this.hjid = hjid;
    }

    /**
     * @return the doc_srcs
     */
    public DOCUMENT_SRCS getDoc_srcs() {
        return doc_srcs;
    }

    /**
     * @param doc_srcs the doc_srcs to set
     */
    public void setDoc_srcs(DOCUMENT_SRCS doc_srcs) {
        this.doc_srcs = doc_srcs;
    }
}
