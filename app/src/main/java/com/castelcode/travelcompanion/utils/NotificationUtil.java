package com.castelcode.travelcompanion.utils;

import com.castelcode.travelcompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.Info;

import java.util.Calendar;
import java.util.TimeZone;

public class NotificationUtil {

    public static Calendar getNotificationTimeForInfoItem(Info item) {
        String startDate = item.getStartDate();
        String startTime = item.getStartTime();
        if(startDate == null || startTime == null || startDate.equals("") || startTime.equals("")) {
            return null;
        }
        Calendar timeToNotify = Calendar.getInstance();
        timeToNotify.set(Calendar.DAY_OF_MONTH, DateStringUtil.getDay(startDate));
        timeToNotify.set(Calendar.MONTH, DateStringUtil.getMonth(startDate));
        timeToNotify.set(Calendar.YEAR, DateStringUtil.getYear(startDate));
        int amOrPm = TimeStringUtil.getAMorPM(startTime);
        if (amOrPm != -1) {
            timeToNotify.set(Calendar.AM_PM, TimeStringUtil.getAMorPM(startTime));
            timeToNotify.set(Calendar.HOUR, TimeStringUtil.getHour(startTime));
        } else {
            timeToNotify.set(Calendar.HOUR_OF_DAY, TimeStringUtil.getHour(startTime));
        }
        timeToNotify.set(Calendar.MINUTE, TimeStringUtil.getMinute(startTime));
        timeToNotify.add(Calendar.MONTH, -1);
        timeToNotify.add(Calendar.DAY_OF_MONTH, -1);
        timeToNotify.add(Calendar.MINUTE, -15);
        timeToNotify.set(Calendar.SECOND, 0);

        return timeToNotify;
    }

    public static String getNotificationIdForInfoItem(Info item) {
        String notificationId = item.getPrimaryName() + item.getConfirmationNumber();
        return notificationId.equals("") ? "DEFAULT_ITEM_ID" : notificationId;
    }

    public static String getNotificationMessageForInfoItem(Info item) {
        String message = "Remember to check in for your " + item.getPrimaryName();
        if(item instanceof CruiseInfo) {
            message += " cruise ";
        }
        else if(item instanceof FlightInfo) {
            message += " flight ";
        }
        else if(item instanceof HotelInfo) {
            message += " hotel ";
        }
        else if(item instanceof BusInfo) {
            message += " bus ";
        }
        message += "reservation.";
        return message;
    }
}
