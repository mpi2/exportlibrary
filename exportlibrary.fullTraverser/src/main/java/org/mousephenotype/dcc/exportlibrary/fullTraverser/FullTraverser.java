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
package org.mousephenotype.dcc.exportlibrary.fullTraverser;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.hibernate.type.LongType;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation_report.ResourceVersion;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FullTraverser {

    private static final Logger logger = LoggerFactory.getLogger(FullTraverser.class);
    private final HibernateManager hibernateManager;
    private List<java.math.BigInteger> trackerIDs;
    private Traverser traverser;

    public FullTraverser(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    private void loadSubmissionTrackerIDs(Calendar date) {
        if (date == null) {
            logger.info("loading every submission by trackerID");
            trackerIDs = this.hibernateManager.nativeQuery("select distinct trackerID from SUBMISSION  where trackerID >= 5867 order by trackerID");
        } else {
            Table<String, Class, Object> parameters = HashBasedTable.create();
            parameters.put("colonyID", Calendar.class, date);

            Map<String, org.hibernate.type.Type> scalars = ImmutableMap.<String, org.hibernate.type.Type>builder().put("trackerID", LongType.INSTANCE).build();

            trackerIDs = this.hibernateManager.nativeQuery("select distinct trackerID from SUBMISSION submission order by trackerID where submission.submissionDate < :submissionDate", scalars, parameters);
        }
    }

    public void run(Calendar date) {
        this.loadSubmissionTrackerIDs(date);
        for (java.math.BigInteger trackerID : this.trackerIDs) {
            logger.info("running for trackerID {}", trackerID);
            Calendar start = DatatypeConverter.now();
            logger.info("running traverser started at {} ", DatatypeConverter.printDateTime(start));
            traverser = new Traverser(hibernateManager);
            traverser.run(trackerID.longValue(), new ResourceVersion());
            hibernateManager.getEntityManager().close();
            Calendar finish = DatatypeConverter.now();
            logger.info("finished traverser at {}", DatatypeConverter.printDateTime(finish));
            logger.info("run traverser, lasted for {}", DatatypeConverter.getDuration(start, finish));
        }
    }
}
