package com.castelcode.cruisecompanion.utils_tests;

import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.cruisecompanion.utils.NotificationUtil;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class NotificationUtilTests {
    @Test
    public void getNotificationTimeForInfoItem_test() throws Exception {

        HotelInfo hotelInfo1 = new HotelInfo("Name12", "Conf#cba",
                "123-456-7890", "321 address way", "city",
                "state", "1/3/2018", "7:00 PM",
                "1/4/2018", "10:00AM");
        Calendar newCal1 = NotificationUtil.getNotificationTimeForInfoItem(hotelInfo1);
        assertEquals(0, newCal1.get(Calendar.MONTH));
        assertEquals(2, newCal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(2018, newCal1.get(Calendar.YEAR));
        assertEquals(6, newCal1.get(Calendar.HOUR));
        assertEquals(Calendar.PM, newCal1.get(Calendar.AM_PM));
        assertEquals(45, newCal1.get(Calendar.MINUTE));

        HotelInfo hotelInfo2 = new HotelInfo("Name12", "Conf#cba",
                "123-456-7890", "321 address way", "city",
                "state", "1/3/2018", "20:00",
                "1/4/2018", "10:00AM");
        Calendar newCal2 = NotificationUtil.getNotificationTimeForInfoItem(hotelInfo2);
        assertEquals(0, newCal2.get(Calendar.MONTH));
        assertEquals(2, newCal2.get(Calendar.DAY_OF_MONTH));
        assertEquals(2018, newCal2.get(Calendar.YEAR));
        assertEquals(7, newCal2.get(Calendar.HOUR));
        assertEquals(Calendar.PM, newCal2.get(Calendar.AM_PM));
        assertEquals(19, newCal2.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, newCal2.get(Calendar.MINUTE));

        HotelInfo hotelInfo3 = new HotelInfo("Name12", "Conf#cba",
                "123-456-7890", "321 address way", "city",
                "state", "1/3/2018", "12:15 AM",
                "1/4/2018", "10:00AM");
        Calendar newCal3 = NotificationUtil.getNotificationTimeForInfoItem(hotelInfo3);
        assertEquals(0, newCal3.get(Calendar.MONTH));
        assertEquals(2, newCal3.get(Calendar.DAY_OF_MONTH));
        assertEquals(2018, newCal3.get(Calendar.YEAR));
        assertEquals(0, newCal3.get(Calendar.HOUR));
        assertEquals(Calendar.AM, newCal3.get(Calendar.AM_PM));
        assertEquals(0, newCal3.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, newCal3.get(Calendar.MINUTE));
    }

    @Test
    public void getNotificationIdForInfoItem_test() throws Exception {
        HotelInfo hotelInfo1 = new HotelInfo("Name12", "Conf#cba",
                "123-456-7890", "321 address way", "city",
                "state", "1/3/2018", "7:00 PM",
                "1/4/2018", "10:00AM");
        assertEquals("Name12Conf#cba",
                NotificationUtil.getNotificationIdForInfoItem(hotelInfo1));

        HotelInfo hotelInfo2 = new HotelInfo("Name123", "",
                "123-456-7890", "321 address way", "city",
                "state", "1/3/2018", "7:00 PM",
                "1/4/2018", "10:00AM");
        assertEquals("Name123",
                NotificationUtil.getNotificationIdForInfoItem(hotelInfo2));

        HotelInfo hotelInfo3 = new HotelInfo("", "",
                "123-456-7890", "321 address way", "city",
                "state", "1/3/2018", "7:00 PM",
                "1/4/2018", "10:00AM");
        assertEquals("DEFAULT_ITEM_ID",
                NotificationUtil.getNotificationIdForInfoItem(hotelInfo3));
    }

    @Test
    public void getNotificationMessageForInfoItem_test() throws Exception {
        HotelInfo hotelInfo1 = new HotelInfo("Name12", "Conf#cba",
                "123-456-7890", "321 address way", "city",
                "state", "1/3/2018", "7:00 PM",
                "1/4/2018", "10:00AM");
        assertEquals("Remember to check in for your Name12 hotel reservation.",
                NotificationUtil.getNotificationMessageForInfoItem(hotelInfo1));
        BusInfo busInfo1 = new BusInfo("Name1", "Conf#",
                "123-456-7890", "18D", "ORD", "PHX",
                "12:00AM", "11:00PM", "1/1/2018");
        assertEquals("Remember to check in for your Name1 bus reservation.",
                NotificationUtil.getNotificationMessageForInfoItem(busInfo1));
        CruiseInfo cruiseInfo1 =  new CruiseInfo("Name3", "Conf#",
                "123-456-7890", "1745", "testShip1",
                "1/1/2018", "12:00PM");
        assertEquals("Remember to check in for your Name3 cruise reservation.",
                NotificationUtil.getNotificationMessageForInfoItem(cruiseInfo1));
        FlightInfo flightInfo1 = new FlightInfo("Name", "Conf1#",
                "123-456-7890", "123", "12D",
                "LAX", "SAN", "10:00AM", "12:00PM", "1/1/2018");
        assertEquals("Remember to check in for your Name flight reservation.",
                NotificationUtil.getNotificationMessageForInfoItem(flightInfo1));
    }
}
