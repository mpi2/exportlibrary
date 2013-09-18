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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.support;

import com.google.common.collect.Table;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSampleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.OntologyParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesMediaParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType;

public class RepeatedParameterInfo {

    public static final BigInteger NO_SEQUENCEID = BigInteger.valueOf(-1);
    private Object parameter;
    private String parameterKey;
    private BigInteger sequenceID = NO_SEQUENCEID;
    private ImpressParameterType parameterType;
    private ImpressParameterType definedParameterType;

    public RepeatedParameterInfo(Object parameter, String parameterKey, BigInteger sequenceID, ImpressParameterType parameterType, ImpressParameterType definedParameterType) {
        this.parameter = parameter;
        this.parameterKey = parameterKey;
        if (sequenceID != null) {
            this.sequenceID = sequenceID;
        }
        this.parameterType = parameterType;
        this.definedParameterType = definedParameterType;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public String getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(String parameterKey) {
        this.parameterKey = parameterKey;
    }

    public BigInteger getSequenceID() {
        return sequenceID;
    }

    public void setSequenceID(BigInteger sequenceID) {
        this.sequenceID = sequenceID;
    }

    public ImpressParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(ImpressParameterType parameterType) {
        this.parameterType = parameterType;
    }

    /**
     * @return the definedParameterType
     */
    public ImpressParameterType getDefinedParameterType() {
        return definedParameterType;
    }

    /**
     * @param definedParameterType the definedParameterType to set
     */
    public void setDefinedParameterType(ImpressParameterType definedParameterType) {
        this.definedParameterType = definedParameterType;
    }

    public SimpleParameter getSimpleParameter() {
        return (SimpleParameter) this.parameter;
    }

    public OntologyParameter getOntologyParameter() {
        return (OntologyParameter) this.parameter;
    }

    public SeriesParameter getSeriesParameter() {
        return (SeriesParameter) this.parameter;
    }

    public MediaParameter getMediaParameter() {
        return (MediaParameter) this.parameter;
    }

    public MediaSampleParameter getMediaSampleParameter() {
        return (MediaSampleParameter) this.parameter;
    }

    public SeriesMediaParameter getSeriesMediaParameter() {
        return (SeriesMediaParameter) this.parameter;
    }

    public ProcedureMetadata getProcedureMetadata() {
        return (ProcedureMetadata) this.parameter;
    }

    public static List<RepeatedParameterInfo> put(Table<String, BigInteger, List<RepeatedParameterInfo>> table, RepeatedParameterInfo info) {
        List<RepeatedParameterInfo> infos = null;
        if (table.contains(info.getParameterKey(), info.getSequenceID())) {
            infos = table.get(info.getParameterKey(), info.getSequenceID());
        } else {
            infos = new ArrayList<RepeatedParameterInfo>();
            table.put(info.getParameterKey(), info.getSequenceID(), infos);
        }
        infos.add(info);
        return infos;
    }

    public String getErrorMessage(int repeats) {
        StringBuilder sb = new StringBuilder();
        sb.append("Parameter ");
        sb.append(this.getParameterKey());
        if (this.getSequenceID() != NO_SEQUENCEID) {
            sb.append(" sequenceID ");
            sb.append(this.sequenceID);
        }
        sb.append(" has been found ");
        sb.append(repeats);
        sb.append(" times");
        return sb.toString();
    }
}
