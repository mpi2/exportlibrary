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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JodaTimeTests {

    private static final Logger logger = LoggerFactory.getLogger(JodaTimeTests.class);

    @Test
    public void testDurations() {
        DateTime start = org.joda.time.DateTime.now(DateTimeZone.UTC);
        long timeout = 1000;
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        DateTime finish = org.joda.time.DateTime.now(DateTimeZone.UTC);
        Interval interval = new Interval(start, finish);
        Period toPeriod = interval.toPeriod();
        PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" day", " days")
                .appendSeparator(" and ")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .appendSeparator(" and ")
                .appendSeconds()
                .appendSuffix(" second", " seconds")
                .toFormatter();

        logger.info("[{}]", daysHoursMinutes.print(toPeriod));
    }

    @Test
    public void testDurations2() {
        Calendar start = DatatypeConverter.now();
        start.roll(Calendar.DAY_OF_MONTH, -2);
        Calendar finish = DatatypeConverter.now();
        logger.info("diff {}", DatatypeConverter.getDuration(start, finish));
        Assert.assertEquals("2 days", DatatypeConverter.getDuration(start, finish));
    }

    @Test
    public void utcTest() {

        //lexicalDateTime is a datetime with no zone
        String lexicalDateTime = "2012-06-30T02:45:00";
        String READ_xmlPattern = "yyyy-MM-dd'T'HH:mm:ss";
        String dateTimeXMLpattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        //dateTime is the lexicalDatetime but with your timezone
        org.joda.time.DateTime dateTime = org.joda.time.DateTime.parse(lexicalDateTime, DateTimeFormat.forPattern(READ_xmlPattern).withZone(DateTimeZone.forID("US/Eastern")));
        String dateTimewithLocale = dateTime.toString(dateTimeXMLpattern);
        logger.info("lexicalDateTime {}", lexicalDateTime); //prints       2012-06-30T02:45:00
        logger.info("dateTimeWithLocale {}", dateTimewithLocale);//prints  2012-06-30T02:45:00-0400
        //calendarToJava2XML parses your lexicalDateTime with your localzone into a calendar object, that the xml libraries can deal with
        Calendar calendarToJava2XML = null;
        try {
            calendarToJava2XML = (org.joda.time.DateTime.parse(dateTimewithLocale, DateTimeFormat.forPattern(dateTimeXMLpattern).withZoneUTC())).toGregorianCalendar();
        } catch (Exception ex) {
            logger.error("can't parse {}", dateTimewithLocale, ex);
            Assert.fail();
        }
        String mylocaleToUTC = (new DateTime(calendarToJava2XML, ISOChronology.getInstance()).withZone(DateTimeZone.UTC)).toString(dateTimeXMLpattern);
        logger.info("my locale to UTC{}", mylocaleToUTC); //prints         2012-06-30T06:45:00+0000
        Assert.assertEquals("2012-06-30T06:45:00+0000", mylocaleToUTC);

    }
}
