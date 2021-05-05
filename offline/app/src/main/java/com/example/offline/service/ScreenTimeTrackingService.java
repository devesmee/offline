package com.example.offline.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.offline.MainActivity;
import com.example.offline.R;

//This service is not used
public class ScreenTimeTrackingService extends Service {

    private final String ACTIVITY_CHANNEL_ID = "ACTIVITY_CHANNEL";
    private final int ACTIVITY_ID = 1;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        ScreenTimeTrackingService getService() {
            return ScreenTimeTrackingService.this;
        }
    }

    @Override
    public void onCreate() {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this, ACTIVITY_CHANNEL_ID)
                .setContentTitle(getText(R.string.activity_notification_title))
                .setContentText(getText(R.string.activity_notification_message))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setTicker(getString(R.string.activity_notification_ticker))
                .build();

        startForeground(ACTIVITY_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.activity_tracking_channel);
            String description = getString(R.string.activity_tracking_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(ACTIVITY_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
