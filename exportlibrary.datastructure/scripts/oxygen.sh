#
# Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
#
# MEDICAL RESEARCH COUNCIL UK MRC
#
# Harwell Mammalian Genetics Unit
#
# http://www.har.mrc.ac.uk
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#

sh /opt/oxygen/schemaDocumentation.sh ../src/main/resources/schemas/core/procedure_definition.xsd  -out:../../../../../docs/procedure_definition.pdf -format:pdf

sh /opt/oxygen14/Oxygen\ XML\ Editor\ 14/schemaDocumentation.sh ../src/main/resources/schemas/core/procedure_definition.xsd -out:../../../../../docs/procedure_definition.pdf -format:pdf

sh /opt/oxygen14/Oxygen\ XML\ Editor\ 14/schemaDocumentation.sh ../src/main/resources/schemas/core/specimen_definition.xsd -out:../../../../../docs/specimen_definition.pdf -format:pdf

sh /opt/oxygen14/Oxygen\ XML\ Editor\ 14/schemaDocumentation.sh ../src/main/resources/schemas/tracker/submission.xsd -out:../../../../../docs/submission.pdf -format:pdf

sh /opt/oxygen14/Oxygen\ XML\ Editor\ 14/schemaDocumentation.sh ../src/main/resources/schemas/tracker/validation.xsd -out:../../../../../docs/validation.pdf -format:pdf

