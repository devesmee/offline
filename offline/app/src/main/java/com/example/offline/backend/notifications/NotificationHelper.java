package com.example.offline.backend.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.offline.R;

public class NotificationHelper extends ContextWrapper {

    private static final Integer NOTIFICATION_ID = 1;
    private static final String EXTRA_NOTIFICATION_ID = "1";
    private static final String ACTION_HAPPY = "happy";
    private static final String ACTION_NEUTRAL = "neutral";
    private static final String ACTION_SAD = "sad";

    private static NotificationHelper notificationHelperInstance;

    private NotificationBroadcastReceiver notificationBroadcastReceiver;


    private NotificationHelper(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_HAPPY);
        filter.addAction(ACTION_NEUTRAL);
        filter.addAction(ACTION_SAD);
        context.registerReceiver(notificationBroadcastReceiver, filter);
    }

    public static NotificationHelper getInstance(Context context) {
        if (notificationHelperInstance == null) {
            notificationHelperInstance = new NotificationHelper(context);
        }
        return notificationHelperInstance;
    }

    private String CHANNEL_NAME = "channel";
    private String CHANNEL_ID = CHANNEL_NAME;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("this is the description");
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    /**
     * @param summary  summary
     * @param title    title
     * @param activity the activity leading to
     */
    public void sendNotification(String summary, String title, Class activity) {
        Intent intent = new Intent(this, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent sadProductivityFeelingIntent = new Intent(this, NotificationBroadcastReceiver.class);
        sadProductivityFeelingIntent.setAction(ACTION_SAD);
        sadProductivityFeelingIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent sadProductivityFeelingPendingIntent =
                PendingIntent.getBroadcast(this, 0, sadProductivityFeelingIntent, 0);

        Intent neutralProductivityFeelingIntent = new Intent(this, NotificationBroadcastReceiver.class);
        neutralProductivityFeelingIntent.setAction(ACTION_NEUTRAL);
        neutralProductivityFeelingIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent neutralProductivityFeelingPendingIntent =
                PendingIntent.getBroadcast(this, 0, neutralProductivityFeelingIntent, 0);

        Intent happyProductivityFeelingIntent = new Intent(this, NotificationBroadcastReceiver.class);
        happyProductivityFeelingIntent.setAction(ACTION_HAPPY);
        happyProductivityFeelingIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent happyProductivityFeelingPendingIntent =
                PendingIntent.getBroadcast(this, 0, happyProductivityFeelingIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().setSummaryText(summary).setBigContentTitle(title))
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.sad, getString(R.string.productivity_feeling_sad),
                        sadProductivityFeelingPendingIntent)
                .addAction(R.drawable.neutral, getString(R.string.productivity_feeling_neutral),
                        neutralProductivityFeelingPendingIntent)
                .addAction(R.drawable.happy, getString(R.string.productivity_feeling_happy),
                        happyProductivityFeelingPendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }

    public void cancelNotification() {
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
    }
}