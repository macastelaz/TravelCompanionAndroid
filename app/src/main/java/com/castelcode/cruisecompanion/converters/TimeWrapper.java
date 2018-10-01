package com.castelcode.cruisecompanion.converters;

public class TimeWrapper extends ResultWrapper {
    private int mHour, mMinute;
    TimeWrapper(int hour, int minute) {
        super(0); // This is unused and indicates the parent class should be an interface rather than concrete class.
        mHour = hour;
        mMinute = minute;
    }

    public int getHour() {return mHour;}
    public int getMinute() {return mMinute;}

}
