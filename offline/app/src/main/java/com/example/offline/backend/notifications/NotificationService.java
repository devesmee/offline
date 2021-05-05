package com.example.offline.backend.notifications;

import android.content.Context;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.offline.backend.apps.SystemManager;
import com.example.offline.backend.apps.WhitelistedAppsManager;
import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;

import java.util.ArrayList;

public class NotificationService extends NotificationListenerService {

    private final String TAG = this.getClass().getSimpleName();
    Context context;
    private static ArrayList<String> blackListedApps = new ArrayList<>();
    private static NotificationService notificationServiceInstance;
    private Storage localStorage;

    public NotificationService() {
    }

    public static NotificationService getInstance() {
        if (notificationServiceInstance == null) {
            notificationServiceInstance = new NotificationService();
        }
        return notificationServiceInstance;

    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        SystemManager systemManager = SystemManager.getInstance(context);
        WhitelistedAppsManager whitelistedAppsManager = WhitelistedAppsManager.getInstance(context);
        ArrayList<String> whiteListedApps = whitelistedAppsManager.getWhitelistedApps();
        localStorage = new Storage(context);
        blackListedApps = systemManager.getBlackListedApps(whiteListedApps);

        super.onCreate();
    }

    /**
     * Check in the local storage if we should intercept notifications or  not
     *
     * @return true | false
     */
    private boolean isBlockNotificationServiceEnabled() {
        return this.localStorage.getBoolean(Key.NOTIFICATION_ENABLED);
    }


    /**
     * When notification arrives check if it included in the white list apps
     * If yes do not block notification, otherwise kill them all
     *
     * @param sbn status bar  notification
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNotificationPosted");
        Log.i(TAG, "ID :" + sbn.getId() + " \t " + sbn.getNotification().tickerText + " \t " + sbn.getPackageName());
        Log.i("Application", sbn.getKey());

        if (isBlockNotificationServiceEnabled()) {
            Log.e(TAG, "BLOCK NOTIFICATION SERVICE ENABLED");
            for (String app : blackListedApps) {
                if (app.equalsIgnoreCase(sbn.getPackageName())) {
                    cancelNotification(sbn.getKey());
                    //  Log.e(TAG, "Canceling "+ sbn.getPackageName());
                }
            }
        } else {
            Log.e(TAG, "BLOCK NOTIFICATION SERVICE IS NOT ENABLED");
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNotificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + " \t " + sbn.getNotification().tickerText + " \t " + sbn.getPackageName());
    }

}
