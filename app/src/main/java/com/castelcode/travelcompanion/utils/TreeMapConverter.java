package com.castelcode.travelcompanion.utils;

import com.castelcode.travelcompanion.agenda_entry.DateEntry;
import com.castelcode.travelcompanion.agenda_entry.DateString;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class TreeMapConverter {
    public static TreeMap<String, ArrayList<DateEntry>> toStringKeyed(
            TreeMap<DateString, ArrayList<DateEntry>> dateStringKeyed){
        TreeMap<String, ArrayList<DateEntry>> returnTree = new TreeMap<>();
        for (Object o : dateStringKeyed.entrySet()) {
            Map.Entry pair = (Map.Entry) o;

            @SuppressWarnings("unchecked")
            ArrayList<DateEntry> value = (ArrayList<DateEntry>) pair.getValue();
            returnTree.put(((DateString) pair.getKey()).getDateString(),
                    value);
        }
        return returnTree;
    }

    public static TreeMap<DateString, ArrayList<DateEntry>> toDateStringKeyed(
            TreeMap<String, ArrayList<DateEntry>> stringKeyed){
        TreeMap<DateString, ArrayList<DateEntry>> returnTree = new TreeMap<>();
        for (Object o : stringKeyed.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            @SuppressWarnings("unchecked")
            ArrayList<DateEntry> value = (ArrayList<DateEntry>) pair.getValue();
            returnTree.put(new DateString((String) pair.getKey()),
                    value);
        }
        return returnTree;
    }
}
