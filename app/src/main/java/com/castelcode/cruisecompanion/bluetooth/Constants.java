package com.castelcode.cruisecompanion.bluetooth;

/**
 * Defines several constants used between {@link BluetoothShareService} and the UI.
 */
public interface Constants {

    // Message types sent from the BluetoothShareService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothShareService Handler
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";

}
