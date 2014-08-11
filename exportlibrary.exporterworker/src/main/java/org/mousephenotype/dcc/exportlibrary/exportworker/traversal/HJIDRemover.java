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
package org.mousephenotype.dcc.exportlibrary.exportworker.traversal;

import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Dimension;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Housing;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Line;
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
import org.mousephenotype.dcc.exportlibrary.datastructure.traverser.CommandImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets all of the HJID's to null so they do not appear in the export.
 *
 * @author duncan
 */
public class HJIDRemover extends CommandImpl {

    private static final Logger logger = LoggerFactory.getLogger(HJIDRemover.class);

    @Override
    public void execute(Object parameter) {
        super.execute(parameter);
        if (parameter.getClass().equals(StatusCode.class)) {
            run((StatusCode) parameter);
        }
        if (parameter.getClass().equals(Line.class)) {
            run((Line) parameter);
        }
        if (parameter.getClass().equals(Housing.class)) {
            run((Housing) parameter);
        }
    }

    @Override
    public boolean run(CentreProcedure centreProcedure) {
        centreProcedure.setHjid(null);
        return true;
    }

    @Override
    public boolean run(Experiment experiment) {
        experiment.setHjid(null);
        return true;

    }

    @Override
    public boolean run(Procedure Procedure) {
        Procedure.setHjid(null);
        return true;
    }

    @Override
    public boolean run(SimpleParameter simpleParameter) {
        simpleParameter.setHjid(null);
        return true;
    }

    @Override
    public boolean run(OntologyParameter ontologyParameter) {
        ontologyParameter.setHjid(null);
        return true;
    }

    @Override
    public boolean run(SeriesParameter seriesParameter) {
        seriesParameter.setHjid(null);
        return true;
    }

    @Override
    public boolean run(SeriesParameterValue seriesParameterValue) {
        seriesParameterValue.setHjid(null);
        return true;
    }

    @Override
    public boolean run(MediaParameter mediaParameter) {
        mediaParameter.setHjid(null);
        return true;
    }

    @Override
    public boolean run(MediaSampleParameter mediaSampleParameter) {
        mediaSampleParameter.setHjid(null);
        return true;
    }

    @Override
    public boolean run(MediaSample mediaSample) {
        mediaSample.setHjid(null);
        return true;
    }

    @Override
    public boolean run(MediaSection mediaSection) {
        mediaSection.setHjid(null);
        return true;
    }

    @Override
    public boolean run(MediaFile mediaFile) {
        mediaFile.setHjid(null);
        return true;
    }

    @Override
    public boolean run(ParameterAssociation parameterAssociation) {
        parameterAssociation.setHjid(null);
        return true;
    }

    @Override
    public boolean run(Dimension dimension) {
        dimension.setHjid(null);
        return true;
    }

    @Override
    public boolean run(SeriesMediaParameter seriesMediaParameter) {
        seriesMediaParameter.setHjid(null);
        return true;
    }

    @Override
    public boolean run(SeriesMediaParameterValue seriesMediaParameterValue) {
        seriesMediaParameterValue.setHjid(null);
        return true;
    }

    @Override
    public boolean run(ProcedureMetadata procedureMetadata) {
        procedureMetadata.setHjid(null);
        return true;
    }

    @Override
    public boolean run(CentreSpecimen centreSpecimen) {
        centreSpecimen.setHjid(null);
        return true;
    }

    @Override
    public boolean run(Specimen specimen) {
        specimen.setHjid(null);
        return true;
    }

    @Override
    public boolean run(Mouse mouse) {
        mouse.setHjid(null);
        return true;
    }

    @Override
    public boolean run(Embryo embryo) {
        embryo.setHjid(null);
        return true;
    }

    public boolean run(StatusCode statuscode) {
        statuscode.setHjid(null);
        return true;
    }

    public boolean run(Housing housing) {
        housing.setHjid(null);
        return true;
    }

    public boolean run(Line line) {
        line.setHjid(null);
        return true;
    }
}
