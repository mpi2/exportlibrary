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
package org.mousephenotype.dcc.exportlibrary.exporter.linedaos;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;

@Table(name = "EMBRYO")
public class ExperimentDAO implements Serializable, Equals, HashCode {

    private String REPORTIDENTIFIER;
    protected Long hjid;
    protected String EXPERIMENTID;
    protected String SEQUENCEID;

    @Transient
    public boolean isSetREPORTIDENTIFIER() {
        return (this.REPORTIDENTIFIER != null);
    }

    @Transient
    public boolean isSetEXPERIMENTID() {
        return (this.EXPERIMENTID != null);
    }

    @Transient
    public boolean isSetSEQUENCEID() {
        return (this.SEQUENCEID != null);
    }

    @Basic
    @Column(name = "PIPELINE", length = 255)
    public String getREPORTIDENTIFIER() {
        return REPORTIDENTIFIER;
    }

    public void setREPORTIDENTIFIER(String REPORTIDENTIFIER) {
        this.REPORTIDENTIFIER = REPORTIDENTIFIER;
    }

    @Id
    @Column(name = "HJID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getHjid() {
        return hjid;
    }

    public void setHjid(Long hjid) {
        this.hjid = hjid;
    }

    @Basic
    @Column(name = "PIPELINE", length = 255)
    public String getEXPERIMENTID() {
        return EXPERIMENTID;
    }

    public void setEXPERIMENTID(String EXPERIMENTID) {
        this.EXPERIMENTID = EXPERIMENTID;
    }

    @Basic
    @Column(name = "PIPELINE", length = 255)
    public String getSEQUENCEID() {
        return SEQUENCEID;
    }

    public void setSEQUENCEID(String SEQUENCEID) {
        this.SEQUENCEID = SEQUENCEID;
    }

    @Override
    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof ExperimentDAO)) {
            return false;
        }
        if (this == object) {
            return true;
        }

        final ExperimentDAO that = (ExperimentDAO) object;


        {
            String lhsREPORTIDENTIFIER;
            lhsREPORTIDENTIFIER = this.getREPORTIDENTIFIER();
            String rhsREPORTIDENTIFIER;
            rhsREPORTIDENTIFIER = that.getREPORTIDENTIFIER();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "REPORTIDENTIFIER", lhsREPORTIDENTIFIER), LocatorUtils.property(thatLocator, "REPORTIDENTIFIER", rhsREPORTIDENTIFIER), lhsREPORTIDENTIFIER, rhsREPORTIDENTIFIER)) {
                return false;
            }
        }

        {
            String lhsEXPERIMENTID;
            lhsEXPERIMENTID = this.getEXPERIMENTID();
            String rhsEXPERIMENTID;
            rhsEXPERIMENTID = that.getEXPERIMENTID();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "EXPERIMENTID", lhsEXPERIMENTID), LocatorUtils.property(thatLocator, "REPORTIDENTIFIER", rhsEXPERIMENTID), lhsEXPERIMENTID, rhsEXPERIMENTID)) {
                return false;
            }
        }

        {
            String lhsSEQUENCEID;
            lhsSEQUENCEID = this.getSEQUENCEID();
            String rhsSEQUENCEID;
            rhsSEQUENCEID = that.getSEQUENCEID();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "EXPERIMENTID", lhsSEQUENCEID), LocatorUtils.property(thatLocator, "REPORTIDENTIFIER", rhsSEQUENCEID), lhsSEQUENCEID, rhsSEQUENCEID)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    /*
     private String REPORTIDENTIFIER;
     protected Long hjid;
     protected String EXPERIMENTID;
     protected String SEQUENCEID;
     
     */
    @Override
    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = 1;

        {
            String theREPORTIDENTIFIER;
            theREPORTIDENTIFIER = this.getREPORTIDENTIFIER();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "REPORTIDENTIFIER", theREPORTIDENTIFIER), currentHashCode, theREPORTIDENTIFIER);
        }

        {
            String theEXPERIMENTID;
            theEXPERIMENTID = this.getEXPERIMENTID();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "EXPERIMENTID", theEXPERIMENTID), currentHashCode, theEXPERIMENTID);
        }

        {
            String theSEQUENCEID;
            theSEQUENCEID = this.getSEQUENCEID();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "SEQUENCEID", theSEQUENCEID), currentHashCode, theSEQUENCEID);
        }


        return currentHashCode;
    }

    @Override
    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }
}
