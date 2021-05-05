package com.example.offline.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.offline.backend.notifications.NotificationBroadcastReceiver;

public class NotificationService extends Service {

    private static NotificationBroadcastReceiver notificationBroadcastReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        registerNotificationService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void registerNotificationService() {
        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        // register service when following actions happen
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_MAIN);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_USER_UNLOCKED);
        filter.addAction(Intent.CATEGORY_HOME);
        getApplicationContext().registerReceiver(notificationBroadcastReceiver, filter);
    }
}
