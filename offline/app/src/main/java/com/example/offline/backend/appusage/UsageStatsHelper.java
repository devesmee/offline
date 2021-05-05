package com.example.offline.backend.appusage;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.offline.backend.apps.SystemApp;
import com.example.offline.backend.apps.SystemManager;
import com.example.offline.backend.day.CampusTime;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class UsageStatsHelper {
    SystemManager systemManager;
    Context context;
    ArrayList<Statistics> statistics = new ArrayList<>();

    private static final String CONTACTS_APP = "Contacts";
    private static final String SETTINGS_APP = "Settings";
    private static final String VOICE_SEARCH_APP = "Voice Search";
    private static final String OFFLINE = "Offline";
    private static final String PIXEL_LAUNCHER = "Pixel Launcher";
    private static final String ANDROID_SYSTEM = "Android System";
    private static final String PERMISSION_CONTROLLER = "Permission Controller";
    private final List<String> systemAppsFilter = Arrays.asList(CONTACTS_APP, SETTINGS_APP, VOICE_SEARCH_APP, OFFLINE, PIXEL_LAUNCHER, ANDROID_SYSTEM, PERMISSION_CONTROLLER);

    public UsageStatsHelper(Context context) {
        this.context = context;
        systemManager = new SystemManager(context);
    }

    public ArrayList<Statistics> getUsageOfThisWeek() {
        ArrayList<Day> days = DayManager.getInstance(context).getDays();
        ArrayList<Statistics> weeklyUsageStats = new ArrayList<>();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        int amountOfWeekdaysThisWeek = 0;

        for (Day day : days) {
            int dayOfWeek = DayOfWeek.from(LocalDate.parse(day.getDate(), dtf)).getValue();
            if (dayOfWeek != DayOfWeek.SATURDAY.getValue() && dayOfWeek != DayOfWeek.SUNDAY.getValue()) {
                amountOfWeekdaysThisWeek = dayOfWeek;
                break;
            }
        }

        ArrayList<Day> daysThisWeek = new ArrayList<>();
        if (amountOfWeekdaysThisWeek < days.size()) {
            daysThisWeek = new ArrayList<Day>(days.subList(days.size() - amountOfWeekdaysThisWeek, days.size()));
        } else {
            daysThisWeek = days;
        }

        for (Day day : daysThisWeek) {
            ArrayList<Statistics> dayUsageStats = getUsageOfDay(day);

            for (Statistics temp : dayUsageStats) {
                boolean isUsageUpdated = false;

                for (Statistics actual : weeklyUsageStats) {
                    if (temp.appName.equals(actual.appName)) {
                        actual.usage += temp.usage;
                        isUsageUpdated = true;
                        break;
                    }
                }

                if (!isUsageUpdated) {
                    weeklyUsageStats.add(temp);
                }
            }
        }

        return weeklyUsageStats;
    }


    /**
     * @param day which day you want to receive app usage from.
     */
    public ArrayList<Statistics> getUsageOfDay(Day day) {
        ArrayList<Statistics> dayUsageStats = new ArrayList<>();

        for (CampusTime event : day.getCampusTimes()) {
            ArrayList<Statistics> result = getUsageStatsForTimePeriod(event.getEnterTime(), event.getLeaveTime());

            for (Statistics temp : result) {
                // check if app has at least one sec usage
                if (temp.getUsage() > 999) {
                    Log.e("Result", temp.getUsage() + "");
                    boolean isUsageUpdated = false;

                    for (Statistics actual : dayUsageStats) {
                        if (temp.appName.equals(actual.appName)) {
                            actual.usage += temp.usage;
                            isUsageUpdated = true;
                            break;
                        }
                    }

                    if (!isUsageUpdated) {
                        dayUsageStats.add(temp);
                    }
                }

            }
        }
        return dayUsageStats;
    }

    /**
     * Gather all the AppUsageEvents from the UsageStatsManager for selected time period
     * <p>
     * Group per package (app)
     * Remove entries from not relevant apps
     * Calculate time for each app
     *
     * @return list with statistics
     */
    public ArrayList<Statistics> getUsageStatsForTimePeriod(long start, long end) {
        List<SystemApp> installedApps = systemManager.getSystemApps();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        ArrayList<Statistics> results = new ArrayList<>();
        Map<String, List<UsageEvents.Event>> sortedEvents = new HashMap<>();
        UsageEvents usageEvents;

        //Get all events for time period
        //Check if session has end time otherwise use current time
        if (end == 0) {
            usageEvents = usageStatsManager.queryEvents(start, System.currentTimeMillis());
        } else {
            usageEvents = usageStatsManager.queryEvents(start, end);
        }

        // Group all events to events per package
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event e = new UsageEvents.Event();
            usageEvents.getNextEvent(e);

            List<UsageEvents.Event> packageEvents = sortedEvents.get(e.getPackageName());
            if (packageEvents == null) packageEvents = new ArrayList<>();
            packageEvents.add(e);
            sortedEvents.put(e.getPackageName(), packageEvents);
        }

        // Remove entries that are not installed apps
        Iterator<String> it = sortedEvents.keySet().iterator();
        while (it.hasNext()) {
            String packageName = it.next();
            boolean inList = false;
            for (SystemApp installedApp : installedApps) {
                if (installedApp.getPackageName().equals(packageName)) {
                    inList = true;
                    break;
                }
            }
            if (!inList) {
                sortedEvents.remove(it);
            }
        }

        // Calculate total time for each app
        sortedEvents.forEach((packageName, sortedPackageEvents) -> {
            // get package meta
            Drawable icon = null;
            String name = "";
            try {
                ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);
                icon = context.getPackageManager().getApplicationIcon(app);
                name = (String) context.getPackageManager().getApplicationLabel(app);
            } catch (PackageManager.NameNotFoundException ex) {
                ex.printStackTrace();
            }
            long startTime = 0;
            long endTime = 0;
            long totalTime = 0;

            for (UsageEvents.Event event : sortedPackageEvents) {
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    startTime = event.getTimeStamp();
                } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    endTime = event.getTimeStamp();
                }

                // If there's an end time with no start time, this might mean that
                //  the app was started before entering the campus,
                //  so take the time entered the campus as the start time
                if (startTime == 0 && endTime != 0) {
                    startTime = start;
                }

                // Session is defined, add session time to totalTime
                if (startTime != 0 && endTime != 0) {
                    totalTime += (endTime - startTime);

                    // Reset start/end times
                    startTime = 0;
                    endTime = 0;
                }
            }

            // If there is a start time without an end time, this might mean that
            //  the app was used past the leaveCampusEvent, so take (leaveCampus - 1 second)
            //  as the end time
            //  Take the system time if there is no leaveCampus time.
            if (startTime != 0 && endTime == 0) {
                if (end == 0) {
                    totalTime += (System.currentTimeMillis() - startTime - 1000);
                } else {
                    totalTime += (end - startTime - 1000);
                }
            }

            boolean toBeAdded = true;
            for (String appName : systemAppsFilter) {
                if (name.equalsIgnoreCase(appName)) {
                    toBeAdded = false;
                    break;
                }
            }
            if (toBeAdded) {
                results.add(new Statistics(name, totalTime, icon));
            }
        });
        return results;
    }

    public ArrayList<Statistics> getTop5Apps(ArrayList<Statistics> stats) {
        Collections.sort(stats);
        if (stats.size() > 5) {
            return new ArrayList<>(stats.subList(stats.size() - 5, stats.size()));
        }
        return stats;
    }

    public ArrayList<Statistics> getTop5AppsByWeek(ArrayList<Day> days) {
        ArrayList<Statistics> allUsage = new ArrayList<>();

        for (Day day : days) {
            ArrayList<Statistics> dayUsageStats = getUsageOfDay(day);

            for (Statistics temp : dayUsageStats) {
                boolean isUsageUpdated = false;

                for (Statistics actual : allUsage) {
                    if (temp.appName.equals(actual.appName)) {
                        actual.usage += temp.usage;
                        isUsageUpdated = true;
                        break;
                    }
                }

                if (!isUsageUpdated) {
                    allUsage.add(temp);
                }
            }
        }
        return getTop5Apps(allUsage);
    }

    public ArrayList<Statistics> getAllUsedAppsByWeek(ArrayList<Day> days) {
        ArrayList<Statistics> allUsage = new ArrayList<>();

        for (Day day : days) {
            ArrayList<Statistics> dayUsageStats = getUsageOfDay(day);

            for (Statistics temp : dayUsageStats) {
                boolean isUsageUpdated = false;

                for (Statistics actual : allUsage) {
                    if (temp.appName.equals(actual.appName)) {
                        actual.usage += temp.usage;
                        isUsageUpdated = true;
                        break;
                    }
                }

                if (!isUsageUpdated) {
                    allUsage.add(temp);
                }
            }
        }
        return allUsage;
    }


    public String convertTimeToText(long TimeInForeground) {

        int hours = (int) ((TimeInForeground / (1000 * 60 * 60)) % 24);

        int minutes = (int) ((TimeInForeground / (1000 * 60)) % 60);

        int seconds = (int) (TimeInForeground / 1000) % 60;

        String usage;
        if (hours == 0) {
            usage = minutes + "min " + seconds + " sec";
        } else if (minutes == 0) {
            usage = hours + "h";
        } else {
            usage = hours + "h" + " " + minutes + "min";
        }

        return usage;
    }

    public long getTotalScreenTime() {
        long total = 0;
        for (Statistics statistics :
                statistics) {
            total += statistics.usage;
        }
        return total;
    }

    public long utcToLocal(long utcTime) {
        try {
            Time timeFormat = new Time();
            timeFormat.set(utcTime + TimeZone.getDefault().getOffset(utcTime));
            return timeFormat.toMillis(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return utcTime;
    }

    /**
     * Check if user has already given permissions for the user stats service
     *
     * @param context
     * @return true | false
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean isUsageStatsServiceEnabled(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * Ask for usage stats permission to the user if they are not already granted
     *
     * @param context
     */
    public static void askUsageStatsPermission(Context context) {

        if (!isUsageStatsServiceEnabled(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("In order to be able to track your screen time usage you need to turn on the Usage Access permission.")
                    .setTitle("Usage Access permission");
            builder.setPositiveButton("Grant permission", (dialog, id) -> context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
            builder.setNegativeButton("Cancel", (dialog, id) -> {
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
