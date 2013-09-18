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
package org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.converters;

import java.util.Calendar;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class DatatypeConverter {

    private static final Logger logger = LoggerFactory.getLogger(DatatypeConverter.class);
    public static final String dateTimeXMLpattern = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String dateXMLpattern = "yyyy-MM-dd";
    public static final String timeXMLpattern = "HH:mm:ss";

    public static Calendar parseDate(String lexicalDate) {
        if (lexicalDate == null || lexicalDate.equals("")) {
            logger.trace("parsing is null or empty");
            return null;
        }
        logger.trace("parsing date {}", lexicalDate);
        LocalDate localDate = LocalDate.parse(lexicalDate, DateTimeFormat.forPattern(dateXMLpattern));
        return localDate.toDateTimeAtStartOfDay().toGregorianCalendar();
    }

    public static String printDate(Calendar date) {
        if (date == null) {
            logger.trace("date is null");
            return null;
        }
        logger.trace("printing dateTime {}", date.toString());
        LocalDate localDate = new LocalDate(date, ISOChronology.getInstance());
        return localDate.toString(dateXMLpattern);
    }

    public static Calendar parseTime(String lexicalDate) {
        if (lexicalDate == null || lexicalDate.equals("")) {
            logger.trace("parsing is null or empty");
            return null;
        }
        logger.trace("parsing time {}", lexicalDate);
        LocalTime localTime = LocalTime.parse(lexicalDate, DateTimeFormat.forPattern(timeXMLpattern));
        return localTime.toDateTime(new DateTime(0)).toGregorianCalendar();
    }

    public static String printTime(Calendar date) {
        if (date == null) {
            logger.trace("date is null");
            return null;
        }
        logger.trace("printing time {}", date.toString());
        LocalTime localTime = new LocalTime(date, ISOChronology.getInstance());
        return localTime.toString(timeXMLpattern);
    }

    public static Calendar parseDateTime(String lexicalDate) {
        if (lexicalDate == null || lexicalDate.equals("")) {
            logger.trace("parsing is null or empty");
            return null;
        }
        logger.trace("parsing dateTime {}", lexicalDate);
        org.joda.time.DateTime dateTime = org.joda.time.DateTime.parse(lexicalDate, DateTimeFormat.forPattern(dateTimeXMLpattern).withZoneUTC());
        return dateTime.toGregorianCalendar();
    }

    public static String printDateTime(Calendar date) {
        if (date == null) {
            logger.trace("date is null");
            return null;
        }
        logger.trace("printing dateTime {}", date.toString());
        DateTime dateTime = new DateTime(date, ISOChronology.getInstance()).withZone(DateTimeZone.UTC);
        return dateTime.toString(dateTimeXMLpattern);
    }

    public static Calendar now() {
        return org.joda.time.DateTime.now(DateTimeZone.UTC).toGregorianCalendar();
    }
}
