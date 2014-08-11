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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;

import java.util.Date;

/**
 *
 * @author duncan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CentreContainer {

    private static final Logger logger = LoggerFactory.getLogger(CentreContainer.class);
    private CentreILARcode centreILAR;
    private String pipeline;
    private Date creationTimestamp;
    private List<SpecimenReference> specimenReference = new ArrayList<SpecimenReference>();
    private List<DataReference> dataReference = new ArrayList<DataReference>();

    public CentreContainer(CentreILARcode centreILAR, String pipeline, Date creationTimestamp) {
        this.centreILAR = centreILAR;
        this.pipeline = pipeline;
        this.creationTimestamp = creationTimestamp;
    }

    public CentreContainer() {
    }

    public CentreILARcode getCentreILAR() {
        return centreILAR;
    }

    public void setCentreILAR(CentreILARcode centreILAR) {
        this.centreILAR = centreILAR;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public List<SpecimenReference> getSpecimenReference() {
        return specimenReference;
    }

    public void setSpecimenReference(List<SpecimenReference> specimenReference) {
        this.specimenReference = specimenReference;
    }

    public List<DataReference> getDataReference() {
        return dataReference;
    }

    public void setDataReference(List<DataReference> dataReference) {
        this.dataReference = dataReference;
    }
}
