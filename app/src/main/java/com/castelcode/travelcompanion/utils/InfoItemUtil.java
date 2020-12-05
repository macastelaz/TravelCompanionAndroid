package com.castelcode.travelcompanion.utils;

import com.castelcode.travelcompanion.tile_activities.TripInformation;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.Info;

import java.util.ArrayList;

public class InfoItemUtil {
    public static ArrayList<Info> getAllCruiseInfoItems() {
        ArrayList<Info> cruiseInfoItems = new ArrayList<>();
        for (Info infoItem: TripInformation.infoItems) {
            if (infoItem instanceof CruiseInfo) {
                cruiseInfoItems.add(infoItem);
            }
        }
        return cruiseInfoItems;
    }

    public static ArrayList<Info> getAllBusInfoItems() {
        ArrayList<Info> busInfoItems = new ArrayList<>();
        for (Info infoItem: TripInformation.infoItems) {
            if (infoItem instanceof BusInfo) {
                busInfoItems.add(infoItem);
            }
        }
        return busInfoItems;
    }

    public static ArrayList<Info> getAllFlightInfoItems() {
        ArrayList<Info> flightInfoItems = new ArrayList<>();
        for (Info infoItem: TripInformation.infoItems) {
            if (infoItem instanceof FlightInfo) {
                flightInfoItems.add(infoItem);
            }
        }
        return flightInfoItems;
    }

    public static ArrayList<Info> getAllHotelInfoItems() {
        ArrayList<Info> hotelInfoItems = new ArrayList<>();
        for (Info infoItem: TripInformation.infoItems) {
            if (infoItem instanceof HotelInfo) {
                hotelInfoItems.add(infoItem);
            }
        }
        return hotelInfoItems;
    }
}
