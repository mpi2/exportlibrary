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

SELECT COUNT(1), phenodcc_raw.CENTREPROCEDURE.CENTREID, phenodcc_raw.PROCEDURE_.PROCEDUREID
FROM phenodcc_raw.SUBMISSIONSET
    /*1*/
JOIN phenodcc_raw.SUBMISSION ON SUBMISSIONSET.HJID = SUBMISSION.SUBMISSION_SUBMISSIONSET_HJID
    /*2*/
JOIN phenodcc_raw.CENTREPROCEDURE ON SUBMISSION.HJID = CENTREPROCEDURE.CENTREPROCEDURE_SUBMISSION_H_0
    /*a1*/
JOIN phenodcc_raw.EXPERIMENT       ON CENTREPROCEDURE.HJID = EXPERIMENT.EXPERIMENT_CENTREPROCEDURE_H_0
--
JOIN phenodcc_raw.VALIDATIONREPORT ON EXPERIMENT.HJID = VALIDATIONREPORT.EXPERIMENT_VALIDATIONREPORT__0
    /*a2*/
JOIN phenodcc_raw.PROCEDURE_ ON EXPERIMENT.PROCEDURE__EXPERIMENT_HJID = PROCEDURE_.HJID
    /*a3*/
LEFT JOIN phenodcc_raw.SIMPLEPARAMETER ON PROCEDURE_.HJID = SIMPLEPARAMETER.SIMPLEPARAMETER_PROCEDURE__H_0
    --
LEFT JOIN phenodcc_raw.ONTOLOGYPARAMETER      ON PROCEDURE_.HJID = ONTOLOGYPARAMETER.ONTOLOGYPARAMETER_PROCEDURE__0
LEFT JOIN phenodcc_raw.ONTOLOGYPARAMETER_TERM ON ONTOLOGYPARAMETER.HJID = ONTOLOGYPARAMETER_TERM.HJID
    --
LEFT JOIN phenodcc_raw.SERIESPARAMETER      ON PROCEDURE_.HJID = SERIESPARAMETER.SERIESPARAMETER_PROCEDURE__H_0
LEFT JOIN phenodcc_raw.SERIESPARAMETERVALUE ON SERIESPARAMETER.HJID = SERIESPARAMETERVALUE.VALUE__SERIESPARAMETER_HJID
    --
LEFT JOIN phenodcc_raw.MEDIAPARAMETER       ON PROCEDURE_.HJID = MEDIAPARAMETER.MEDIAPARAMETER_PROCEDURE__HJ_0
LEFT JOIN phenodcc_raw.PARAMETERASSOCIATION ON MEDIAPARAMETER.HJID =PARAMETERASSOCIATION.PARAMETERASSOCIATION_MEDIAPA_0
LEFT JOIN phenodcc_raw.DIMENSION            ON PARAMETERASSOCIATION.HJID =DIMENSION.DIM_PARAMETERASSOCIATION_HJID
    --LEFT JOIN phenodcc_raw.PROCEDUREMETADATA    ON MEDIAPARAMETER.HJID =PROCEDUREMETADATA.PROCEDUREMETADATA_MEDIAPARAM_0
    --
LEFT JOIN phenodcc_raw.MEDIASAMPLEPARAMETER ON PROCEDURE_.HJID = MEDIASAMPLEPARAMETER.MEDIASAMPLEPARAMETER_PROCEDU_0
LEFT JOIN phenodcc_raw.MEDIASAMPLE          ON MEDIASAMPLEPARAMETER.HJID = MEDIASAMPLE.MEDIASAMPLE_MEDIASAMPLEPARAM_0
LEFT JOIN phenodcc_raw.MEDIASECTION         ON MEDIASAMPLE.HJID = MEDIASECTION.MEDIASECTION_MEDIASAMPLE_HJID
LEFT JOIN phenodcc_raw.MEDIAFILE            ON MEDIASECTION.HJID = MEDIAFILE.MEDIAFILE_MEDIASECTION_HJID
    --LEFT JOIN phenodcc_raw.PROCEDUREMETADATA    AS pd2 ON MEDIAFILE.HJID = PROCEDUREMETADATA.PROCEDUREMETADATA_MEDIAFILE__0
LEFT JOIN phenodcc_raw.PARAMETERASSOCIATION AS pa2 ON MEDIAFILE.HJID =pa2.PARAMETERASSOCIATION_MEDIAFI_0
LEFT JOIN phenodcc_raw.DIMENSION            AS d2  ON PARAMETERASSOCIATION.HJID =d2.DIM_PARAMETERASSOCIATION_HJID
    --
LEFT JOIN phenodcc_raw.SERIESMEDIAPARAMETER      ON PROCEDURE_.HJID = SERIESMEDIAPARAMETER.SERIESMEDIAPARAMETER_PROCEDU_0
LEFT JOIN phenodcc_raw.SERIESMEDIAPARAMETERVALUE ON SERIESMEDIAPARAMETER.HJID = SERIESMEDIAPARAMETERVALUE.VALUE__SERIESMEDIAPARAMETER__0
LEFT JOIN phenodcc_raw.PARAMETERASSOCIATION pa3  ON MEDIAPARAMETER.HJID =pa3.PARAMETERASSOCIATION_MEDIAPA_0
LEFT JOIN phenodcc_raw.DIMENSION d3              ON PARAMETERASSOCIATION.HJID =d3.DIM_PARAMETERASSOCIATION_HJID
    --LEFT JOIN phenodcc_raw.PROCEDUREMETADATA AS pd3  ON SERIESMEDIAPARAMETER.HJID = PROCEDUREMETADATA.PROCEDUREMETADATA_SERIESMEDI_0
    --LEFT JOIN phenodcc_raw.PROCEDUREMETADATA AS pd4  ON SERIESMEDIAPARAMETERVALUE.HJID = PROCEDUREMETADATA.PROCEDUREMETADATA_SERIESMEDI_1
    --
    --LEFT JOIN phenodcc_raw.PROCEDUREMETADATA AS pp ON PROCEDURE_.HJID = pp.PROCEDUREMETADATA_PROCEDURE__0
WHERE VALIDATIONREPORT.ISVALID
AND NOT VALIDATIONREPORT.SUPERSEEDED

group by CENTREPROCEDURE.CENTREID, PROCEDURE_.PROCEDUREID

order by CENTREPROCEDURE.CENTREID, PROCEDURE_.PROCEDUREID