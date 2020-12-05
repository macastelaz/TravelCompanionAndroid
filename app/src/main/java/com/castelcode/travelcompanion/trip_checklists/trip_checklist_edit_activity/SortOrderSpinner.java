package com.castelcode.travelcompanion.trip_checklists.trip_checklist_edit_activity;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Objects;

public class SortOrderSpinner extends android.support.v7.widget.AppCompatSpinner {
    public SortOrderSpinner(Context context) {
        super(context);
    }
    public SortOrderSpinner(Context context, AttributeSet atts) {
        super(context, atts);
    }
    public SortOrderSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (sameSelected) {
            try {
                Objects.requireNonNull(getOnItemSelectedListener()).onItemSelected(
                        this,
                        getSelectedView(),
                        position, getSelectedItemId());
            } catch (NullPointerException ex) {
                System.out.println(
                        "No item selected listener set results in exception: " + ex.toString());
            }
        }
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected) {
            try {
                Objects.requireNonNull(getOnItemSelectedListener()).onItemSelected(
                        this,
                        getSelectedView(),
                        position,
                        getSelectedItemId());
            } catch (NullPointerException ex) {
                System.out.println(
                        "No item selected listener set results in exception: " + ex.toString());
            }
        }
    }

}
