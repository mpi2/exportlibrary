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
package org.mousephenotype.dcc.exportlibrary.traverser.aux;

import com.google.common.collect.Table;
import java.util.Iterator;
import java.util.List;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation.Validation;

public class SubmissionCounter {

    private int valids = 0;
    private int invalids = 0;

    public void incValid() {
        this.valids++;
    }

    public void incInvalid() {
        this.invalids++;
    }

    public int getValids() {
        return this.valids;
    }

    public int getInvalids() {
        return this.invalids;
    }

    public void countSubmissions(Table<Submission, Boolean, List<Validation>> privateSubmissions) {

        Iterator<Table.Cell<Submission, Boolean, List<Validation>>> iterator = privateSubmissions.cellSet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getColumnKey()) {
                this.incValid();
            } else {
                this.incInvalid();
            }
        }

    }
}
