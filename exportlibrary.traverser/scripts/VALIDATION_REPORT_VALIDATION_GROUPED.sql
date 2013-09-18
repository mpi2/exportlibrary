--
-- Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
--
-- MEDICAL RESEARCH COUNCIL UK MRC
--
-- Harwell Mammalian Genetics Unit
--
-- http://www.har.mrc.ac.uk
--
-- Licensed under the Apache License, Version 2.0 (the "License"); you may not
-- use this file except in compliance with the License. You may obtain a copy of
-- the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
-- WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
-- License for the specific language governing permissions and limitations under
-- the License.
--

DROP TABLE IF EXISTS VALIDATION_REPORT_VALIDATION_GROUPED;
CREATE TABLE VALIDATION_REPORT_VALIDATION_GROUPED
    (
        validationreport bigint NOT NULL,
        validation bigint NOT NULL,
        PRIMARY KEY (validationreport, validation),
        CONSTRAINT FKFDC58A1E625A1C5Bb FOREIGN KEY (validationreport) REFERENCES VALIDATIONREPORT (HJID) ,
        CONSTRAINT FKFDC58A1ED279EDFAa FOREIGN KEY (validation) REFERENCES VALIDATION (HJID),
        INDEX FKFDC58A1ED279EDFAA (validation),
        INDEX FKFDC58A1E625A1C5BB (validationreport)
    )
    ENGINE=InnoDB DEFAULT CHARSET=latin1;
INSERT
INTO VALIDATION_REPORT_VALIDATION_GROUPED
SELECT validationreport,
    validation
FROM phenodcc_raw_m2m.VALIDATION_REPORT_VALIDATION
GROUP BY validationreport,
    validation;
