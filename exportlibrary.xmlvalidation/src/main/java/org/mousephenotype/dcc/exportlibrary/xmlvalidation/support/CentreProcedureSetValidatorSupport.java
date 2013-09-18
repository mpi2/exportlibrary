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

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.util.Set;

/**
 *
 * @author julian
 */
public class CentreProcedureSetValidatorSupport {

    public static String getListOfRequiredNotPresentParameters(Set<String> required, Set<String> present) {
        StringBuilder sb = new StringBuilder("{");
        for (String nonpresent : Sets.difference(required, present)) {
            sb.append(nonpresent);
            sb.append(", ");
        }
        sb.deleteCharAt(sb.lastIndexOf(", "));
        sb.append("}");
        return sb.toString();
    }

    public static String getListOfParametersNotBelongingToThisProcedure(SetView<String> difference) {
        StringBuilder sb = new StringBuilder("{");
        for (String nonpresent :difference) {
            sb.append(nonpresent);
            sb.append(", ");
        }
        sb.deleteCharAt(sb.lastIndexOf(", "));
        sb.append("}");
        return sb.toString();
    }

    public static boolean isMediaAvailable(String urI) {
        return true;
    }

    public static boolean fileTypeSupported(String fileType) {
        return true;
    }
}
