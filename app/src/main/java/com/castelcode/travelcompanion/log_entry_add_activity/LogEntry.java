package com.castelcode.travelcompanion.log_entry_add_activity;

import android.support.annotation.VisibleForTesting;

import com.castelcode.travelcompanion.agenda_entry.DateString;
import com.castelcode.travelcompanion.utils.DateStringUtil;
import com.castelcode.travelcompanion.utils.LogEntryConstants;
import com.castelcode.travelcompanion.utils.TimeStringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogEntry implements Serializable{
    private String mTextEntry;
    private Calendar specifiedDate;
    private ArrayList<LogEntryImage> mImages;
    private String mImageFileName;
    private String mImagePath;

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public LogEntry(String text, String date, String time, ArrayList<LogEntryImage> images){
        mTextEntry = text;
        if(date.equals("Select a date") || time.equals("Select a time")) {
            specifiedDate = Calendar.getInstance();
            specifiedDate.set(Calendar.SECOND, 0);
        }
        else {
            Date d = DateStringUtil.stringToDate(DateStringUtil.slashToDot(date));
            specifiedDate = Calendar.getInstance();
            specifiedDate.setTime(d);
            specifiedDate.set(Calendar.HOUR, TimeStringUtil.getHour(time));
            specifiedDate.set(Calendar.MINUTE, TimeStringUtil.getMinute(time));
            specifiedDate.set(Calendar.SECOND, 0);
        }
        mImages = images;
        mImageFileName = String.valueOf(specifiedDate.get(Calendar.YEAR)) +
                String.valueOf(specifiedDate.get(Calendar.MONTH)) +
                String.valueOf(specifiedDate.get(Calendar.DAY_OF_MONTH)) +
                String.valueOf(specifiedDate.get(Calendar.HOUR_OF_DAY)) +
                String.valueOf(specifiedDate.get(Calendar.MINUTE)) +
                String.valueOf(specifiedDate.get(Calendar.SECOND));
    }

    public String getFileName(){
        return mImageFileName;
    }

    public String getDateTimeAsString(){
        int hour = specifiedDate.get(Calendar.HOUR_OF_DAY);
        Locale locale = Locale.getDefault();
        String amOrPm;
        if(hour == 0){
            hour = 12;
            amOrPm = "AM";
        }
        else if(hour < 12){
            amOrPm = "AM";
        }
        else if(hour == 12){
            amOrPm = "PM";
        }
        else{
            hour = hour - 12;
            amOrPm = "PM";
        }
        return String.valueOf(specifiedDate.get(Calendar.MONTH) + 1) + "/" +
                String.valueOf(specifiedDate.get(Calendar.DAY_OF_MONTH)) + "/" +
                String.valueOf(specifiedDate.get(Calendar.YEAR)) + " " +
                String.valueOf(hour) + ":" +
                String.format(locale, "%02d", specifiedDate.get(Calendar.MINUTE)) + ":" +
                String.format(locale, "%02d", specifiedDate.get(Calendar.SECOND)) + amOrPm;
    }

    public String getTextPreview(){
        if(mTextEntry.length() < LogEntryConstants.PREVIEW_SIZE)
            return mTextEntry.substring(0, mTextEntry.length());
        return mTextEntry.substring(0, LogEntryConstants.PREVIEW_SIZE) + "...";
    }

    public String getTextEntry(){
        return mTextEntry;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public String getDateString() {
        return DateStringUtil.convertToOneIndexed(DateStringUtil.calendarToString(specifiedDate));
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public String getTimeString() {
        return TimeStringUtil.getSummaryString(TimeStringUtil.createTimeString(specifiedDate.get(Calendar.HOUR_OF_DAY),
                specifiedDate.get(Calendar.MINUTE)));
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof LogEntry){
            LogEntry other = (LogEntry)o;
            return this.getDateTimeAsString().equals(other.getDateTimeAsString());
        }
        return false;
    }

    public String getImagePath(){
        return mImagePath;
    }

    public ArrayList<LogEntryImage> getImages() {
        return mImages;
    }
}
