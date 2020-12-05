package com.castelcode.travelcompanion.utils;

import java.util.Calendar;

public class TimeStringUtil {

    public static String createTimeString(int hour, int min){
        if(hour < 0 || hour > 23) {
            hour = 12;
        }
        if (min < 0 || min > 59) {
            min = 0;
        }
        return String.valueOf(hour)+":"+String.valueOf(min);
    }

    public static String getSummaryString(String time){
        int hour = getHour(time);
        int minutes = getMinute(time);
        if(hour < 0 || hour > 23 || minutes < 0 || minutes > 59) {
            return "Invalid Time";
        }
        String timeSet;
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12){
            timeSet = "PM";
        } else{
            timeSet = "AM";
        }

        String min;
        if (minutes < 10)
            min = "0" + minutes ;
        else
            min = String.valueOf(minutes);
        return hour + ":" + min + " " + timeSet;
    }

    public static int getHour(String time){
        String[] pieces = time.split(":");
        int hour;
        try {
            hour = Integer.parseInt(pieces[0]);
            if (hour < 0 || hour > 23) {
                hour = -1;
            }
        }
        catch (NumberFormatException ex) {
            hour = -1;
        }
        return hour;
    }

    public static int getMinute(String time){
        String[] pieces = time.split(":");
        int minutes;
        try {
            String[] minutesWithoutAmPm = pieces[1].split(" ");
            minutes = Integer.parseInt(minutesWithoutAmPm[0]);
            if (minutes < 0 || minutes > 59) {
                minutes = -1;
            }
        } catch (IndexOutOfBoundsException ex) {
            minutes = -1;
        } catch (NumberFormatException ex) {
            minutes = -1;
        }
        return minutes;
    }

    public static int getAMorPM(String time){
        try {
            String[] pieces = time.split(" ");
            return pieces[1].equals("AM") ? Calendar.AM : Calendar.PM;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            String[] pieces = time.split(":");
            int hour;
            try {
                hour = Integer.parseInt(pieces[0]);
                if (hour >= 12 && hour < 24) {
                    return Calendar.PM;
                }
                else if (hour >= 0 && hour < 12) {
                    return Calendar.AM;
                }
                else {
                    return -1;
                }
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }
}
