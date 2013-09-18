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

#http://tomcat.apache.org/maven-plugin-2.0-SNAPSHOT/archetype.html
#http://tomcat.apache.org/maven-plugin-2.0-beta-1/archetype.html
#https://developers.google.com/chart/interactive/docs/dev/implementing_data_source
#https://developers.google.com/chart/interactive/docs/dev/dsl_intro
# mvn tomcat7:run

mvn archetype:generate -DarchetypeGroupId=org.apache.tomcat.maven \
                       -DarchetypeArtifactId=tomcat-maven-archetype \
                       -DarchetypeVersion=2.0-beta-1 \
                       -DgroupId=org.mousephenotype.dcc.exportlibrary.wsrestful \
                       -DartifactId=exportlibrary.xmlvalidationresourcesviewer \
                       -DrootArtifactId=exportlibrary.xmlvalidationresourcesviewer \
                       -Dversion=1.0-SNAPSHOT   \
		       -DartifactId-api=exportlibrary.xmlvalidationresourcesviewer-api \
		       -DartifactId-api-impl=exportlibrary.xmlvalidationresourcesviewer-api-impl \
                       -DartifactId-webapp=exportlibrary.xmlvalidationresourcesviewer-webapp \
		       -DartifactId-webapp-exec=exportlibrary.xmlvalidationresourcesviewer-webapp-exec \
		       -DartifactId-webapp-it=exportlibrary.xmlvalidationresourcesviewer-webapp-it

