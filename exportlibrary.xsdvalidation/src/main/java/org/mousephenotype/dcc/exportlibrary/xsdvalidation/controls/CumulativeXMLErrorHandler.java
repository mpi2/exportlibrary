/**
 * Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
 *
 * MEDICAL RESEARCH COUNCIL UK MRC
 *
 * Harwell Mammalian Genetics Unit
 *
 * http://www.har.mrc.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.dcc.exportlibrary.xsdvalidation.controls;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author julian
 */
public class CumulativeXMLErrorHandler implements ErrorHandler {

    protected static final Logger logger = LoggerFactory.getLogger(CumulativeXMLErrorHandler.class);
    private List<SAXParseException> exceptions;

    public CumulativeXMLErrorHandler() {
        
    }

    public boolean errorsFound() {
        return this.exceptions != null && this.exceptions.size() > 0;
    }

    public List<SAXParseException> getExceptionMessages() {
        if (this.exceptions == null) {
            this.exceptions = new ArrayList<SAXParseException>();
        }
        return this.exceptions;
    }

    public String getFormattedExceptionMessages() {
        if (this.exceptions == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (SAXParseException exception : this.exceptions) {
            sb.append(exception.getSystemId());
            sb.append(" [");
            sb.append(exception.getLineNumber());
            sb.append(":");
            sb.append(exception.getColumnNumber());
            sb.append("] -- ");
            sb.append(exception.getPublicId());
            sb.append(" -- ||");
            sb.append(exception.getMessage());
            sb.append("|| \n");
        }
        return sb.toString();
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        this.getExceptionMessages().add(exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        this.getExceptionMessages().add(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        this.getExceptionMessages().add(exception);
    }
}
