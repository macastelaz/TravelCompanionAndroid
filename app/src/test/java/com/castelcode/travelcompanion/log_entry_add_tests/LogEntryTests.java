package com.castelcode.travelcompanion.log_entry_add_tests;

import com.castelcode.travelcompanion.log_entry_add_activity.LogEntry;
import com.castelcode.travelcompanion.utils.DateStringUtil;
import com.castelcode.travelcompanion.utils.TimeStringUtil;

import org.junit.Test;
import java.util.Calendar;
import static org.junit.Assert.assertEquals;

public class LogEntryTests {
    @Test
    public void LogEntry_creation() throws Exception {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 0);
        LogEntry logEntry = new LogEntry("Text", "Select a date", "Select a time");
        assertEquals(String.valueOf(c.get(Calendar.YEAR)) +
                String.valueOf(c.get(Calendar.MONTH)) +
                String.valueOf(c.get(Calendar.DAY_OF_MONTH)) +
                String.valueOf(c.get(Calendar.HOUR_OF_DAY)) +
                String.valueOf(c.get(Calendar.MINUTE)) +
                String.valueOf(c.get(Calendar.SECOND)), logEntry.getFileName());
    }

    @Test
    public void GetDateTimeAsString() throws Exception {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2018);
        c.set(Calendar.MONTH, 2);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        LogEntry logEntry = new LogEntry("Text", DateStringUtil.calendarToString(c),
                TimeStringUtil.createTimeString(c.get(Calendar.HOUR), c.get(Calendar.MINUTE)));

        assertEquals("2/1/2018 10:00:00AM", logEntry.getDateTimeAsString());

    }

    @Test
    public void GetTextEntryPreview() throws Exception {
        LogEntry logEntry = new LogEntry("Text", "Select a date", "Select a time");
        assertEquals("Text", logEntry.getTextPreview());
        logEntry = new LogEntry(
                "Text with a really long sentence but is exactly 89 characters to make sure it " +
                        "all appears", "Select a date", "Select a time");
        assertEquals("Text with a really long sentence but is exactly 89 characters to make sure it "
                + "all appears", logEntry.getTextPreview());
        logEntry = new LogEntry(
                "Text with a really long sentence but is more than 89 characters to make sure " +
                        "it is summarized.", "Select a date", "Select a time");
        assertEquals("Text with a really long sentence but is more than 89 characters to make sure " +
                "it is summari...", logEntry.getTextPreview());
        assertEquals("Text with a really long sentence but is more than 89 characters to make sure " +
                "it is summarized.", logEntry.getTextEntry());
    }

    @Test
    public void GetDateString() throws Exception {
        LogEntry logEntry = new LogEntry("Text", "3/3/2018", "10:00 AM");
        assertEquals("3/3/2018", logEntry.getDateString());
    }

    @Test
    public void GetTimeString() throws Exception {
        LogEntry logEntry = new LogEntry("Text", "3/3/2018", "10:00 AM");
        assertEquals("10:00 AM", logEntry.getTimeString());
    }

    @Test
    public void equals() throws  Exception {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2018);
        c.set(Calendar.MONTH, 2);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        LogEntry logEntry1 = new LogEntry("Text", DateStringUtil.calendarToString(c),
                TimeStringUtil.createTimeString(c.get(Calendar.HOUR), c.get(Calendar.MINUTE)));
        LogEntry logEntry2 = new LogEntry("Different text", DateStringUtil.calendarToString(c),
                TimeStringUtil.createTimeString(c.get(Calendar.HOUR), c.get(Calendar.MINUTE)));
        assertEquals(true, logEntry1.equals(logEntry2));
        c.set(Calendar.MINUTE, 10);
        LogEntry logEntry3 = new LogEntry("Different text", DateStringUtil.calendarToString(c),
                TimeStringUtil.createTimeString(c.get(Calendar.HOUR), c.get(Calendar.MINUTE)));
        assertEquals(false, logEntry2.equals(logEntry3));
    }
}
