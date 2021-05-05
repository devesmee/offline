package com.example.offline.backend.apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemManager {
    private static SystemManager systemManagerInstance;
    private final PackageManager packageManager;
    private Context context;
    /**
     * Apps that need to be filtered out from the system apps shown in the whitelistt
     */
    private static final String CAMERA_APP = "Camera";
    private static final String CONTACTS_APP = "Contacts";
    private static final String SETTINGS_APP = "Settings";
    private static final String VOICE_SEARCH_APP = "Voice Search";
    private static final String OFFLINE = "Offline";
    private final List<String> systemAppsFilter = Arrays.asList(CAMERA_APP, CONTACTS_APP, SETTINGS_APP, VOICE_SEARCH_APP, OFFLINE);

    public SystemManager(Context context) {
        this.context = context;
        this.packageManager = context.getPackageManager();
    }

    public List<String> getSystemAppsFilter() {
        return systemAppsFilter;
    }

    public static SystemManager getInstance(Context context) {
        if (systemManagerInstance == null) {
            systemManagerInstance = new SystemManager(context);
        }
        return systemManagerInstance;
    }

    /**
     * Extract the installed apps names and icons
     *
     * @return a list with system apps
     */
    public List<SystemApp> getSystemApps() {
        List<SystemApp> systemApps = new ArrayList<>();

        List<ResolveInfo> installedApps = getFilteredSystemApps(getInstalledSystemApps());
        for (ResolveInfo resolveInfo : installedApps) {
            SystemApp systemApp = new SystemApp(resolveInfo.loadLabel(packageManager).toString(), resolveInfo.activityInfo.loadIcon(packageManager), resolveInfo.activityInfo.packageName);
            systemApps.add(systemApp);
        }
        return systemApps;
    }

    /**
     * Get all the information about the apps that are installed in the system
     *
     * @return a list with info about the installed apps
     */
    private List<ResolveInfo> getInstalledSystemApps() {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return packageManager.queryIntentActivities(main_intent, 0);
    }

    /**
     * Filter out some specific system apps such as camera, contacts, settings, etc
     *
     * @param systemApps to be filtered
     * @return a list with filtered system apps
     */
    private List<ResolveInfo> getFilteredSystemApps(List<ResolveInfo> systemApps) {
        List<ResolveInfo> appsToRemove = new ArrayList<>();

        for (ResolveInfo resolveInfo : systemApps) {
            for (String appName : systemAppsFilter) {
                if (resolveInfo.loadLabel(packageManager).toString().equalsIgnoreCase(appName)) {
                    appsToRemove.add(resolveInfo);
                }
            }
        }
        systemApps.removeAll(appsToRemove);
        return systemApps;
    }


    /**
     * Retrieve the black listed app based on the existing white listed apps list
     *
     * @param whitelistedApps to iterate
     * @return black listed apps
     */
    public ArrayList<String> getBlackListedApps(ArrayList<String> whitelistedApps) {

        ArrayList<String> blackListedApps = new ArrayList<>();
        List<ResolveInfo> filteredSystemApps = getFilteredSystemApps(getInstalledSystemApps());

        // get all system apps package name listed apps from the system
        for (ResolveInfo resolveInfo : filteredSystemApps) {
            String app = resolveInfo.activityInfo.packageName;
            blackListedApps.add(app);
        }
        // remove white listed apps
        blackListedApps.removeAll(whitelistedApps);

        return blackListedApps;

    }
}
