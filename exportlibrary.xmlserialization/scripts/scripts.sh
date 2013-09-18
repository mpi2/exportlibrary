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





java -jar exportlibrary.xmlserialization-1.1-SNAPSHOT-jar-with-dependencies.jar  -d mysql_richard_importLib.properties
java -jar exportlibrary.xmlserialization-1.1-SNAPSHOT-jar-with-dependencies.jar  -d mysql_richard_importLib.properties -s Davis.cohort.1.xml  -t 1 -r 2012-10-05T09:00:00+0100
java -jar exportlibrary.xmlserialization-1.1-SNAPSHOT-jar-with-dependencies.jar  -d mysql_richard_importLib.properties -s Baylor.cohort.xml  -t 2 -r 2012-10-05T09:00:00+0100

java -jar exportlibrary.xmlserialization-1.1-SNAPSHOT-jar-with-dependencies.jar  -d mysql_richard_importLib.properties -p Baylor.experiment.1.xml  -t 3 -r 2012-10-05T09:00:00+0100
java -jar exportlibrary.xmlserialization-1.1-SNAPSHOT-jar-with-dependencies.jar  -d mysql_richard_importLib.properties -p Davis.experiment.1.xml   -t 4 -r 2012-10-05T09:00:00+0100
