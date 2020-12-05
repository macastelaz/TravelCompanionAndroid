package com.castelcode.travelcompanion.info_items_tests;

import android.os.Parcel;
import android.os.Parcelable;

import com.castelcode.travelcompanion.Cruise;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.CruiseInfo;

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
public class CruiseInfoTests {
    @Test
    public void CruiseInfo_parameterizedCreation() throws Exception {
        CruiseInfo cruiseInfo = new CruiseInfo("Name", "Conf#",
                "123-456-7890", "1745", "testShip",
                "1/1/2018", "12:00PM");
        assertEquals("Name", cruiseInfo.getPrimaryName());
        assertEquals("Conf#", cruiseInfo.getConfirmationNumber());
        assertEquals("123-456-7890", cruiseInfo.getPhoneNumber());
        assertEquals("1745", cruiseInfo.getRoomNumber());
        assertEquals("testShip", cruiseInfo.getShipName());
        assertEquals("1/1/2018", cruiseInfo.getStartDate());
        assertEquals("12:00PM", cruiseInfo.getStartTime());
    }

    @Test
    public void CruiseInfo_parcelCreation() throws Exception {
        Parcel parcel = Mockito.spy(Parcel.obtain());
        Parcelable parcelable = new CruiseInfo("Name", "Conf#",
                "123-456-7890", "1745", "testShip",
                "1/1/2018", "12:00PM");
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        CruiseInfo cruiseInfoFromParcel = new CruiseInfo(parcel);
        assertEquals(true, ((CruiseInfo)parcelable).equals(cruiseInfoFromParcel));
    }


    @Test
    public void CruiseInfo_writeToParcel() throws Exception {
        Parcel parcel = Mockito.spy(Parcel.obtain());
        Parcelable parcelable = new CruiseInfo("Name", "Conf#",
                "123-456-7890", "1745", "testShip",
                "1/1/2018", "12:00PM");
        parcelable.writeToParcel(parcel, 0);

        InOrder inOrder = Mockito.inOrder(parcel);
        for (Field field : parcelable.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(String.class)) {
                if(!field.get(parcelable).toString().equals("Cruise")) {
                    inOrder.verify(parcel).writeString(field.get(parcelable).toString());
                }
            }
        }
    }

    @Test
    public void CruiseInfo_shareableStringCreation() throws Exception {
        CruiseInfo cruiseInfo = new CruiseInfo("Cruise|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|1234|test_ship");

        assertEquals("myName", cruiseInfo.getPrimaryName());
        assertEquals("Conf#", cruiseInfo.getConfirmationNumber());
        assertEquals("123-456-7890", cruiseInfo.getPhoneNumber());
        assertEquals("1/1/2018", cruiseInfo.getStartDate());
        assertEquals("8:00PM", cruiseInfo.getStartTime());
        assertEquals("1234", cruiseInfo.getRoomNumber());
        assertEquals("test_ship", cruiseInfo.getShipName());
    }

    @Test
    public void CruiseInfo_toString() throws Exception {
        CruiseInfo cruiseInfo = new CruiseInfo("Cruise|myName|Conf#|123-456-7890|" +
                "1/1/2018|8:00PM|1234|test_ship");
        assertEquals("myName test_ship - Conf#", cruiseInfo.toString());
    }

    @Test
    public void CruiseInfo_toShareableString() throws Exception {
        CruiseInfo cruiseInfo = new CruiseInfo("Name", "Conf#",
                "123-456-7890", "1745", "testShip",
                "1/1/2018", "12:00PM");

        assertEquals("Cruise|Name|Conf#|123-456-7890|1/1/2018|12:00PM|1745|testShip",
                cruiseInfo.toShareableString());
    }

    @Test
    public void CruiseInfo_equals() throws Exception {
        CruiseInfo cruiseInfo1 = new CruiseInfo("Name", "Conf#",
                "098-765-4321", "4567", "test_Ship",
                "3/1/2018", "4:00PM");
        CruiseInfo cruiseInfo2 = new CruiseInfo("Name", "Conf#",
                "123-456-7890", "1745", "testShip",
                "1/1/2018", "12:00PM");
        assertEquals(true, cruiseInfo1.equals(cruiseInfo2));
        cruiseInfo2 = new CruiseInfo("Name", "Conf#1",
                "123-456-7890", "1745", "testShip",
                "1/1/2018", "12:00PM");
        assertEquals(false, cruiseInfo1.equals(cruiseInfo2));
    }
}
