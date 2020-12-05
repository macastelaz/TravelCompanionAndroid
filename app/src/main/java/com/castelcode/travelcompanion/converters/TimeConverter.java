package com.castelcode.travelcompanion.converters;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeConverter extends Converter{
    private static final String TAG = "TIME_CONVERTER";

    private int originalHour;
    private int originalMinute;

    public TimeConverter(Context context) {
        super(context);
    }

    public void setOriginalHour(int hour) {originalHour = hour;}

    public void setOriginalMinute(int minute) {originalMinute = minute;}

    @Override
    public TimeWrapper convert(String originalUnit, String desiredUnit) {
        Calendar timeToConvert = null;
        switch (originalUnit) {
            case "PT":
                timeToConvert = new GregorianCalendar(TimeZone.getTimeZone("US/Pacific"));
                break;
            case "CT":
                timeToConvert = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
                break;
            case "ET":
                timeToConvert = new GregorianCalendar(TimeZone.getTimeZone("US/Eastern"));
                break;
            case "MT":
                timeToConvert = new GregorianCalendar(TimeZone.getTimeZone("US/Mountain"));
                break;
            case "HT":
                timeToConvert = new GregorianCalendar(TimeZone.getTimeZone("US/Hawaii"));
                break;
            case "AKT":
                timeToConvert = new GregorianCalendar(TimeZone.getTimeZone("US/Alaska"));
                break;
            default:
                Log.i(TAG, "ORIGIN TIMEZONE NOT SUPPORTED");
        }
        if (timeToConvert == null) {
            return new TimeWrapper(originalHour, originalMinute);
        }
        if (originalHour >= 12) {
            timeToConvert.set(Calendar.AM_PM, Calendar.PM);
            timeToConvert.set(Calendar.HOUR, originalHour - 12);
        } else {
            timeToConvert.set(Calendar.AM_PM, Calendar.AM);
            timeToConvert.set(Calendar.HOUR, originalHour);
        }
        timeToConvert.set(Calendar.MINUTE, originalMinute);

        Calendar destinationTimeZone = null;
        switch (desiredUnit) {
            case "PT":
                destinationTimeZone = new GregorianCalendar(TimeZone.getTimeZone("US/Pacific"));
                break;
            case "CT":
                destinationTimeZone = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
                break;
            case "ET":
                destinationTimeZone = new GregorianCalendar(TimeZone.getTimeZone("US/Eastern"));
                break;
            case "MT":
                destinationTimeZone = new GregorianCalendar(TimeZone.getTimeZone("US/Mountain"));
                break;
            case "HT":
                destinationTimeZone = new GregorianCalendar(TimeZone.getTimeZone("US/Hawaii"));
                break;
            case "AKT":
                destinationTimeZone = new GregorianCalendar(TimeZone.getTimeZone("US/Alaska"));
                break;
            default:
                Log.i(TAG, "DESTINATION TIMEZONE NOT SUPPORTED");
        }
        if (destinationTimeZone == null) {
            return new TimeWrapper(originalHour, originalMinute);
        }
        destinationTimeZone.setTimeInMillis(timeToConvert.getTimeInMillis());
        int hours = destinationTimeZone.get(Calendar.HOUR);
        if (destinationTimeZone.get(Calendar.AM_PM) == Calendar.PM) {
            hours += 12;
        }
        return new TimeWrapper(hours, destinationTimeZone.get(Calendar.MINUTE));
    }
}
