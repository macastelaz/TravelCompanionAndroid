package com.castelcode.cruisecompanion.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Pair;

import com.castelcode.cruisecompanion.HomePage;

import java.util.ArrayList;
import java.util.Date;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null &&
                intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ArrayList<Pair<PendingIntent, Date>> pendingIntentValues =
                   new ArrayList<>(WakefulReceiver.pendingIntents.values());
            context = HomePage.getContext();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            for(int i = 0; i < WakefulReceiver.pendingIntents.size(); i++) {
                if(Build.VERSION.SDK_INT >= 19 && alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                            pendingIntentValues.get(i).second.getTime(),
                            pendingIntentValues.get(i).first);
                }
            }

        }
    }
}
