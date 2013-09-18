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
package org.mousephenotype.dcc.exportlibrary.xmlvalidation.external.impress;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;


/**
 *
 * @author julian
 */
public class Caster {

    private static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);

    public static Object cast(String parameterValue, String valueType) throws ParseException {
        if (parameterValue == null || valueType == null) {
            return null;
        }
        if (valueType.equals("BOOL")) {
            return Boolean.valueOf(parameterValue);
        }
        if (valueType.equals("DATETIME")) {
            return DatatypeConverter.parseDateTime(parameterValue);
        }
        
        if (valueType.equals("DATE")) {
            return DatatypeConverter.parseDate(parameterValue);
        }
        
        if (valueType.equals("TIME")) {
            return DatatypeConverter.parseTime(parameterValue);
        }
        
        if (valueType.equals("FLOAT")) {
            return new Float(numberFormat.parse(parameterValue).floatValue());
        }

        if (valueType.equals("IMAGE")) {
            return null;
        }

        if (valueType.equals("INT")) {
            return new Integer(numberFormat.parse(parameterValue).intValue());
        }

        if (valueType.equals("TEXT")) {
            return parameterValue;
        }
        return null;
        
    }
}
