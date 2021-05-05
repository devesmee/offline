package com.example.offline.backend.apps;

import android.graphics.drawable.Drawable;

public class SystemApp {

    private String appName;
    private Drawable appIcon;
    private Boolean selected;
    private String packageName;

    public SystemApp(String appName, Drawable appIcon, String packageName) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.packageName = packageName;
        this.selected = false;
    }

    /**
     * @return app name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @return app package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return return app icon
     */
    public Drawable getAppIcon() {
        return appIcon;
    }

    /**
     * @return return app selection state
     */
    public Boolean isSelected() {
        return this.selected;
    }

    /**
     * Set selection state of the app
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
