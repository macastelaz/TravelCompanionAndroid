package com.castelcode.travelcompanion.utils_tests;

import com.castelcode.travelcompanion.utils.TimeStringUtil;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class TimeStringUtilTests {
    @Test
    public void createTimeString_test() throws Exception {
        assertEquals("12:0", TimeStringUtil.createTimeString(12,0));
        assertEquals("3:15", TimeStringUtil.createTimeString(3,15));
        assertEquals("7:45", TimeStringUtil.createTimeString(7,45));
        assertEquals("12:45", TimeStringUtil.createTimeString(24,45));
        assertEquals("23:0", TimeStringUtil.createTimeString(23,70));
        assertEquals("12:0", TimeStringUtil.createTimeString(-1,-1));
    }

    @Test
    public void getSummaryString_test() throws Exception {
        assertEquals("3:00 AM", TimeStringUtil.getSummaryString("3:0"));
        assertEquals("12:00 AM", TimeStringUtil.getSummaryString("0:0"));
        assertEquals("12:00 PM", TimeStringUtil.getSummaryString("12:0"));
        assertEquals("12:15 AM", TimeStringUtil.getSummaryString("0:15"));
        assertEquals("11:59 PM", TimeStringUtil.getSummaryString("23:59"));
        assertEquals("11:59 PM", TimeStringUtil.getSummaryString("23:59"));
        assertEquals("Invalid Time", TimeStringUtil.getSummaryString("24:59"));
        assertEquals("Invalid Time", TimeStringUtil.getSummaryString("24:60"));
        assertEquals("Invalid Time", TimeStringUtil.getSummaryString("-1:59"));
    }

    @Test
    public void getHour_test() throws Exception {
        assertEquals(3, TimeStringUtil.getHour("3:15"));
        assertEquals(12, TimeStringUtil.getHour("12:15"));
        assertEquals(0, TimeStringUtil.getHour("0:15"));
        assertEquals(15, TimeStringUtil.getHour("15:15"));
        assertEquals(-1, TimeStringUtil.getHour("-3:15"));
        assertEquals(-1, TimeStringUtil.getHour("24:15"));
        assertEquals(-1, TimeStringUtil.getHour("Hello"));
    }

    @Test
    public void getMinute_test() throws Exception {
        assertEquals(15, TimeStringUtil.getMinute("3:15"));
        assertEquals(0, TimeStringUtil.getMinute("12:0"));
        assertEquals(-1, TimeStringUtil.getMinute("0:-5"));
        assertEquals(-1, TimeStringUtil.getMinute("15:330"));
        assertEquals(-1, TimeStringUtil.getMinute("Hello"));
    }

    @Test
    public void getAMorPM_test() throws Exception {
        assertEquals(Calendar.AM, TimeStringUtil.getAMorPM("3:15"));
        assertEquals(Calendar.PM, TimeStringUtil.getAMorPM("12:0"));
        assertEquals(Calendar.AM, TimeStringUtil.getAMorPM("12:00 AM"));
        assertEquals(Calendar.PM, TimeStringUtil.getAMorPM("12:00 PM"));
        assertEquals(-1, TimeStringUtil.getAMorPM("24:00"));
        assertEquals(-1, TimeStringUtil.getAMorPM("-1:00"));
        assertEquals(-1, TimeStringUtil.getAMorPM("Hello"));
    }
}
