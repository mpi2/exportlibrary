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

--
CREATE TABLE phenodcc_overviews.valid_experiments
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
INTO phenodcc_overviews.valid_experiments
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
        AND VALIDATIONREPORT.ISVALID
        GROUP BY VALIDATIONREPORT.REPORTIDENTIFIER) AS report ON EXPERIMENT.HJID = report.hjid;
--
--
DROP TABLE IF EXISTS phenodcc_overviews.valid_specimens;
--
--
CREATE TABLE phenodcc_overviews.valid_specimens
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
INTO phenodcc_overviews.valid_specimens
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
        WHERE VALIDATIONREPORT.ISVALID
        AND VALIDATIONREPORT.SPECIMEN_VALIDATIONREPORT_HJ_0 IS NOT NULL
        GROUP BY VALIDATIONREPORT.REPORTIDENTIFIER ) AS report ON SPECIMEN.HJID = report.hjid;
--
--
DROP TABLE IF EXISTS phenodcc_overviews.valid_experiments_with_valid_expecimens;
--
--
CREATE TABLE phenodcc_overviews.valid_experiments_with_valid_expecimens
    (
        REPORTIDENTIFIER VARCHAR(255),
        HJID bigint,
        EXPERIMENTID VARCHAR(255),
        SEQUENCEID VARCHAR(255),
        PRIMARY KEY (HJID)
    )
    ENGINE=InnoDB DEFAULT CHARSET=latin1;
--
--
INSERT
INTO phenodcc_overviews.valid_experiments_with_valid_expecimens
    (
        REPORTIDENTIFIER,
        HJID,
        EXPERIMENTID,
        SEQUENCEID
    )
SELECT valid_experiments.*
FROM phenodcc_overviews.valid_experiments
JOIN phenodcc_raw.EXPERIMENT_SPECIMENID ON valid_experiments.HJID = EXPERIMENT_SPECIMENID.HJID
JOIN phenodcc_overviews.valid_specimens                    ON EXPERIMENT_SPECIMENID.HJVALUE = valid_specimens.SPECIMENID;
--
--
DROP TABLE IF EXISTS phenodcc_overviews.experiment_specimen;
--
--
CREATE TABLE phenodcc_overviews.experiment_specimen
    (
        experiment_REPORTIDENTIFIER VARCHAR(255),
        specimen_REPORTIDENTIFIER VARCHAR(255),
        EXPERIMENTID VARCHAR(255),
        experiment_SEQUENCEID VARCHAR(255),
        SPECIMENID VARCHAR(255),
        COLONYID VARCHAR(255),
        STRAINID VARCHAR(255),
        ISBASELINE TINYINT(1),
        experiment_HJID bigint,
        specimen_HJID bigint,
        PRIMARY KEY (experiment_HJID, specimen_HJID)
    )
    ENGINE=InnoDB DEFAULT CHARSET=latin1;
--
--
INSERT
INTO phenodcc_overviews.experiment_specimen
    (
        experiment_REPORTIDENTIFIER,
        specimen_REPORTIDENTIFIER,
        EXPERIMENTID,
        experiment_SEQUENCEID,
        SPECIMENID,
        COLONYID,
        STRAINID,
        ISBASELINE,
        experiment_HJID,
        specimen_HJID
    )
SELECT valid_experiments_with_valid_expecimens.REPORTIDENTIFIER AS experiment_REPORTIDENTIFIER,
    phenodcc_overviews.valid_specimens.REPORTIDENTIFIER         AS specimen_REPORTIDENTIFIER,
    valid_experiments_with_valid_expecimens.EXPERIMENTID        AS EXPERIMENTID,
    valid_experiments_with_valid_expecimens.SEQUENCEID          AS experiment_SEQUENCEID,
    phenodcc_overviews.valid_specimens.SPECIMENID               AS SPECIMENID,
    phenodcc_overviews.valid_specimens.COLONYID                 AS COLONYID,
    phenodcc_overviews.valid_specimens.STRAINID                 AS STRAINID,
    phenodcc_overviews.valid_specimens.ISBASELINE               AS ISBASELINE,
    valid_experiments_with_valid_expecimens.HJID                AS experiment_HJID,
    valid_specimens.HJID                                        AS specimen_HJID
FROM phenodcc_overviews.valid_experiments_with_valid_expecimens
JOIN phenodcc_raw.EXPERIMENT_SPECIMENID ON valid_experiments_with_valid_expecimens.HJID = EXPERIMENT_SPECIMENID.HJID
JOIN phenodcc_overviews.valid_specimens ON EXPERIMENT_SPECIMENID.HJVALUE = valid_specimens.SPECIMENID
    --group by valid_experiments_with_valid_expecimens.REPORTIDENTIFIER
    --having count(1) >1
    --order by count(1) desc