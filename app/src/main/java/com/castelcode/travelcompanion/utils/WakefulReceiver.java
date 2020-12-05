package com.castelcode.travelcompanion.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.Pair;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.castelcode.travelcompanion.HomePage;
import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.tile_activities.TripInformation;

public class WakefulReceiver extends BroadcastReceiver {
    private static AlarmManager mAlarmManager;
    public static HashMap<String, Pair<PendingIntent, Date>> pendingIntents = new HashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent resultIntent = new Intent(context, TripInformation.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
        bigTextStyle.bigText(intent.getStringExtra("NotificationMessage"));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TripInfoAlert")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_cruise_ship_256)
                .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(resultPendingIntent)
                .setContentText(intent.getStringExtra("NotificationMessage"))
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(bigTextStyle);


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("TripInfoAlert",
                        "Cruise Companion",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            Notification notification = builder.build();
            notificationManager.notify(0, notification);
            cancelAlarm(context, intent.getStringExtra("AlertId"));
        }
    }

    /**
     * Sets the next alarm to run. When the alarm fires,
     * the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context the context of the app's Activity.
     */
    public static void setAlarm(Context context, Calendar timeToAlert, String alertId,
                         String notificationMessage) {
        if(pendingIntents.get(alertId) != null) { //Alert already present, no need to reschedule
            return;
        }
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulReceiver.class);
        intent.putExtra("AlertId", alertId);
        intent.putExtra("NotificationMessage", notificationMessage);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alertId.hashCode(), intent, 0);

        Date date = timeToAlert.getTime();
        if(Build.VERSION.SDK_INT >= 19) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);
            pendingIntents.put(alertId, new Pair<>(alarmIntent, date));
        }

        // Enable {@code BootReceiver} to automatically restart when the
        // device is rebooted.
        //// TODO: you may need to reference the context by ApplicationActivity.class
        ComponentName receiver = new ComponentName(HomePage.getContext(), BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels the next alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     * @param context the context of the app's Activity
     */
    public static void cancelAlarm(Context context, String alertId) {
        Log.d("WakefulAlarmReceiver", "{cancelAlarm}");

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(pendingIntents.get(alertId) != null) {
            PendingIntent alarmIntent = pendingIntents.get(alertId).first;
            if (alarmIntent != null) {
                mAlarmManager.cancel(alarmIntent);
            }
            pendingIntents.remove(alertId);
        }

        // Disable {@code BootReceiver} so that it doesn't automatically restart when the device is rebooted.
        //// TODO: you may need to reference the context by ApplicationActivity.class
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}
