package com.castelcode.travelcompanion.share_activity;


import android.widget.CheckBox;

import com.castelcode.travelcompanion.tile_activities.TripChecklists;
import com.castelcode.travelcompanion.trip_checklists.Checklist;
import com.castelcode.travelcompanion.trip_checklists.ChecklistItem;

public class ShareTripChecklist {
    public static String getHtmlMessageForEmail(int positionOfItemToShare) {
        StringBuilder builder = new StringBuilder();
        if (positionOfItemToShare != -1) {
            Checklist checklistToShare = TripChecklists.checklists.get(positionOfItemToShare);
            builder.append(createChecklistEmailFriendlyString(checklistToShare)).append("<br>");
        } else {
            for (Checklist checklistToShare : TripChecklists.checklists) {
                builder.append(createChecklistEmailFriendlyString(checklistToShare)).append("<br>");
            }
        }
        return builder.toString();
    }

    private static String createChecklistEmailFriendlyString(Checklist checklist) {
        StringBuilder builder = new StringBuilder();
        builder.append("Checklist Name: ").append(checklist.getChecklistName()).append("<br>");
        builder.append("Ratings: ");
        if (checklist.getIsRateable()) {
            builder.append("Yes");
        } else {
            builder.append("No");
        }
        builder.append("<br>");
        for (ChecklistItem item : checklist.getItems()) {
            builder.append("- ").append(item.getItemTitle()).append("<br>");
            builder.append("Currently Checked: ");
            if (item.getCheckedState()) {
                builder.append("Yes");
            } else {
                builder.append("No");
            }
            builder.append("<br>");
            if (checklist.getIsRateable()){
                builder.append("Rating: ").append(item.getRating()).append("<br>");
            }
        }
        return  builder.toString();
    }
}
