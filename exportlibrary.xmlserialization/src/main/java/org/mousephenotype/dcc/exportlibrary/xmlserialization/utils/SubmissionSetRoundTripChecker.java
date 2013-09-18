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
package org.mousephenotype.dcc.exportlibrary.xmlserialization.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.persistence.*;
import javax.xml.bind.JAXBException;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.SubmissionSet;
import org.mousephenotype.dcc.utils.persistence.HibernateManager;
import org.mousephenotype.dcc.utils.persistence.RoundTripChecker;
import org.mousephenotype.dcc.utils.xml.XMLUtils;


/**
 *
 * @author julian
 */
public class SubmissionSetRoundTripChecker extends RoundTripChecker<SubmissionSet> {

    public SubmissionSetRoundTripChecker(String contextPath, String filename, HibernateManager hibernateManager) {
        super(contextPath, filename, hibernateManager);
    }

    @Override
    public SubmissionSet readFromDatabase() throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException, LockTimeoutException, PersistenceException {
        RoundTripChecker.logger.info("loading HJID: {}", this.fromFile.getHjid());        
        return this.hibernateManager.load(SubmissionSet.class, this.fromFile.getHjid());        
    }

    @Override
    public  SubmissionSet readFromFile() throws JAXBException, FileNotFoundException,IOException {
        return XMLUtils.unmarshal(this.contextPath, SubmissionSet.class, this.filename);
    }
}
