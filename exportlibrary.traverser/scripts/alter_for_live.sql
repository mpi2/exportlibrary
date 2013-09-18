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


CREATE TABLE VALIDATION_REPORT_VALIDATION
    (
        validationreport bigint NOT NULL,
        validation bigint NOT NULL,
        PRIMARY KEY (validationreport, validation),
        CONSTRAINT FKFDC58A1E625A1C5B FOREIGN KEY (validationreport) REFERENCES VALIDATIONREPORT (HJID) ,
        CONSTRAINT FKFDC58A1ED279EDFA FOREIGN KEY (validation) REFERENCES VALIDATION (HJID),
        INDEX FKFDC58A1ED279EDFA (validation),
        INDEX FKFDC58A1E625A1C5B (validationreport)
    )
    ENGINE=InnoDB DEFAULT CHARSET=latin1;


SET foreign_key_checks = 0;

truncate table VALIDATIONREPORTSET;
truncate table VALIDATIONREPORT;
truncate table VALIDATION_REPORT_VALIDATION;
SET foreign_key_checks = 1;
    
 
alter table VALIDATIONREPORT add column VALIDATIONREPORT_HJORDER INT;