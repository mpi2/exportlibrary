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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.mgi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Scanner;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.mgi.MGIStrain;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.mgi.MgiStrains;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.mgi.StrainType;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class MGIStrainParser {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MGIStrainParser.class);
    private static final String FIELD_DELIMITER = "\\t";
    private static final String LINE_DELIMITER = "\n";
    private MgiStrains strains;
    private final String filename;
    private final Calendar parsed;
    private final Scanner scanner;

    public MGIStrainParser(String filename, Calendar parsed) throws FileNotFoundException {
        this.filename = filename;
        this.parsed = parsed;
        this.scanner = new Scanner(new BufferedReader(new FileReader(this.filename)));
        this.scanner.useDelimiter(LINE_DELIMITER);
        this.strains = new MgiStrains();
        this.strains.setParsed(this.parsed);
    }

    private MGIStrain getMGIStrain(String[] fields) {
        MGIStrain mGIStrain = new MGIStrain();
        mGIStrain.setMGIID(fields[0]);
        mGIStrain.setStrainID(fields[1]);
        try {
            mGIStrain.setStrainType(StrainType.fromValue(fields[2]));
        } catch (Exception ex) {
            logger.error("exception for value [{}]", fields[2], ex);
        }
        return mGIStrain;
    }

    public void run() {
        while (this.scanner.hasNext()) {
            this.strains.getMgiStrain().add(this.getMGIStrain(this.scanner.next().split(FIELD_DELIMITER)));
        }
        this.scanner.close();
    }

    public MgiStrains getMGIStrains() {
        return this.strains;
    }
}
