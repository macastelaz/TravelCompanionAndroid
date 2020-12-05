package com.castelcode.travelcompanion.share_activity;

import com.castelcode.travelcompanion.tile_activities.TripInformation;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.Info;

public class ShareTripInfoItem {
    public static String getHtmlMessageForEmail(int positionOfItemToShare) {
        StringBuilder builder = new StringBuilder();
        if(positionOfItemToShare != -1) {
            Info itemShared = TripInformation.infoItems.get(positionOfItemToShare);
            if (itemShared instanceof CruiseInfo) {
                builder.append(getCruiseEmailFriendlyString((CruiseInfo)itemShared));
                builder.append("<br>");
            }
            else if (itemShared instanceof HotelInfo) {
                builder.append(getHotelEmailFriendlyString((HotelInfo)itemShared));
                builder.append("<br>");
            }
            else if (itemShared instanceof FlightInfo) {
                builder.append(getFlightEmailFriendlyString((FlightInfo)itemShared));
                builder.append("<br>");
            }
            else if (itemShared instanceof BusInfo) {
                builder.append(getBusEmailFriendlyString((BusInfo)itemShared));
                builder.append("<br>");
            }
        }
        else {

            for (Info item : TripInformation.infoItems) {
                if (item instanceof CruiseInfo) {
                    builder.append(getCruiseEmailFriendlyString((CruiseInfo)item));
                    builder.append("<br>");
                }
                else if (item instanceof HotelInfo) {
                    builder.append(getHotelEmailFriendlyString((HotelInfo)item));
                    builder.append("<br>");
                }
                else if (item instanceof FlightInfo) {
                    builder.append(getFlightEmailFriendlyString((FlightInfo)item));
                    builder.append("<br>");
                }
                else if (item instanceof BusInfo) {
                    builder.append(getBusEmailFriendlyString((BusInfo)item));
                    builder.append("<br>");
                }
            }
        }
        return builder.toString();
    }

    private static String getCruiseEmailFriendlyString(CruiseInfo item) {
        return "Cruise" + "<br>" +
                "Cruise line: " + item.getPrimaryName() + "<br>" +
                "Cruise ship: " + item.getShipName() + "<br>" +
                "Cruise date: " + item.getStartDate() + "<br>" +
                "Cruise time: " + item.getStartTime() + "<br>" +
                "Room number: " + item.getRoomNumber() + "<br>" +
                "Confirmation Number: " + item.getConfirmationNumber() + "<br>" +
                "Phone Number: " + item.getPhoneNumber() + "<br>";

    }
    private static String getFlightEmailFriendlyString(FlightInfo item) {
        return "Flight" + "<br>" +
                "Airline: " + item.getPrimaryName() + "<br>" +
                "Flight number: " + item.getFlightNumber() + "<br>" +
                "Seat number: " + item.getSeatNumber() + "<br>" +
                "Departure date: " + item.getStartDate() + "<br>" +
                "Departure time: " + item.getStartTime() + "<br>" +
                "Arrival time: " + item.getArrivalTime() + "<br>" +
                "Origin: " + item.getOrigin() + "<br>" +
                "Destination: " + item.getDestination() + "<br>" +
                "Confirmation Number: " + item.getConfirmationNumber() + "<br>" +
                "Phone Number: " + item.getPhoneNumber() + "<br>";
    }
    private static String getHotelEmailFriendlyString(HotelInfo item) {
        return "Hotel" + "<br>" +
                "Hotel: " + item.getPrimaryName() + "<br>" +
                "Address: " + item.getAddress() + "<br>" +
                "City: " + item.getCity() + "<br>" +
                "State/Province/Country: " + item.getStateProvince() + "<br>" +
                "Check in date: " + item.getStartDate() + "<br>" +
                "Check in time: " + item.getStartTime() + "<br>" +
                "Check out date: " + item.getCheckOutDate() + "<br>" +
                "Check out time: " + item.getCheckOutTime() + "<br>" +
                "Confirmation Number: " + item.getConfirmationNumber() + "<br>" +
                "Phone Number: " + item.getPhoneNumber() + "<br>";
    }
    private static String getBusEmailFriendlyString(BusInfo item) {
        return "Bus" + "<br>" +
                "Bus line: " + item.getPrimaryName() + "<br>" +
                "Seat number: " + item.getSeatNumber() + "<br>" +
                "Departure date: " + item.getStartDate() + "<br>" +
                "Departure time: " + item.getStartTime() + "<br>" +
                "Arrival time: " + item.getArrivalTime() + "<br>" +
                "Origin: " + item.getOrigin() + "<br>" +
                "Destination: " + item.getDestination() + "<br>" +
                "Confirmation Number: " + item.getConfirmationNumber() + "<br>" +
                "Phone Number: " + item.getPhoneNumber() + "<br>";
    }
}
