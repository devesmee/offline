package com.example.offline.backend.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.day.ProductivityFeeling;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "NBR";
    private static final String ACTION_HAPPY = "happy";
    private static final String ACTION_NEUTRAL = "neutral";
    private static final String ACTION_SAD = "sad";
    private DayManager dayManager;
    private NotificationHelper notificationHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        dayManager = DayManager.getInstance(context);
        notificationHelper = NotificationHelper.getInstance(context);

        switch (intent.getAction()) {
            case ACTION_HAPPY:
                dayManager.saveProductivityFeeling(ProductivityFeeling.HAPPY);
                dayManager.saveAwaitingProductivityFeeling(false);
                dayManager.getCurrentDay().setAwaitingProductivityInput(false);
                notificationHelper.cancelNotification();
                break;
            case ACTION_NEUTRAL:
                dayManager.saveProductivityFeeling(ProductivityFeeling.NEUTRAL);
                dayManager.saveAwaitingProductivityFeeling(false);
                dayManager.getCurrentDay().setAwaitingProductivityInput(false);
                notificationHelper.cancelNotification();
                break;
            case ACTION_SAD:
                dayManager.saveProductivityFeeling(ProductivityFeeling.SAD);
                dayManager.saveAwaitingProductivityFeeling(false);
                dayManager.getCurrentDay().setAwaitingProductivityInput(false);
                notificationHelper.cancelNotification();
                break;
        }
    }
}
