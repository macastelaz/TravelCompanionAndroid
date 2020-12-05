package com.castelcode.travelcompanion.info_items_tests;

import android.os.Parcel;
import android.os.Parcelable;

import com.castelcode.travelcompanion.trip_info_add_activity.info_items.HotelInfo;

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
public class HotelInfoTests {
    @Test
    public void HotelInfo_parameterizedCreation() throws Exception {
        HotelInfo hotelInfo = new HotelInfo("Name", "Conf#",
                "123-456-7890", "123 address way", "city",
                "state", "1/1/2018", "3:00PM",
                "1/3/2018", "10:00AM");
        assertEquals("Name", hotelInfo.getPrimaryName());
        assertEquals("Conf#", hotelInfo.getConfirmationNumber());
        assertEquals("123-456-7890", hotelInfo.getPhoneNumber());
        assertEquals("123 address way", hotelInfo.getAddress());
        assertEquals("city", hotelInfo.getCity());
        assertEquals("state", hotelInfo.getStateProvince());
        assertEquals("1/1/2018", hotelInfo.getStartDate());
        assertEquals("3:00PM", hotelInfo.getStartTime());
        assertEquals("1/3/2018", hotelInfo.getCheckOutDate());
        assertEquals("10:00AM", hotelInfo.getCheckOutTime());
    }

    @Test
    public void HotelInfo_parcelCreation() throws Exception {
        Parcel parcel = Mockito.spy(Parcel.obtain());
        Parcelable parcelable = new HotelInfo("Name", "Conf#",
                "123-456-7890", "123 address way", "city",
                "state", "1/1/2018", "3:00PM",
                "1/3/2018", "10:00AM");
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        HotelInfo cruiseInfoFromParcel = new HotelInfo(parcel);
        assertEquals(true, ((HotelInfo)parcelable).equals(cruiseInfoFromParcel));
    }


    @Test
    public void HotelInfo_writeToParcel() throws Exception {
        Parcel parcel = Mockito.spy(Parcel.obtain());
        Parcelable parcelable = new HotelInfo("Name", "Conf#",
                "123-456-7890", "123 address way", "city",
                "state", "1/1/2018", "3:00PM",
                "1/3/2018", "10:00AM");
        parcelable.writeToParcel(parcel, 0);

        InOrder inOrder = Mockito.inOrder(parcel);
        for (Field field : parcelable.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(String.class)) {
                if(!field.get(parcelable).toString().equals("Hotel")) {
                    inOrder.verify(parcel).writeString(field.get(parcelable).toString());
                }
            }
        }
    }

    @Test
    public void HotelInfo_shareableStringCreation() throws Exception {
        HotelInfo hotelInfo = new HotelInfo("Hotel|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|123 test way|city|state|1/3/2018|11:00PM");
        assertEquals("myName", hotelInfo.getPrimaryName());
        assertEquals("Conf#", hotelInfo.getConfirmationNumber());
        assertEquals("123-456-7890", hotelInfo.getPhoneNumber());
        assertEquals("1/1/2018", hotelInfo.getStartDate());
        assertEquals("8:00PM", hotelInfo.getStartTime());
        assertEquals("123 test way", hotelInfo.getAddress());
        assertEquals("city", hotelInfo.getCity());
        assertEquals("state", hotelInfo.getStateProvince());
        assertEquals("1/3/2018", hotelInfo.getCheckOutDate());
        assertEquals("11:00PM", hotelInfo.getCheckOutTime());
    }

    @Test
    public void HotelInfo_toString() throws Exception {
        HotelInfo hotelInfo = new HotelInfo("Hotel|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|123 test way|city|state|1/3/2018|11:00PM");
        assertEquals("myName - Conf#", hotelInfo.toString());
    }

    @Test
    public void HotelInfo_toShareableString() throws Exception {
        HotelInfo hotelInfo = new HotelInfo("Name", "Conf#",
                "123-456-7890", "123 address way", "city",
                "state", "1/1/2018", "3:00PM",
                "1/3/2018", "10:00AM");

        assertEquals("Hotel|Name|Conf#|123-456-7890|1/1/2018|3:00PM|123 address way|city" +
                        "|state|1/3/2018|10:00AM",
                hotelInfo.toShareableString());
    }

    @Test
    public void HotelInfo_equals() throws Exception {
        HotelInfo hotelInfo1 = new HotelInfo("Name", "Conf#",
                "123-456-7890", "123 address way", "newCity",
                "state", "1/1/2018", "3:00PM",
                "1/3/2018", "10:00AM");
        HotelInfo hotelInfo2 = new HotelInfo("Name", "Conf#",
                "123-456-7890", "123 address way", "city",
                "newState", "1/3/2018", "4:00PM",
                "1/5/2018", "11:00AM");
        assertEquals(true, hotelInfo1.equals(hotelInfo2));
        hotelInfo2 = new HotelInfo("NameNew", "Conf#",
                "123-456-7890", "123 address way", "city",
                "state", "1/1/2018", "3:00PM",
                "1/3/2018", "10:00AM");
        assertEquals(false, hotelInfo1.equals(hotelInfo2));
    }
}
