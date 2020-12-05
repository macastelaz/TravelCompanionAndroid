package com.castelcode.travelcompanion.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateStringUtil {

    public static Date stringToDate(String dateString) {
        Date date;
        try {
            date =  formatter().parse(dateString);
        } catch (ParseException e) {
            date = Calendar.getInstance().getTime();
        }
        return date;
    }
    /**
     * Produces the date formatter used for dates in the XML. The default is yyyy.MM.dd.
     * Override this to change that.
     *
     * @return the SimpleDateFormat used for XML dates
     */
    public static SimpleDateFormat formatter() {
        return new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
    }

    public static String convertToZeroIndexed(String dateString){
        int day = getDay(dateString);
        int month = getMonth(dateString) - 1;
        int year = getYear(dateString);

        return month + "/" + day + "/" + year;
    }

    public static String convertToOneIndexed(String dateString){
        int day = getDay(dateString);

        int month = getMonth(dateString);
        if (month != -1) {
            month = month + 1;
        }
        int year = getYear(dateString);

        return month + "/" + day + "/" + year;
    }

    public static String intToDateString(int month, int day, int year){
        return String.valueOf(month) + "/" +
                String.valueOf(day) + "/" + String.valueOf(year);
    }

    public static String calendarToString(Calendar calendar){
        return calendar.get(Calendar.MONTH)+ "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                calendar.get(Calendar.YEAR);
    }

    public static String slashToDot(String date){
        return getYear(date) + "." + getMonth(date) + "." + getDay(date);
    }

    public static String dotToSlash(String date){
        return getMonth(date) + "/" + getDay(date) + "/" + getYear(date) ;
    }

    public static int getDay(String dateString){
        int day = getComponentAtIndexWithDelimiter(dateString, 1, "/");
        if(day == -1) {
            day = getComponentAtIndexWithDelimiter(dateString, 2, "\\.");
        }
        return day;
    }

    public static int getMonth(String dateString){
        int month = getComponentAtIndexWithDelimiter(dateString, 0, "/");
        if(month == -1){
            month = getComponentAtIndexWithDelimiter(dateString, 1, "\\.");
        }
        return month;
    }

    public static int getYear(String dateString){
        int year = getComponentAtIndexWithDelimiter(dateString, 2, "/");
        if(year == -1) {
            year = getComponentAtIndexWithDelimiter(dateString, 0, "\\.");
        }
        return year;
    }

    private static int getComponentAtIndexWithDelimiter(
            String dateString, int index, String delimiter) {
        try {
            return Integer.valueOf(dateString.split(delimiter)[index]);
        }
        catch (IndexOutOfBoundsException ex){
            return -1;
        }
        catch (NumberFormatException ex) {
            return  -1;
        }
    }
}
