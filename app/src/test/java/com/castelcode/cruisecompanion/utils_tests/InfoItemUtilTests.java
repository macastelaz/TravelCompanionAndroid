package com.castelcode.cruisecompanion.utils_tests;

import com.castelcode.cruisecompanion.tile_activities.TripInformation;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.Info;
import com.castelcode.cruisecompanion.utils.InfoItemUtil;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class InfoItemUtilTests {

    BusInfo busInfo1 = new BusInfo("Name1", "Conf#",
            "123-456-7890", "18D", "ORD", "PHX",
            "12:00AM", "11:00PM", "1/1/2018");
    BusInfo busInfo2 = new BusInfo("Name2", "Conf#",
            "123-456-7890", "12D", "SJC", "MKE",
            "12:00PM", "2:00PM", "1/1/2018");
    CruiseInfo cruiseInfo1 = new CruiseInfo("Name1", "Conf#",
            "123-456-7890", "1745", "testShip1",
            "1/1/2018", "12:00PM");
    CruiseInfo cruiseInfo2 = new CruiseInfo("Name2", "Conf#",
            "098-7654-321", "5432", "testShip2",
            "3/1/2018", "1:00PM");
    FlightInfo flightInfo1 = new FlightInfo("Name", "Conf1#",
                           "123-456-7890", "123", "12D",
            "LAX", "SAN", "10:00AM", "12:00PM", "1/1/2018");
    FlightInfo flightInfo2 = new FlightInfo("Name", "Conf2#",
            "123-456-7890", "123", "12D", "MWC",
            "MDW", "10:00AM", "12:00PM", "1/1/2018");
    HotelInfo hotelInfo1 = new HotelInfo("Name21", "Conf#abc",
                          "123-456-7890", "123 address way", "city",
                          "state", "1/1/2018", "3:00PM",
                          "1/3/2018", "10:00AM");
    HotelInfo hotelInfo2 = new HotelInfo("Name12", "Conf#cba",
            "123-456-7890", "321 address way", "city",
            "state", "1/1/2018", "5:00PM",
            "1/3/2018", "10:00AM");

    ArrayList<Info> infoItems = new ArrayList<Info>() {{
        add(cruiseInfo1);
        add(cruiseInfo2);
        add(flightInfo1);
        add(flightInfo2);
        add(hotelInfo1);
        add(hotelInfo2);
        add(busInfo1);
        add(busInfo2);
    }};

    @Test
    public void getAllCruiseItems_test() throws Exception {
        TripInformation.infoItems = infoItems;
        assertEquals(2, InfoItemUtil.getAllCruiseInfoItems().size());
        assertEquals(new ArrayList<Info>(){{
            add(cruiseInfo1);
            add(cruiseInfo2);
        }}, InfoItemUtil.getAllCruiseInfoItems());
    }

    @Test
    public void getAllHotelItems_test() throws Exception {
        TripInformation.infoItems = infoItems;
        assertEquals(2, InfoItemUtil.getAllHotelInfoItems().size());
        assertEquals(new ArrayList<Info>(){{
            add(hotelInfo1);
            add(hotelInfo2);
        }}, InfoItemUtil.getAllHotelInfoItems());
    }

    @Test
    public void getAllBusItems_test() throws Exception {
        TripInformation.infoItems = infoItems;
        assertEquals(2, InfoItemUtil.getAllBusInfoItems().size());
        assertEquals(new ArrayList<Info>(){{
            add(busInfo1);
            add(busInfo2);
        }}, InfoItemUtil.getAllBusInfoItems());
    }

    @Test
    public void getAllFlightItems_test() throws Exception {
        TripInformation.infoItems = infoItems;
        assertEquals(2, InfoItemUtil.getAllFlightInfoItems().size());
        assertEquals(new ArrayList<Info>(){{
            add(flightInfo1);
            add(flightInfo2);
        }}, InfoItemUtil.getAllFlightInfoItems());
    }
}
