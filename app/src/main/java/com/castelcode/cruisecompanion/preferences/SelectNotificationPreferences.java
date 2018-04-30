package com.castelcode.cruisecompanion.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.utils.SettingsConstants;
import com.castelcode.cruisecompanion.utils.SharedPreferencesManager;

public class SelectNotificationPreferences extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener {
    private static boolean flightNotifications;
    private static boolean hotelNotifications;
    private static boolean busNotifications;
    private static boolean cruiseNotifications;

    private CheckBox flightNotificationsCheckBox;
    private CheckBox hotelNotificationsCheckBox;
    private CheckBox busNotificationsCheckBox;
    private CheckBox cruiseNotificationsCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_notification_preferences);

        flightNotificationsCheckBox = (CheckBox) findViewById(R.id.flight_notifications);
        flightNotificationsCheckBox.setOnCheckedChangeListener(this);

        hotelNotificationsCheckBox = (CheckBox) findViewById(R.id.hotel_notifications);
        hotelNotificationsCheckBox.setOnCheckedChangeListener(this);

        busNotificationsCheckBox = (CheckBox) findViewById(R.id.bus_notifications);
        busNotificationsCheckBox.setOnCheckedChangeListener(this);

        cruiseNotificationsCheckBox = (CheckBox) findViewById(R.id.cruise_notifications);
        cruiseNotificationsCheckBox.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flightNotifications = SharedPreferencesManager.getBooleanFromSP(
                SettingsConstants.FLIGHT_NOTIFICATION_PREFERENCE_TAG,
                this.getApplicationContext());
        flightNotificationsCheckBox.setChecked(flightNotifications);
        hotelNotifications = SharedPreferencesManager.getBooleanFromSP(
                SettingsConstants.HOTEL_NOTIFICATION_PREFERENCE_TAG,
                this);
        hotelNotificationsCheckBox.setChecked(hotelNotifications);
        busNotifications = SharedPreferencesManager.getBooleanFromSP(
                SettingsConstants.BUS_NOTIFICATION_PREFERENCE_TAG,
                this);
        busNotificationsCheckBox.setChecked(busNotifications);
        cruiseNotifications = SharedPreferencesManager.getBooleanFromSP(
                SettingsConstants.CRUISE_NOTIFICATION_PREFERENCE_TAG,
                this);
        cruiseNotificationsCheckBox.setChecked(cruiseNotifications);
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.putExtra(SettingsConstants.FLIGHT_NOTIFICATION_KEY, flightNotifications);
        mIntent.putExtra(SettingsConstants.BUS_NOTIFICATION_KEY, busNotifications);
        mIntent.putExtra(SettingsConstants.HOTEL_NOTIFICATION_KEY, hotelNotifications);
        mIntent.putExtra(SettingsConstants.CRUISE_NOTIFICATION_KEY, cruiseNotifications);
        setResult(RESULT_OK, mIntent);
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == cruiseNotificationsCheckBox) {
            cruiseNotifications = cruiseNotificationsCheckBox.isChecked();
        }
        else if(buttonView == hotelNotificationsCheckBox) {
            hotelNotifications = hotelNotificationsCheckBox.isChecked();
        }
        else if(buttonView == flightNotificationsCheckBox) {
            flightNotifications = flightNotificationsCheckBox.isChecked();
        }
        else if(buttonView == busNotificationsCheckBox) {
            busNotifications = busNotificationsCheckBox.isChecked();
        }
    }
}