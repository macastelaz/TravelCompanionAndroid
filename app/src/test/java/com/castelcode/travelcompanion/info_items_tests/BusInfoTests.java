package com.castelcode.travelcompanion.info_items_tests;

import android.os.Parcel;
import android.os.Parcelable;

import com.castelcode.travelcompanion.trip_info_add_activity.info_items.BusInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

@Config(manifest=Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class BusInfoTests {
    @Test
    public void BusInfo_parameterizedCreation() throws Exception {
        BusInfo busInfo = new BusInfo("Name", "Conf#",
                "123-456-7890", "12D", "SJC", "MKE",
                "12:00PM", "2:00PM", "1/1/2018");
        assertEquals("Name", busInfo.getPrimaryName());
        assertEquals("Conf#", busInfo.getConfirmationNumber());
        assertEquals("123-456-7890", busInfo.getPhoneNumber());
        assertEquals("12D", busInfo.getSeatNumber());
        assertEquals("SJC", busInfo.getOrigin());
        assertEquals("MKE", busInfo.getDestination());
        assertEquals("12:00PM", busInfo.getStartTime());
        assertEquals("2:00PM", busInfo.getArrivalTime());
        assertEquals("1/1/2018", busInfo.getStartDate());
    }

    @Test
    public void BusInfo_parcelCreation() throws Exception {
        Parcel parcel = Mockito.spy(Parcel.obtain());
        Parcelable parcelable = new BusInfo("Bus|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|12D|MKE|SJC|10:00PM");
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        BusInfo busInfoFromParcel = new BusInfo(parcel);
        assertEquals(true, ((BusInfo)parcelable).equals(busInfoFromParcel));
    }


    @Test
    public void BusInfo_writeToParcel() throws Exception {
        Parcel parcel = Mockito.spy(Parcel.obtain());
        Parcelable parcelable = new BusInfo("Bus|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|12D|MKE|SJC|10:00PM");
        parcelable.writeToParcel(parcel, 0);

        InOrder inOrder = Mockito.inOrder(parcel);
        for (Field field : parcelable.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(String.class)) {
                if(!field.get(parcelable).toString().equals("Bus")) {
                    inOrder.verify(parcel).writeString(field.get(parcelable).toString());
                }
            }
        }
    }

    @Test
    public void BusInfo_shareableStringCreation() throws Exception {
        BusInfo busInfo = new BusInfo("Bus|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|12D|MKE|SJC|10:00PM");

        assertEquals("myName", busInfo.getPrimaryName());
        assertEquals("Conf#", busInfo.getConfirmationNumber());
        assertEquals("123-456-7890", busInfo.getPhoneNumber());
        assertEquals("12D", busInfo.getSeatNumber());
        assertEquals("MKE", busInfo.getOrigin());
        assertEquals("SJC", busInfo.getDestination());
        assertEquals("8:00PM", busInfo.getStartTime());
        assertEquals("10:00PM", busInfo.getArrivalTime());
        assertEquals("1/1/2018", busInfo.getStartDate());
    }

    @Test
    public void BusInfo_toString() throws Exception {
        BusInfo busInfo = new BusInfo("Bus|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|12D|MKE|SJC|10:00PM");

        assertEquals("myName - Conf#", busInfo.toString());
    }

    @Test
    public void BusInfo_toShareableString() throws Exception {
        BusInfo busInfo = new BusInfo("Name", "Conf#",
                "123-456-7890", "12D", "SJC", "MKE",
                "12:00PM", "2:00PM", "1/1/2018");

        assertEquals("Bus|Name|Conf#|123-456-7890|1/1/2018|12:00PM|12D|SJC|MKE|2:00PM",
                busInfo.toShareableString());
    }

    @Test
    public void BusInfo_equals() throws Exception {
        BusInfo busInfo1 = new BusInfo("Name", "Conf#",
                "098-765-4321", "15D", "SJC", "MKE",
                "1:00PM", "3:00PM", "3/1/2018");
        BusInfo busInfo2 = new BusInfo("Name", "Conf#",
                "123-456-7890", "12D", "SJC", "MKE",
                "12:00PM", "2:00PM", "1/1/2018");
        assertEquals(true, busInfo1.equals(busInfo2));
        busInfo2 = new BusInfo("Name", "Conf#",
                "123-456-7890", "12D", "SJC", "ORD",
                "12:00PM", "2:00PM", "1/1/2018");
        assertEquals(false, busInfo1.equals(busInfo2));
    }
}
