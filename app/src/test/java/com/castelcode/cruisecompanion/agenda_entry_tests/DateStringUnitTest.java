package com.castelcode.cruisecompanion.agenda_entry_tests;

import com.castelcode.cruisecompanion.agenda_entry.DateString;

import org.junit.Test;
import static org.junit.Assert.*;

public class DateStringUnitTest {
    @Test
    public void DateString_creation() throws Exception {
        DateString dateStringInvalid = new DateString("testInvalid");
        assertEquals("-1/-1/-1", dateStringInvalid.getDateString());
        DateString dateStringValid = new DateString("0/1/2018");
        assertEquals("1/1/2018", dateStringValid.getDateString());
    }

    @Test
    public void DateString_compareTo() throws Exception {
        DateString dateStringOne = new DateString("0/1/2018");
        DateString dateStringTwo = new DateString("0/1/2018");
        assertEquals(0, dateStringOne.compareTo(dateStringTwo));
        dateStringOne = new DateString("11/31/2017");
        assertEquals(-1, dateStringOne.compareTo(dateStringTwo));
        dateStringOne = new DateString("0/2/2018");
        assertEquals(1, dateStringOne.compareTo(dateStringTwo));
        dateStringOne = new DateString("1/1/2018");
        assertEquals(1, dateStringOne.compareTo(dateStringTwo));
        dateStringOne = new DateString("0/1/2019");
        assertEquals(1, dateStringOne.compareTo(dateStringTwo));
        dateStringTwo = new DateString(("1/1/2019"));
        assertEquals(-1, dateStringOne.compareTo(dateStringTwo));
        dateStringOne = new DateString(("1/1/2019"));
        dateStringTwo = new DateString(("1/2/2019"));
        assertEquals(-1, dateStringOne.compareTo(dateStringTwo));

    }

}
