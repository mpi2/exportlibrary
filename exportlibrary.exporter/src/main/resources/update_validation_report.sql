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

use phenodcc_raw;
--
--
UPDATE phenodcc_raw.VALIDATIONREPORT
SET VALIDATIONREPORT.SUPERSEEDED = true;
--
--
DROP TABLE IF EXISTS phenodcc_overviews.latest_experiments;
--
--
CREATE TABLE phenodcc_overviews.latest_experiments
    (
        REPORTIDENTIFIER VARCHAR(255),
        HJID bigint DEFAULT '0' NOT NULL,
        EXPERIMENTID VARCHAR(255),
        SEQUENCEID VARCHAR(255),
        PRIMARY KEY (HJID)
    )
    ENGINE=InnoDB DEFAULT CHARSET=latin1;
--
--
INSERT
INTO phenodcc_overviews.latest_experiments
    (
        REPORTIDENTIFIER,
        HJID,
        EXPERIMENTID,
        SEQUENCEID
    )
SELECT report.REPORTIDENTIFIER,
    report.hjid,
    EXPERIMENTID,
    SEQUENCEID
FROM phenodcc_raw.EXPERIMENT
JOIN
    (SELECT MAX(VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0) AS hjid,
            VALIDATIONREPORT.REPORTIDENTIFIER                    AS REPORTIDENTIFIER
        FROM phenodcc_raw.VALIDATIONREPORT
        WHERE VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0 IS NOT NULL
        GROUP BY VALIDATIONREPORT.REPORTIDENTIFIER) AS report ON EXPERIMENT.HJID = report.hjid;
--
--
UPDATE VALIDATIONREPORT                   AS ext,
    phenodcc_overviews.latest_experiments AS report
SET ext.SUPERSEEDED = false
WHERE ext.EXPERIMENT_VALIDATIONREPORT__0 =report.HJID;
--
--
DROP TABLE IF EXISTS phenodcc_overviews.latest_specimens;
--
--
CREATE TABLE phenodcc_overviews.latest_specimens
    (
        REPORTIDENTIFIER VARCHAR(255),
        HJID bigint,
        SPECIMENID VARCHAR(255),
        COLONYID VARCHAR(255),
        STRAINID VARCHAR(255),
        ISBASELINE TINYINT(1),
        PIPELINE VARCHAR(255),
        PHENOTYPINGCENTRE VARCHAR(255),
        PRODUCTIONCENTRE VARCHAR(255),
        PRIMARY KEY (HJID)
    )
    ENGINE=InnoDB DEFAULT CHARSET=latin1;
--
--
INSERT
INTO phenodcc_overviews.latest_specimens
    (
        REPORTIDENTIFIER,
        HJID,
        SPECIMENID,
        COLONYID,
        STRAINID,
        ISBASELINE,
        PIPELINE,
        PHENOTYPINGCENTRE,
        PRODUCTIONCENTRE
    )
SELECT report.REPORTIDENTIFIER,
    report.hjid,
    SPECIMENID,
    COLONYID,
    STRAINID,
    ISBASELINE,
    PIPELINE,
    PHENOTYPINGCENTRE,
    PRODUCTIONCENTRE
FROM phenodcc_raw.SPECIMEN
JOIN
    ( SELECT MAX(VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0) AS hjid,
            REPORTIDENTIFIER                                      AS REPORTIDENTIFIER
        FROM phenodcc_raw.VALIDATIONREPORT
        WHERE VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0 IS NOT NULL
        GROUP BY VALIDATIONREPORT.REPORTIDENTIFIER ) AS report ON SPECIMEN.HJID = report.hjid;
--
--
UPDATE VALIDATIONREPORT                 AS ext,
    phenodcc_overviews.latest_specimens AS report
SET ext.SUPERSEEDED = false
WHERE ext.SPECIMEN_VALIDATIONREPORT_HJ_0 =report.HJID;








