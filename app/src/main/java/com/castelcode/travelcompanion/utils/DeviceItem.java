package com.castelcode.travelcompanion.utils;

public class DeviceItem {

    private String deviceName;
    private String address;
    private boolean type; //True is iOS, false is android

    public String getDeviceName() {
        return deviceName;
    }

    public String getAddress() {
        return address;
    }

    public DeviceItem(String name, String address, boolean type){
        this.deviceName = name;
        this.address = address;
        this.type = type;
    }

    public boolean getType() {
        return type;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if(!(o instanceof  DeviceItem)){
            return false;
        }
        DeviceItem itemIn = (DeviceItem) o;
        return itemIn.getDeviceName().equals(this.getDeviceName());
    }
}