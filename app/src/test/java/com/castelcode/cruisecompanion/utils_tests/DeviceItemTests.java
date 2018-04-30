package com.castelcode.cruisecompanion.utils_tests;

import com.castelcode.cruisecompanion.utils.DeviceItem;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeviceItemTests {

    @Test
    public void deviceItem_creation_test() throws Exception {
        DeviceItem deviceItem = new DeviceItem("myName", "myAddress", false);
        assertEquals(false, deviceItem.getType());
        assertEquals("myName", deviceItem.getDeviceName());
        assertEquals("myAddress", deviceItem.getAddress());
    }

    @Test
    public void deviceItem_equals_test() throws Exception {
        DeviceItem deviceItem = new DeviceItem("myName", "myAddress", false);
        DeviceItem secondDeviceItem = new DeviceItem("myName", "mySecondAddress",
                true);
        assertEquals(true, deviceItem.equals(secondDeviceItem));
        DeviceItem thirdDeviceItem = new DeviceItem("myDifferentName",
                "myNewAddress", false);
        assertEquals(false, deviceItem.equals(thirdDeviceItem));
    }
}
