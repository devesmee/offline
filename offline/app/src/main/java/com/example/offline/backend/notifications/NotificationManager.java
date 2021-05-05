package com.example.offline.backend.notifications;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import com.example.offline.backend.storage.Storage;

import static com.example.offline.backend.storage.Key.NOTIFICATION_ENABLED;

public class NotificationManager {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private Context mContext;
    private Storage mLocalStorage;
    private static NotificationManager notificationManagerInstance;

    private NotificationManager(Context context) {
        this.mContext = context;
        this.mLocalStorage = new Storage(context);
    }

    public static NotificationManager getInstance(Context context) {
        if (notificationManagerInstance == null) {
            notificationManagerInstance = new NotificationManager(context);
        }
        return notificationManagerInstance;
    }

    /**
     * Start block notification service only if user has given permissions
     */
    public void startBlockNotificationService() {
        if (askForNotificationPermissions()) {
            this.mLocalStorage.saveBoolean(NOTIFICATION_ENABLED, true);
        }
    }

    /**
     * Stop the service and do not block notifications anymore
     */
    public void stopBlockNotificationService() {
        this.mLocalStorage.saveBoolean(NOTIFICATION_ENABLED, false);
    }

    /**
     * Ask for notification service permission
     *
     * @return true|false
     */
    public boolean askForNotificationPermissions() {
        if (isNotificationServiceEnabled()) {
            return true;
        }
        AlertDialog notificationPermissionDialog = buildNotificationPermissionDialog();
        notificationPermissionDialog.show();
        return false;
    }

    /**
     * Checks if the notification listener service is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    public boolean isNotificationServiceEnabled() {
        String pkgName = mContext.getPackageName();
        final String flat = Settings.Secure.getString(mContext.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     *
     * @return Alert dialog that redirects the user to the notifications settings screen
     */
    public AlertDialog buildNotificationPermissionDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Notifications permission");
        alertDialogBuilder.setMessage("In order to make sure that you do not get disturbed from the applications that are not listed on the white list we need permissions to manage your notifications");
        alertDialogBuilder.setPositiveButton("Grant permission",
                (dialog, id) -> mContext.startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        alertDialogBuilder.setNegativeButton("Cancel",
                (dialog, id) -> {
                    // If user doesn't accept we should tell him that the app will not function well or certain features will not work
                });
        return alertDialogBuilder.create();
    }
}