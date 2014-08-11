/**
 * Copyright (C) 2014 Duncan Sneddon <d.sneddon at har.mrc.ac.uk>
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
package org.mousephenotype.dcc.exportlibrary.exportcommunication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author duncan
 */
public class DataReference {

    private static final Logger logger = LoggerFactory.getLogger(DataReference.class);
    private Long hjid;
    private ObjectType type;

    public static enum ObjectType {
        EXPERIMENT, LINE, HOUSING
    };

    public DataReference() {
    }

    public DataReference(Long hjid, ObjectType type) {
        this.hjid = hjid;
        this.type = type;
    }

    public Long getHjid() {
        return hjid;
    }

    public void setHjid(Long hjid) {
        this.hjid = hjid;
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }
}
