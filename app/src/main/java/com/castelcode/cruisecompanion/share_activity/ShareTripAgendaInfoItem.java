package com.castelcode.cruisecompanion.share_activity;

import com.castelcode.cruisecompanion.agenda_entry.DateEntry;
import com.castelcode.cruisecompanion.agenda_entry.DateString;
import com.castelcode.cruisecompanion.tile_activities.TripAgenda;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ShareTripAgendaInfoItem {
    public static String getHtmlMessageForEmail(int positionOfItemToShare) {
        StringBuilder builder = new StringBuilder();
        if(positionOfItemToShare != -1) {
            DateString dateToUse =
                    (DateString) TripAgenda.listDateChildren.keySet().toArray()[positionOfItemToShare];
            ArrayList<DateEntry> entries = TripAgenda.listDateChildren.get(dateToUse);
            builder.append("<b><u>").append(dateToUse.getDateString()).append("</u></b>").append("<br>");
            for(DateEntry entry: entries) {
                builder.append(getDateEntryEmailFriendlyString(entry));
            }
        }
        else {
            for (Map.Entry<DateString, ArrayList<DateEntry>> entry:
                    TripAgenda.listDateChildren.entrySet()) {
                String dateString = entry.getKey().getDateString();
                builder.append("<b><u>").append(dateString).append("</u></b>").append("<br>");
                for(DateEntry dateEntry: entry.getValue()) {
                    builder.append(getDateEntryEmailFriendlyString(dateEntry));
                }
                builder.append("<br>");
            }
        }
        return builder.toString();
    }

    private static String getDateEntryEmailFriendlyString(DateEntry entry) {
        return "&emsp;" + "<b>" + "Title: " + "</b>" + entry.getTitle() + "<br>"
                + "&emsp;" + "<b>" + "Time: " + "</b>" + entry.getTime() + "<br>"
                + "&emsp;" + "<b>" + "Location: " + "</b>" + entry.getLocation() + "<br>"
                + "&emsp;" + "<b>" + "Description: " + "</b>" + entry.getDescription() + "<br>";
    }
}
