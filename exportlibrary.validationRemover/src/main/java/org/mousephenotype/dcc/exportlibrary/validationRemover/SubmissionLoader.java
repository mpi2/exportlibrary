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
package org.mousephenotype.dcc.exportlibrary.validationRemover;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.LoggerFactory;

public class SubmissionLoader {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(SubmissionLoader.class);
    private static final String SUBMISSION_LOAD_QUERY = "from Submission s where s.trackerID =:trackerID";
    private final HibernateManager hibernateManager;
    private List<Submission> submissions;

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public SubmissionLoader(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    public void load(long trackerID) {
        submissions = this.hibernateManager.query(SUBMISSION_LOAD_QUERY,
                ImmutableMap.<String, Object>builder().put("trackerID", trackerID).build(),
                Submission.class);
        logger.info("loaded {} submissions",submissions.size());
    }
}
