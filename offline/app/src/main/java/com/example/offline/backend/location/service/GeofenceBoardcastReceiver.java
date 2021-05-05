package com.example.offline.backend.location.service;


import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.notifications.NotificationManager;
import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Date;

public class GeofenceBoardcastReceiver extends BroadcastReceiver {

    private DayManager dayManager;
    private Storage storage;
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        //Toast.makeText(context, "Geofence triggered", Toast.LENGTH_SHORT).show();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        dayManager = DayManager.getInstance(context);
        storage = new Storage(context);
        notificationManager = NotificationManager.getInstance(context);
        int type = geofencingEvent.getGeofenceTransition();

        switch (type) {
            case Geofence
                    .GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "Enter", Toast.LENGTH_SHORT).show();
                if (!storage.getBoolean(Key.IS_TRACKING)) {
                    onEnterCampusEvent(context);
                }
                break;
            case Geofence
                    .GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "Exit", Toast.LENGTH_SHORT).show();
                if (storage.getBoolean(Key.IS_TRACKING)) {
                    onLeaveCampusEvent(context);
                }
                break;
        }
    }


    private void onEnterCampusEvent(Context context) {
        notificationManager.startBlockNotificationService();
        dayManager.saveEnterTime();
        storage.saveBoolean(Key.IS_TRACKING, true);
    }

    private void onLeaveCampusEvent(Context context) {
        Long leaveTime = new Date().getTime();
        dayManager.saveLeaveTime(leaveTime);
        Long screenTimeForCurrentCampusPeriod = getScreenTimeForCurrentCampusPeriod(context, leaveTime);
        dayManager.saveOnScreenTime(screenTimeForCurrentCampusPeriod);
        notificationManager.stopBlockNotificationService();
        storage.saveBoolean(Key.IS_TRACKING, false);
    }

    public Long getScreenTimeForCurrentCampusPeriod(Context context, Long campusLeaveTime) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        long campusEnter = dayManager.getCurrentDay().getCampusTimes().get(dayManager.getCurrentDay().getCampusTimes().size() - 1).getEnterTime(); //campus enter time
        long campusLeave = campusLeaveTime == 0 ? System.currentTimeMillis() : campusLeaveTime; //campus leave time

        UsageEvents usageEvents = usageStatsManager.queryEvents(campusEnter, campusLeave);

        long timeScreenActive = 0;
        long startTime = campusEnter;
        long endTime = 0;

        UsageEvents.Event e = new UsageEvents.Event();
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(e);
            if (e.getEventType() == UsageEvents.Event.SCREEN_INTERACTIVE) {
                startTime = e.getTimeStamp();
            } else if (e.getEventType() == UsageEvents.Event.SCREEN_NON_INTERACTIVE || e.getEventType() == UsageEvents.Event.DEVICE_SHUTDOWN) {
                endTime = e.getTimeStamp();
            }

            if (startTime > 0 && endTime > 0 && endTime > startTime) {
                timeScreenActive += (endTime - startTime);
                startTime = 0;
                endTime = 0;
            }
        }

        endTime = campusLeaveTime == 0 ? System.currentTimeMillis() : campusLeaveTime;
        timeScreenActive += (endTime - startTime);

        return timeScreenActive;
    }

}
