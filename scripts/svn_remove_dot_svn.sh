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


rm -rf `find . -type d -name .svn`

svn delete  svn://source.har.mrc.ac.uk/PhenoDCC/exportlibrary/trunk/exportlibrary -m "working copy broken"

svn import exportlibrary svn://source.har.mrc.ac.uk/PhenoDCC/exportlibrary/trunk/exportlibrary -m "first commit after broken working copy"

svn co  svn://source.har.mrc.ac.uk/PhenoDCC/exportlibrary/trunk/exportlibrary



svn delete  svn://source.har.mrc.ac.uk/PhenoDCC/exportlibrary/trunk/exportlibrary/exportlibrary.utils/src/test/db/

svn delete  svn://source.har.mrc.ac.uk/PhenoDCC/exportlibrary/trunk/exportlibrary/exportlibrary.xmlvalidation/hsqldb/ -m "remove test dabases"

svn delete  svn://source.har.mrc.ac.uk/PhenoDCC/exportlibrary/trunk/exportlibrary/exportlibrary.xmlvalidation/src/main/generated/ -m "removed generated classes"
