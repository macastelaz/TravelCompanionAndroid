package com.castelcode.cruisecompanion.utils_tests;

import com.castelcode.cruisecompanion.utils.DateStringUtil;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DateStringUtilTests {
    @Test
    public void stringToDate_validDate_test() throws Exception {
        String validDate = "2018.01.03";
        Date d = DateStringUtil.stringToDate(validDate);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        assertEquals(2018, c.get(Calendar.YEAR));
        assertEquals(0, c.get(Calendar.MONTH));
        assertEquals(3, c.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void stringToDate_invalidDate_test() throws Exception {
        String validDate = "01/01/2018";
        Date d = DateStringUtil.stringToDate(validDate);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        boolean datesSame = 2018 == c.get(Calendar.YEAR)
                && 0 == c.get(Calendar.MONTH) && 1 == c.get(Calendar.DAY_OF_MONTH);
        assertEquals(false, datesSame);
    }

    @Test
    public void convertToZeroIndexed_test() throws Exception {
        String validDate = "01/01/2018";
        String zeroIndexedValidDate = DateStringUtil.convertToZeroIndexed(validDate);
        assertNotEquals(validDate, zeroIndexedValidDate);
        assertNotEquals(validDate.split("/")[0], zeroIndexedValidDate.split("/")[0]);
        assertEquals("0", zeroIndexedValidDate.split("/")[0]);
    }

    @Test
    public void convertToOneIndexed_test() throws Exception {
        String validDate = "01/01/2018";
        String oneIndexedValidDate = DateStringUtil.convertToOneIndexed(validDate);
        assertNotEquals(validDate, oneIndexedValidDate);
        assertNotEquals(validDate.split("/")[0], oneIndexedValidDate.split("/")[0]);
        assertEquals("2", oneIndexedValidDate.split("/")[0]);

        String invalidDate = "-1/1/2018";
        String oneIndexedInvalidDate = DateStringUtil.convertToOneIndexed(invalidDate);
        assertEquals(invalidDate, oneIndexedInvalidDate);
    }

    @Test
    public void intToDateString_test() throws Exception {
        String dateString = DateStringUtil.intToDateString(1,1,2018);
        assertEquals("1/1/2018", dateString);
        dateString = DateStringUtil.intToDateString(0,1,2018);
        assertEquals("0/1/2018", dateString);
        dateString = DateStringUtil.intToDateString(12,12,2018);
        assertEquals("12/12/2018", dateString);
    }

    @Test
    public void calendarToString_test() throws Exception {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2018);
        c.set(Calendar.MONTH, 05);
        c.set(Calendar.DAY_OF_MONTH, 05);
        String dateString = DateStringUtil.calendarToString(c);
        assertEquals("5/5/2018", dateString);
    }

    @Test
    public void slashToDot_test() throws Exception {
        String dateString = "1/2/2018";
        String convertedDateString = DateStringUtil.slashToDot(dateString);
        assertEquals("2018.1.2", convertedDateString);
        dateString = "01/02/2018";
        convertedDateString = DateStringUtil.slashToDot(dateString);
        assertEquals("2018.1.2", convertedDateString);
    }

    @Test
    public void getDay_test() throws Exception {
        String dateString = "1/2/2018";
        int day = DateStringUtil.getDay(dateString);
        assertEquals(2, day);
        dateString = "2018.3.4";
        day = DateStringUtil.getDay(dateString);
        assertEquals(4, day);
        dateString = "2018-3-4";
        day = DateStringUtil.getDay(dateString);
        assertEquals(-1, day);
    }

    @Test
    public void getMonth_test() throws Exception {
        String dateString = "1/2/2018";
        int month = DateStringUtil.getMonth(dateString);
        assertEquals(1, month);
        dateString = "2018.3.4";
        month = DateStringUtil.getMonth(dateString);
        assertEquals(3, month);
        dateString = "2018-3-4";
        month = DateStringUtil.getMonth(dateString);
        assertEquals(-1, month);
    }

    @Test
    public void getYear_test() throws Exception {
        String dateString = "1/2/2018";
        int year = DateStringUtil.getYear(dateString);
        assertEquals(2018, year);
        dateString = "2017.3.4";
        year = DateStringUtil.getYear(dateString);
        assertEquals(2017, year);
        dateString = "2018-3-4";
        year = DateStringUtil.getYear(dateString);
        assertEquals(-1, year);
    }
}
