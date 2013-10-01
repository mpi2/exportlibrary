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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.statuscodes;

import java.util.Calendar;
import org.apache.commons.configuration.ConfigurationException;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.statuscodes.StatusCode;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.statuscodes.Statuscodes;
import org.mousephenotype.dcc.utils.io.conf.Command;
import org.mousephenotype.dcc.utils.io.conf.FileReader;
import org.slf4j.LoggerFactory;

public class StatusCodesReader implements Command<StatusCode> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StatusCodesReader.class);
    private final String filename;
    private final Statuscodes statusCodes;
    private final FileReader<StatusCode> fileReader; 

    public StatusCodesReader(String filename) {
        this.filename = filename;
        this.statusCodes = new Statuscodes();
        this.fileReader = new FileReader<>();
    }

    public Statuscodes getStatuscodes() {
        return this.statusCodes;
    }

    public void read(Calendar now) throws ConfigurationException {
        this.statusCodes.setLastUpdated(now);
        this.statusCodes.getStatusCode().addAll(this.fileReader.printFile(filename, this));
    }

    /**
     *
     * @param line
     * @return
     */
    @Override
    public StatusCode execute(String line) {
        StatusCode statusCode = null;
        String[] split = null;
        if (!line.isEmpty() && line.contains("~")) {
            statusCode = new StatusCode();
            split = line.split("~");
            if (split.length > 1) {
                statusCode.setCode(split[0].trim());
                statusCode.setDescription(split[1].trim());
                if (split.length > 2) {
                    statusCode.setExplanation(split[2].trim());
                }
            }
        }
        return statusCode;
    }
}
