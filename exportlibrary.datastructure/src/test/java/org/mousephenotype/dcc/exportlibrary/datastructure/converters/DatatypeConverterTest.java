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
package org.mousephenotype.dcc.exportlibrary.datastructure.converters;

import java.util.Calendar;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian http://joda-time.sourceforge.net/userguide.html
 * http://docs.oracle.com/javase/tutorial/i18n/format/simpleDateFormat.html
 * http://blog.frankel.ch/customize-your-jaxb-bindings
 * http://stackoverflow.com/questions/5106987/jax-ws-and-joda-time
 * http://blog.jadira.co.uk/jadira-support/
 * http://usertype.sourceforge.net/userguide.html
 * http://joda-time.sourceforge.net/contrib/hibernate/
 * http://joda-time.sourceforge.net/timezones.html
 * http://www.odi.ch/prog/design/datetime.php
 *
 */
public class DatatypeConverterTest {

    private static final Logger logger = LoggerFactory.getLogger(DatatypeConverterTest.class);

    /*
     * With joda you can safely cast a string representation of a date, to a 
     * calendar object (can be stored in a mysql DATE datatype using hibernate Temporal.DATE)
     * Bear in mind that for datetime both parsing from xml and printing to xml are done transforing the datetime to UTC
     */
    @Test
    public void testDate() {
        String lexicalDate = "2012-06-30";
        Calendar parseDate = null;
        try {
            parseDate = DatatypeConverter.parseDate(lexicalDate);
        } catch (Exception ex) {
            logger.error("can't parse {}", lexicalDate, ex);
            Assert.fail();
        }
        Assert.assertNotNull(parseDate);
        String printedDate = DatatypeConverter.printDate(parseDate);
        Assert.assertEquals(lexicalDate, printedDate);
    }

    @Test
    public void testTime() {
         String lexicalDate = "02:45:00";
         Calendar parseTime = null;
        try {
            parseTime = DatatypeConverter.parseTime(lexicalDate);
        } catch (Exception ex) {
            logger.error("can't parse {}", lexicalDate, ex);
            Assert.fail();
        }
        Assert.assertNotNull(parseTime);
        String printedTime = DatatypeConverter.printTime(parseTime);
        Assert.assertEquals(lexicalDate, printedTime);
    }

    @Test
    public void forJAX(){
        String lexicalDateTime = "2012-06-30T02:45:00";
        String READ_xmlPattern = "yyyy-MM-dd'T'HH:mm:ss";
        org.joda.time.DateTime dateTime = org.joda.time.DateTime.parse(lexicalDateTime, DateTimeFormat.forPattern(READ_xmlPattern).withZone(DateTimeZone.forID("US/Eastern")));
        logger.info("date time in Jax {}",dateTime.toString());
        org.joda.time.DateTime dateTimeUTC = dateTime.withZone(DateTimeZone.UTC);
        logger.info("date time in UTC {}",dateTimeUTC.toString());
        Calendar calendarToJava2XML = dateTimeUTC.toGregorianCalendar();
    }
    
    @Test
    public void forJAX2(){
        String lexicalDateTime = "2012-06-30T02:45:00";
        String READ_xmlPattern = "yyyy-MM-dd'T'HH:mm:ss";
        String dateTimeXMLpattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        org.joda.time.DateTime dateTime = org.joda.time.DateTime.parse(lexicalDateTime, DateTimeFormat.forPattern(READ_xmlPattern).withZone(DateTimeZone.forID("US/Eastern")));
        String dateTimewithLocale = dateTime.toString(dateTimeXMLpattern);
        logger.info("lexicalDateTime {}",lexicalDateTime);
        logger.info("dateTimeWithLocale {}", dateTimewithLocale);
        Calendar calendarToJava2XML = null;
        try {
            calendarToJava2XML = DatatypeConverter.parseDateTime(dateTimewithLocale);
        } catch (Exception ex) {
            logger.error("can't parse {}", dateTimewithLocale, ex);
            Assert.fail();
        }
        logger.info("my locale to UTC {}", DatatypeConverter.printDateTime(calendarToJava2XML));
        Assert.assertEquals("2012-06-30T06:45:00+0000", DatatypeConverter.printDateTime(calendarToJava2XML));
    }
    
    @Test
    public void testDateTime() {
        String lexicalDate1 = "2012-09-04T12:50:00+0000";
        Calendar parseDateTime = null;
        try {
            parseDateTime = DatatypeConverter.parseDateTime(lexicalDate1);
        } catch (Exception ex) {
            logger.error("can't parse {}", lexicalDate1, ex);
            Assert.fail();
        }
        Assert.assertNotNull(parseDateTime);

        String printedDateTime = DatatypeConverter.printDateTime(parseDateTime);

        Assert.assertEquals(lexicalDate1, printedDateTime);

        lexicalDate1 = "2013-03-13T09:30:02+0000";

        parseDateTime = null;
        try {
            parseDateTime = DatatypeConverter.parseDateTime(lexicalDate1);
        } catch (Exception ex) {
            logger.error("can't parse {}", lexicalDate1, ex);
            Assert.fail();
        }
        Assert.assertNotNull(parseDateTime);
        printedDateTime = DatatypeConverter.printDateTime(parseDateTime);

        Assert.assertEquals("2013-03-13T09:30:02+0000", printedDateTime);
    }
    @Test
    public void testCentres(){
        Assert.assertEquals(CentreILARcode.BCM, CentreILARcode.BCM);
    }
}
