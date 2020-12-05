package com.castelcode.travelcompanion.agenda_entry_tests;

import com.castelcode.travelcompanion.agenda_entry.DateEntry;

import org.junit.Test;
import static org.junit.Assert.*;

public class DateEntryUnitTest {
    @Test
    public void DateEntry_creation() throws Exception {
        DateEntry dateEntry = new DateEntry("title", "time", "location",
                "description", "date");
        assertEquals("title", dateEntry.getTitle());
        assertEquals("time", dateEntry.getTime());
        assertEquals("location", dateEntry.getLocation());
        assertEquals("description", dateEntry.getDescription());
        assertEquals("date", dateEntry.getDate());
    }

    @Test
    public void DateEntry_isEqual() throws Exception {
        DateEntry dateEntryOne = new DateEntry("title", "time", "location1",
                "description1", "date");
        DateEntry dateEntryTwo = new DateEntry("title", "time", "location2",
                "description2", "date");
        assertEquals(true, dateEntryOne.equals(dateEntryTwo));
        dateEntryOne = new DateEntry("title1", "time", "location1",
                "description1", "date");
        assertEquals(false, dateEntryOne.equals(dateEntryTwo));
        dateEntryOne = new DateEntry("title", "time1", "location1",
                "description1", "date");
        assertEquals(false, dateEntryOne.equals(dateEntryTwo));
        dateEntryOne = new DateEntry("title", "time", "location1",
                "description1", "date1");
        assertEquals(false, dateEntryOne.equals(dateEntryTwo));
    }
}
