package com.example.offline.backend.appusage;

import android.graphics.drawable.Drawable;

public class Statistics implements Comparable<Statistics> {

    public final String appName;
    public long usage;

    public final Drawable icon;

    public Statistics(String appName, long usage, Drawable icon) {
        this.appName = appName;
        this.usage = usage;
        this.icon = icon;
    }

    public long getUsage() {
        return usage;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    @Override
    public int compareTo(Statistics s) {
        return Long.compare(getUsage(), s.getUsage());
    }
}
