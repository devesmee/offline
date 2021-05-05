package com.example.offline.backend.calendar;

import com.example.offline.backend.day.Day;

import java.time.Month;
import java.util.ArrayList;

public class CalendarMonth {
    Month month;
    String monthName;
    Integer monthYear;
    ArrayList<Day> monthDays;

    public CalendarMonth(Month month, Integer monthYear, ArrayList<Day> monthDays) {
        // Manipulate month string to only have first letter uppercase.
        String monthString = month.toString();
        monthString = monthString.substring(0, 1).toUpperCase() + monthString.substring(1).toLowerCase();
        this.monthName = monthString;
        this.month = month;
        this.monthYear = monthYear;
        this.monthDays = monthDays;
    }

    public Month getMonth() {
        return this.month;
    }

    public String getMonthName() {
        return this.monthName;
    }

    public int getMonthYear() {
        return this.monthYear;
    }

    public ArrayList<Day> getDaysInMonth() {
        return this.monthDays;
    }
}
