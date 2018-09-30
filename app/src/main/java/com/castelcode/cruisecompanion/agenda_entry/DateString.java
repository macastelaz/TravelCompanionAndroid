package com.castelcode.cruisecompanion.agenda_entry;

import android.support.annotation.NonNull;

import com.castelcode.cruisecompanion.utils.DateStringUtil;

public class DateString implements Comparable<DateString>{
    private String dateString;

    public DateString(String date){
        dateString = date;
    }

    public String getDateString(){
        return dateString;
    }

    /**
     * Compares the dates contained within two DateStrings
     * @param o The date to comapre with.
     * @return 0 if dates are equal, -1 if this date is before the passed in date, 1 otherwise.
     */
    @Override
    public int compareTo(@NonNull DateString o) {
        String[] partsS1 = this.getDateString().split("/");
        String[] partsS2 = o.getDateString().split("/");

        int month1 = Integer.parseInt(partsS1[0]);
        int month2 = Integer.parseInt(partsS2[0]);
        boolean monthOneBefore = month1 < month2;
        boolean monthsEqual = month1 == month2;

        int day1 = Integer.parseInt(partsS1[1]);
        int day2 = Integer.parseInt(partsS2[1]);
        boolean dayOneBefore = day1 < day2;
        boolean daysEqual = day1 == day2;

        int year1 = Integer.parseInt(partsS1[2]);
        int year2 = Integer.parseInt(partsS2[2]);
        boolean yearOneBefore = year1 < year2;
        boolean yearsEqual = year1 == year2;

        if (yearsEqual) {
            if (monthsEqual) {
                if (daysEqual) {
                    return 0;
                } else {
                    return dayOneBefore ? -1 : 1;
                }
            } else {
                return monthOneBefore ? -1 : 1;
            }
        } else {
            return yearOneBefore ? -1 : 1;
        }
    }
    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof DateString){
            DateString other = (DateString) o;
            return this.getDateString().equals(other.getDateString());
        }
        return false;
    }
}
