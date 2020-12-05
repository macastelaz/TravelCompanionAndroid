package com.castelcode.travelcompanion.NotificationPreference;

import java.io.Serializable;

/**
 * Created by Matt on 4/14/18.
 */

public class NotificationPreferenceWrapper implements Serializable {
    private String preferenceName;
    private boolean preferenceValue;

    public NotificationPreferenceWrapper(String name, boolean value) {
        preferenceName = name;
        preferenceValue = value;
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public boolean getPreferenceValue() {
        return preferenceValue;
    }
}
