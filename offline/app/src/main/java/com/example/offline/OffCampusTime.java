package com.example.offline;

public class OffCampusTime {

    private static long startTime;
    private static long endTime;
    private static long total;
    private static boolean isOutOfCampus;

    public OffCampusTime() {
        OffCampusTime.isOutOfCampus = false;
    }

    public static void setStartTime(long startTime) {
        OffCampusTime.startTime = startTime;
        OffCampusTime.isOutOfCampus = true;
    }

    public static void setEndTime(long endTime) {
        OffCampusTime.endTime = endTime;
        OffCampusTime.total += endTime - startTime;
        OffCampusTime.isOutOfCampus = false;
    }

    public static long getTotal() {
        return OffCampusTime.total;
    }
}
