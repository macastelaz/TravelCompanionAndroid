package com.castelcode.cruisecompanion.notification_preference_wrapper_tests;

import com.castelcode.cruisecompanion.NotificationPreference.NotificationPreferenceWrapper;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class NotificationPreferenceWrapperTests {

    @Test
    public void NotificationPreferenceWrapper_creation() throws Exception {
        NotificationPreferenceWrapper notificationPreferenceWrapper =
                new NotificationPreferenceWrapper("TEST_NOTIFICATION", true);
        assertEquals("TEST_NOTIFICATION",
                notificationPreferenceWrapper.getPreferenceName());
        assertEquals(true,
                notificationPreferenceWrapper.getPreferenceValue());
    }
}
