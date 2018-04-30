package com.castelcode.cruisecompanion.preferences;

import android.content.Context;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.content.res.TypedArray;
import android.widget.Toast;

import com.castelcode.cruisecompanion.HomePage;
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.utils.TimeStringUtil;

import java.sql.Time;
import java.util.Calendar;

public class TimePreference extends DialogPreference {
    private int lastHour=0;
    private int lastMinute=0;
    private TimePicker picker=null;
    private Context mContext;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected void onClick() {
        if(HomePage.cruise.getCruiseDateTime() == null) {
            Toast.makeText(mContext, "Please select the date first.", Toast.LENGTH_LONG).show();
            return;
        }
        super.onClick();
    }


    @Override
    protected View onCreateDialogView() {

        picker=new TimePicker(getContext());
        mContext.getTheme().applyStyle(R.style.customPicker, true);

        return(picker);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(lastHour);
            picker.setMinute(lastMinute);
        }
        else{
            picker.setCurrentHour(lastHour);
            picker.setCurrentMinute(lastMinute);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                lastHour = picker.getHour();
                lastMinute = picker.getMinute();
            }
            else {
                lastHour = picker.getCurrentHour();
                lastMinute = picker.getCurrentMinute();
            }
            HomePage.cruise.setCruiseHour(lastHour);
            HomePage.cruise.setCruiseMinute(lastMinute);
            String time = TimeStringUtil.createTimeString(lastHour, lastMinute);

            if (callChangeListener(time)) {
                permistTime(time);
            }
        }
        mContext.getTheme().applyStyle(R.style.prefScreen, true);
    }

    private void permistTime(String s) {
        persistString(s);
        setSummary(TimeStringUtil.getSummaryString(s));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
                setSummary(TimeStringUtil.getSummaryString(time));
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        lastHour = TimeStringUtil.getHour(time);
        if (TimeStringUtil.getAMorPM(time) == Calendar.PM) {
            lastHour += 12;
        }
        lastMinute = TimeStringUtil.getMinute(time);
    }
}