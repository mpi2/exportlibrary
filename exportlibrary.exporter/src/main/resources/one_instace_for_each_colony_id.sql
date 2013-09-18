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

SELECT SPECIMEN.*,
    EMBRYO.*,
    MOUSE.*,
    CASE
        WHEN EMBRYO.HJID IS NOT NULL
        THEN 1
        WHEN MOUSE.HJID IS NOT NULL
        THEN 2
    END AS clazz_
FROM SPECIMEN SPECIMEN
LEFT OUTER JOIN MOUSE MOUSE   ON SPECIMEN.HJID= MOUSE.HJID
LEFT OUTER JOIN EMBRYO EMBRYO ON SPECIMEN.HJID= EMBRYO.HJID
JOIN phenodcc_context.context ON SPECIMEN.HJID = context.subject
WHERE SPECIMEN.COLONYID IS NOT NULL
AND SPECIMEN.COLONYID != 'baseline'

AND (SPECIMEN.PHENOTYPINGCENTRE NOT IN ('BAY',
                                       'NCOM') or SPECIMEN.PHENOTYPINGCENTRE is null)
AND (SPECIMEN.PRODUCTIONCENTRE NOT IN ('BAY',
                                      'NCOM') or SPECIMEN.PRODUCTIONCENTRE is null)
                                      
--AND SPECIMEN.COLONYID = 'BL1487'
AND context.isActive
AND context.isValid
GROUP BY SPECIMEN.COLONYID