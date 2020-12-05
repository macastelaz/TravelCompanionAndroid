package com.castelcode.travelcompanion.agenda_entry;

import android.support.annotation.VisibleForTesting;

import java.io.Serializable;

public class DateEntry implements Serializable {
    private String mTitle;
    private String mTime;
    private String mLocation;
    private String mDescription;
    private String mDate;

    public DateEntry(String title, String time, String location, String description, String date){
        mTitle = title;
        mTime = time;
        mLocation = location;
        mDescription = description;
        mDate = date;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getTime(){
        return mTime;
    }

    public String getLocation(){
        return mLocation;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getDate() {return mDate;}

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof DateEntry){
            DateEntry other = (DateEntry) o;
            return this.getTime().equals(other.getTime()) &&
                    this.getDate().equals(other.getDate()) &&
                    this.getTitle().equals(other.getTitle());
        }
        return false;
    }

    public String toShareableString() {
        String SEPARATOR = "|";
        return mTitle + SEPARATOR + mTime + SEPARATOR +
                mLocation + SEPARATOR + mDescription;
    }
}
