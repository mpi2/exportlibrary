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
package org.mousephenotype.dcc.exportlibrary.datastructure.traverser;

import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Dimension;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaFile;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSample;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSampleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSection;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.OntologyParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ParameterAssociation;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesMediaParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesMediaParameterValue;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameterValue;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Embryo;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Mouse;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;

public abstract class CommandImpl implements Command {

    public abstract boolean run(CentreProcedure centreProcedure);

    public abstract boolean run(Experiment experiment);

    public abstract boolean run(Procedure Procedure);

    public abstract boolean run(SimpleParameter simpleParameter);

    public abstract boolean run(OntologyParameter ontologyParameter);

    public abstract boolean run(SeriesParameter seriesParameter);

    public abstract boolean run(SeriesParameterValue seriesParameterValue);

    public abstract boolean run(MediaParameter mediaParameter);

    public abstract boolean run(MediaSampleParameter mediaSampleParameter);

    public abstract boolean run(MediaSample mediaSample);

    public abstract boolean run(MediaSection mediaSection);

    public abstract boolean run(MediaFile mediaFile);

    public abstract boolean run(ParameterAssociation parameterAssociation);

    public abstract boolean run(Dimension dimension);

    public abstract boolean run(SeriesMediaParameter seriesMediaParameter);

    public abstract boolean run(SeriesMediaParameterValue seriesMediaParameterValue);

    public abstract boolean run(ProcedureMetadata procedureMetadata);
    //specimen
    public abstract boolean run(CentreSpecimen centreSpecimen);
    
    public abstract boolean run(Specimen specimen);
    
    public abstract boolean run(Mouse mouse);
    
    public abstract boolean run(Embryo embryo);
   
    
      
    
    
    @Override
    public boolean executeAndReturn(Object parameter) {
        if (parameter.getClass().equals(CentreProcedure.class)) {
            return this.run((CentreProcedure) parameter);
        }
        if (parameter.getClass().equals(Experiment.class)) {
            return this.run((Experiment) parameter);
        }
        if (parameter.getClass().equals(Procedure.class)) {
            return this.run((Procedure) parameter);

        }
        if (parameter.getClass().equals(SimpleParameter.class)) {
            return this.run((SimpleParameter) parameter);

        }
        if (parameter.getClass().equals(OntologyParameter.class)) {
            return this.run((OntologyParameter) parameter);

        }
        if (parameter.getClass().equals(SeriesParameter.class)) {
            return this.run((SeriesParameter) parameter);

        }
        if (parameter.getClass().equals(SeriesParameterValue.class)) {
            return this.run((SeriesParameterValue) parameter);

        }
        if (parameter.getClass().equals(MediaParameter.class)) {
            return this.run((MediaParameter) parameter);

        }
        if (parameter.getClass().equals(MediaSampleParameter.class)) {
            return this.run((MediaSampleParameter) parameter);

        }
        if (parameter.getClass().equals(MediaSample.class)) {
            return this.run((MediaSample) parameter);

        }
        if (parameter.getClass().equals(MediaSection.class)) {
            return this.run((MediaSection) parameter);

        }
        if (parameter.getClass().equals(MediaFile.class)) {
            return this.run((MediaFile) parameter);

        }
        if (parameter.getClass().equals(ParameterAssociation.class)) {
            return this.run((ParameterAssociation) parameter);

        }
        if (parameter.getClass().equals(Dimension.class)) {
            return this.run((Dimension) parameter);

        }
        if (parameter.getClass().equals(SeriesMediaParameter.class)) {
            return this.run((SeriesMediaParameter) parameter);

        }
        if (parameter.getClass().equals(SeriesMediaParameterValue.class)) {
            return this.run((SeriesMediaParameterValue) parameter);

        }
        if (parameter.getClass().equals(ProcedureMetadata.class)) {
            return this.run((ProcedureMetadata) parameter);
        }
        //specimen    
        if (parameter.getClass().equals(CentreSpecimen.class)) {
            return this.run((CentreSpecimen) parameter);
        }
        if (parameter.getClass().equals(Specimen.class)) {
            return this.run((Specimen) parameter);
        }
        if (parameter.getClass().equals(Mouse.class)) {
            return this.run((Mouse) parameter);
        }
        if (parameter.getClass().equals(Embryo.class)) {
            return this.run((Embryo) parameter);
        }

        return false;
    }
    
    @Override
    public void execute(Object parameter) {
        if (parameter.getClass().equals(CentreProcedure.class)) {
            this.run((CentreProcedure) parameter);
        }
        if (parameter.getClass().equals(Experiment.class)) {
            this.run((Experiment) parameter);
        }
        if (parameter.getClass().equals(Procedure.class)) {
              this.run((Procedure) parameter);

        }
        if (parameter.getClass().equals(SimpleParameter.class)) {
              this.run((SimpleParameter) parameter);

        }
        if (parameter.getClass().equals(OntologyParameter.class)) {
              this.run((OntologyParameter) parameter);

        }
        if (parameter.getClass().equals(SeriesParameter.class)) {
              this.run((SeriesParameter) parameter);

        }
        if (parameter.getClass().equals(SeriesParameterValue.class)) {
              this.run((SeriesParameterValue) parameter);

        }
        if (parameter.getClass().equals(MediaParameter.class)) {
              this.run((MediaParameter) parameter);

        }
        if (parameter.getClass().equals(MediaSampleParameter.class)) {
              this.run((MediaSampleParameter) parameter);

        }
        if (parameter.getClass().equals(MediaSample.class)) {
              this.run((MediaSample) parameter);

        }
        if (parameter.getClass().equals(MediaSection.class)) {
              this.run((MediaSection) parameter);

        }
        if (parameter.getClass().equals(MediaFile.class)) {
              this.run((MediaFile) parameter);

        }
        if (parameter.getClass().equals(ParameterAssociation.class)) {
              this.run((ParameterAssociation) parameter);

        }
        if (parameter.getClass().equals(Dimension.class)) {
              this.run((Dimension) parameter);

        }
        if (parameter.getClass().equals(SeriesMediaParameter.class)) {
              this.run((SeriesMediaParameter) parameter);

        }
        if (parameter.getClass().equals(SeriesMediaParameterValue.class)) {
              this.run((SeriesMediaParameterValue) parameter);

        }
        if (parameter.getClass().equals(ProcedureMetadata.class)) {
              this.run((ProcedureMetadata) parameter);
        }
        //specimen
        if (parameter.getClass().equals(CentreSpecimen.class)) {
            this.run((CentreSpecimen) parameter);
        }
        if (parameter.getClass().equals(Specimen.class)) {
            this.run((Specimen) parameter);
        }
        if (parameter.getClass().equals(Mouse.class)) {
            this.run((Mouse) parameter);
        }
        if (parameter.getClass().equals(Embryo.class)) {
            this.run((Embryo) parameter);
        }
        
    }
}
