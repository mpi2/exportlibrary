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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



/**
 *
 * @author julian
 */
public abstract class XMLValidityChecker {

    protected static final Logger logger = LoggerFactory.getLogger(XMLValidityChecker.class);
    protected SchemaFactory schemaFactory;
    protected Schema schema;
    protected Validator validator;
    protected CumulativeXMLErrorHandler handler;
    protected boolean fileChecked = false;

    public final void init(URL xsdURL) throws SAXException {
        this.schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        this.schema = this.schemaFactory.newSchema(xsdURL);
        this.validator = this.schema.newValidator();
    }

    public final void init(String xsdFilename) throws SAXException {
        this.schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        //SchemaFactory does not have an error handler as the xsd is supposed to be right!!
        this.schema = this.schemaFactory.newSchema(new File(xsdFilename));
        this.validator = this.schema.newValidator();
    }

    public XMLValidityChecker() {
    }

    public XMLValidityChecker(URL xsdURL) throws SAXException {
        this.init(xsdURL);
    }

    public XMLValidityChecker(String xsdFilename) throws SAXException {
        this.init(xsdFilename);
    }

    public void attachErrorHandler() {
        this.handler = new CumulativeXMLErrorHandler();
        this.validator.setErrorHandler(this.handler);
    }

    public CumulativeXMLErrorHandler getHandler() {
        return this.handler;
    }

    public boolean errorsFound() {
        return this.fileChecked && this.handler.errorsFound();
    }

    public List<SAXParseException> getExceptionMessages() {
        return this.handler.getExceptionMessages();
    }

    public String getErrorDescriptions() {
        if (this.fileChecked && this.handler.errorsFound()) {
            return this.handler.getFormattedExceptionMessages();
        }
        return null;
    }

    public void checkWithHandler(String filename) throws SAXException, IOException {
        this.validator.validate(new StreamSource(filename));
        this.fileChecked = true;
    }

    public void check(String filename) throws IOException, SAXException, SAXParseException {
        StreamSource streamSource = new StreamSource(filename);
        this.validator.validate(streamSource);
        this.fileChecked = true;
    }
}
