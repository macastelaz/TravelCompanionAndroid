package com.castelcode.cruisecompanion.utils_tests;

import com.castelcode.cruisecompanion.agenda_entry.DateEntry;
import com.castelcode.cruisecompanion.agenda_entry.DateString;
import com.castelcode.cruisecompanion.utils.TreeMapConverter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeMap;

import de.tum.in.www1.jReto.routing.algorithm.Tree;

import static org.junit.Assert.assertEquals;


public class TreeMapConverterTests {
    @Test
    public void toStringKeyed_test() throws Exception {
        ArrayList<DateEntry> dateEntries = new ArrayList<>();
        dateEntries.add(new DateEntry("test", "8:00 AM", "newLoc",
                "testItem", "3/3/2018"));
        dateEntries.add(new DateEntry("test2", "9:00 AM", "newLoc2",
                "testItem2", "3/3/2018"));
        TreeMap<String, ArrayList<DateEntry>> expectedResult = new TreeMap<>();
        expectedResult.put("3/3/2018", dateEntries);
        TreeMap<DateString, ArrayList<DateEntry>> input = new TreeMap<>();
        input.put(new DateString("2/3/2018"), dateEntries);

        assertEquals(expectedResult, TreeMapConverter.toStringKeyed(input));

        TreeMap<String, ArrayList<DateEntry>> emptyExpectedResult = new TreeMap<>();
        TreeMap<DateString, ArrayList<DateEntry>> emptyInput = new TreeMap<>();
        assertEquals(emptyExpectedResult, TreeMapConverter.toStringKeyed(emptyInput));
    }

    @Test
    public void toDateStringKeyed_test() throws Exception {
        ArrayList<DateEntry> dateEntries = new ArrayList<>();
        dateEntries.add(new DateEntry("test", "8:00 AM", "newLoc",
                "testItem", "3/3/2018"));
        dateEntries.add(new DateEntry("test2", "9:00 AM", "newLoc2",
                "testItem2", "3/3/2018"));
        TreeMap<DateString, ArrayList<DateEntry>> expectedResult = new TreeMap<>();
        expectedResult.put(new DateString("2/3/2018"), dateEntries);
        TreeMap<String, ArrayList<DateEntry>> input = new TreeMap<>();
        input.put("2/3/2018", dateEntries);

        assertEquals(expectedResult, TreeMapConverter.toDateStringKeyed(input));

        TreeMap<DateString, ArrayList<DateEntry>> emptyExpectedResult = new TreeMap<>();
        TreeMap<String, ArrayList<DateEntry>> emptyInput = new TreeMap<>();
        assertEquals(emptyExpectedResult, TreeMapConverter.toDateStringKeyed(emptyInput));
    }
}
