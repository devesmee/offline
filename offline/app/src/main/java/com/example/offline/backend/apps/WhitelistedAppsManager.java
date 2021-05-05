package com.example.offline.backend.apps;

import android.content.Context;

import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;

import java.util.ArrayList;

public class WhitelistedAppsManager {

    private Storage storage;
    private Context context;
    private static WhitelistedAppsManager whiteListedAppsManagerInstance;
    public ArrayList<String> whitelistedApps;

    public WhitelistedAppsManager(Context context) {
        this.context = context;
        this.storage = new Storage(context);
        this.whitelistedApps = getWhitelistedApps();
    }

    /**
     * Get singleton instance of the WhitelistedAppsManager
     *
     * @param context
     * @return singleton instance
     */
    public static WhitelistedAppsManager getInstance(Context context) {
        if (whiteListedAppsManagerInstance == null) {
            whiteListedAppsManagerInstance = new WhitelistedAppsManager(context);
        }
        return whiteListedAppsManagerInstance;
    }

    /**
     * Save the package names of the apps that are marked as whitelisted
     *
     * @param whitelistedApps to be saved
     */
    public void saveWhitelistedApps(ArrayList<String> whitelistedApps) {
        storage.putListString(Key.WHITELISTED_APPS, whitelistedApps);
        this.whitelistedApps = getWhitelistedApps();
    }

    /**
     * @return list of the package names of the apps that were marked as whitelisted
     */
    public ArrayList<String> getWhitelistedApps() {
        return storage.getListString(Key.WHITELISTED_APPS);
    }

}
