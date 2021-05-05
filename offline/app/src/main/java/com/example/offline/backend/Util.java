package com.example.offline.backend;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Util {

    /**
     * Round a double to a preferred decimal precision
     *
     * @param value     to be rounded
     * @param precision to be set
     * @return rounded double
     */
    public static double roundDouble(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.floor(value * scale) / scale;
    }

    public static double millisecondsToMinutes(double millis) {
        double minutes = millis / 60000;
        return roundDouble(minutes, 1);
    }

    public static double millisecondToHours(double millis) {
        double hours = millisecondsToMinutes(millis) / 60;
        return roundDouble(hours, 1);
    }

    public static String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.now(); // Gets the current date
        return date.format(formatter);
    }

    public static String getDateAsString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(formatter);
    }

    public static Date getToday() {
        Date date = new Date();
        return date;
    }
}
