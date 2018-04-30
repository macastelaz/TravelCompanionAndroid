package com.castelcode.cruisecompanion.info_items_tests;

import android.os.Parcel;
import android.os.Parcelable;

import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.FlightInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

@Config(manifest=Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class FlightInfoTests {
    @Test
    public void FlightInfo_parameterizedCreation() throws Exception {
        FlightInfo flightInfo = new FlightInfo("Name", "Conf#",
                "123-456-7890", "123", "12D", "SJC",
                "MKE", "10:00AM", "12:00PM", "1/1/2018");
        assertEquals("Name", flightInfo.getPrimaryName());
        assertEquals("Conf#", flightInfo.getConfirmationNumber());
        assertEquals("123-456-7890", flightInfo.getPhoneNumber());
        assertEquals("123", flightInfo.getFlightNumber());
        assertEquals("12D", flightInfo.getSeatNumber());
        assertEquals("SJC", flightInfo.getOrigin());
        assertEquals("MKE", flightInfo.getDestination());
        assertEquals("10:00AM", flightInfo.getStartTime());
        assertEquals("12:00PM", flightInfo.getArrivalTime());
        assertEquals("1/1/2018", flightInfo.getStartDate());
    }

    @Test
    public void FlightInfo_parcelCreation() throws Exception {
        Parcel parcel = Mockito.spy(Parcel.obtain());
        Parcelable parcelable = new FlightInfo("Name", "Conf#",
                "123-456-7890", "123", "12D", "SJC",
                "MKE", "10:00AM", "12:00PM", "1/1/2018");
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        FlightInfo cruiseInfoFromParcel = new FlightInfo(parcel);
        assertEquals(true, ((FlightInfo)parcelable).equals(cruiseInfoFromParcel));
    }


    @Test
    public void FlightInfo_writeToParcel() throws Exception {
        Parcel parcel = Mockito.spy(Parcel.obtain());
        Parcelable parcelable = new FlightInfo("Name", "Conf#",
                "123-456-7890", "123", "12D", "SJC",
                "MKE", "10:00AM", "12:00PM", "1/1/2018");
        parcelable.writeToParcel(parcel, 0);

        InOrder inOrder = Mockito.inOrder(parcel);
        for (Field field : parcelable.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(String.class)) {
                if(!field.get(parcelable).toString().equals("Flight")) {
                    inOrder.verify(parcel).writeString(field.get(parcelable).toString());
                }
            }
        }
    }

    @Test
    public void FlightInfo_shareableStringCreation() throws Exception {
        FlightInfo flightInfo = new FlightInfo("Flight|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|1234|12D|SJC|MKE|11:00PM");
        assertEquals("myName", flightInfo.getPrimaryName());
        assertEquals("Conf#", flightInfo.getConfirmationNumber());
        assertEquals("123-456-7890", flightInfo.getPhoneNumber());
        assertEquals("1/1/2018", flightInfo.getStartDate());
        assertEquals("8:00PM", flightInfo.getStartTime());
        assertEquals("1234", flightInfo.getFlightNumber());
        assertEquals("12D", flightInfo.getSeatNumber());
        assertEquals("SJC", flightInfo.getOrigin());
        assertEquals("MKE", flightInfo.getDestination());
        assertEquals("11:00PM", flightInfo.getArrivalTime());
    }

    @Test
    public void FlightInfo_toString() throws Exception {
        FlightInfo flightInfo = new FlightInfo("Flight|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|1234|12D|SJC|MKE|11:00PM");
        assertEquals("myName 1234 - Conf#", flightInfo.toString());
    }

    @Test
    public void FlightInfo_toShareableString() throws Exception {
        FlightInfo flightInfo = new FlightInfo("Name", "Conf#",
                "123-456-7890", "123", "12D", "SJC",
                "MKE", "10:00AM", "12:00PM", "1/1/2018");

        assertEquals("Flight|Name|Conf#|123-456-7890|1/1/2018|10:00AM|123|12D|SJC|MKE|" +
                        "12:00PM",
                flightInfo.toShareableString());
    }

    @Test
    public void FlightInfo_equals() throws Exception {
        FlightInfo flightInfo1 = new FlightInfo("Name", "Conf#",
                "123-456-7890", "123", "12D", "SJC",
                "MKE", "10:00AM", "12:00PM", "1/1/2018");
        FlightInfo flightInfo2 = new FlightInfo("Name", "Conf#",
                "123-456-7890", "123", "12D", "SJC",
                "MKE", "10:00AM", "12:00PM", "1/1/2018");
        assertEquals(true, flightInfo1.equals(flightInfo2));
        flightInfo2 = new FlightInfo("Name", "Conf#",
                "123-456-7890", "123", "12D", "LAX",
                "MKE", "10:00AM", "12:00PM", "1/1/2018");
        assertEquals(false, flightInfo1.equals(flightInfo2));
    }
}
