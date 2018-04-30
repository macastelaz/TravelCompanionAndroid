package com.castelcode.cruisecompanion.agenda_entry;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.castelcode.cruisecompanion.R;

public class DatesContainer extends LinearLayout {

    public DatesContainer(Context context) {
        super(context);
        inflate(context);
    }

    public DatesContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
    }

    private void inflate(Context context){
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.dates_container, this, true);
    }

}
