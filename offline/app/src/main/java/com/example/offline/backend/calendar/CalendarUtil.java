package com.example.offline.backend.calendar;

import com.example.offline.backend.day.Day;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarUtil {

    /**
     * Parse date String to LocalDate object of Day object.
     *
     * @param day object of type Day
     * @return LocalDate object.
     */
    public static LocalDate getLocalDate(Day day) {
        String dateString = day.getDate();
        DateTimeFormatter stringFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(dateString, stringFormatter);

        return date;
    }

    public static String getStringDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dateRequiredString = date.format(formatter);

        return dateRequiredString;
    }

    /**
     * Get day of the month from date of Day object.
     *
     * @param day object
     * @return day of the month.
     */
    public static int getDayOfMonth(Day day) {
        LocalDate date = getLocalDate(day);

        return date.getDayOfMonth();
    }

    /**
     * Get month from date of Day object.
     *
     * @param day object
     * @return month.
     */
    public static Month getMonth(Day day) {
        LocalDate date = getLocalDate(day);

        return date.getMonth();
    }

    /**
     * Get year from date of Day object.
     *
     * @param day object
     * @return year.
     */
    public static int getYear(Day day) {
        LocalDate date = getLocalDate(day);

        return date.getYear();
    }

    /**
     * Get array of shifted calendar weekdays in a specific month to match their day of week.
     *
     * @param month
     * @return array of shifted calendar weekdays.
     */
    public static ArrayList<Integer> getDaysInMonth(CalendarMonth month) {
        ArrayList<Integer> daysArray;
        daysArray = getWeekDays(month);
        shiftDays(daysArray, month);
        return daysArray;
    }

    /**
     * Determine if year is a leap year or not.
     *
     * @param year
     * @return
     */
    private static boolean isLeapYear(int year) {
        if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get only weekdays of a specific month.
     *
     * @param month
     * @return array of weekdays.
     */
    private static ArrayList<Integer> getWeekDays(CalendarMonth month) {
        Month selectedMonth = month.getMonth();
        int lengthOfSelectedMonth = selectedMonth.length(isLeapYear(month.getMonthYear()));
        ArrayList<Integer> daysArray = new ArrayList<Integer>();
        for (int i = 1; i <= lengthOfSelectedMonth; i++) {
            // Get day of date.
            LocalDate date = LocalDate.of(month.getMonthYear(), selectedMonth, i);
            DayOfWeek day = date.getDayOfWeek();
            // Check if it's a weekend.
            if (day.getValue() != 6 && day.getValue() != 7) {
                daysArray.add(i);
            }
        }
        return daysArray;
    }

    /**
     * Get shifted weekdays of a specific month.
     *
     * @param daysArray of weekdays
     * @param month     specific month
     * @return array of shifted weekdays.
     */
    private static ArrayList<Integer> shiftDays(ArrayList<Integer> daysArray, CalendarMonth month) {
        // Get first weekday of the month.
        LocalDate firstDayDate = LocalDate.of(month.getMonthYear(), month.getMonth(), (int) daysArray.get(0));
        DayOfWeek firstDay = firstDayDate.getDayOfWeek();
        int firstDayInt = firstDay.getValue();
        // Shift the day to start at the correct day.
        for (int i = 0; i < firstDayInt - 1; i++) {
            daysArray.add(0, 0);
        }
        return daysArray;
    }
}
