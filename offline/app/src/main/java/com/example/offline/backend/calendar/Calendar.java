package com.example.offline.backend.calendar;

import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.util.ArrayList;

public class Calendar {
    DayManager dayManager;
    ArrayList<Day> allDays;
    ArrayList<CalendarMonth> allMonths;

    public Calendar(DayManager dayManager) {
        this.dayManager = dayManager;
    }

    /**
     * @return all the months with data.
     */
    public ArrayList<CalendarMonth> getAllMonths() throws ParseException {
        // Group the days based on the months.
        allMonths = new ArrayList<>();
        allDays = dayManager.getDays();
        // Make an array/list of day objects with the same month.
        ArrayList<Day> daysInMonth = new ArrayList<>();
        // Get first day object.
        Day currentDayObject = allDays.get(0);
        CalendarMonth lastAddedMonth = null;
        for (Day day : allDays) {
            if (day == allDays.get(0)) {
                // First day object so allMonths array is still empty.
                // Create CalendarMonth objects of the current month and one month after.
                LocalDate currentDate = LocalDate.of(CalendarUtil.getYear(day), CalendarUtil.getMonth(day), CalendarUtil.getDayOfMonth(day));
                TemporalAdjuster oneMonthNext = temporal -> {
                    LocalDate date = LocalDate.from(temporal).plusMonths(1);
                    return temporal.with(date);
                };
                LocalDate nextMonthDate = currentDate.with(oneMonthNext);
                daysInMonth.add(day);
                allMonths.add(new CalendarMonth(CalendarUtil.getMonth(day), CalendarUtil.getYear(day), daysInMonth));
                allMonths.add(new CalendarMonth(nextMonthDate.getMonth(), nextMonthDate.getYear(), allDays));
                lastAddedMonth = new CalendarMonth(nextMonthDate.getMonth(), nextMonthDate.getYear(), allDays);
            } else if (CalendarUtil.getMonth(day) == CalendarUtil.getMonth(currentDayObject) && CalendarUtil.getYear(day) == CalendarUtil.getYear(currentDayObject)) {
                // Not the first day object.
                // Current day object has the same month and the same year as an existing object in allMonths.
                daysInMonth.add(day);
            } else if (CalendarUtil.getMonth(day) != CalendarUtil.getMonth(currentDayObject)) {
                TemporalAdjuster oneMonthPrevious = temporal -> {
                    LocalDate date = LocalDate.from(temporal).minusMonths(1);
                    return temporal.with(date);
                };
                LocalDate previousMonthDate = CalendarUtil.getLocalDate(day).with(oneMonthPrevious);
                // something goes wrong here
                if (!lastAddedMonth.month.name().equals(CalendarUtil.getMonth(day).toString())) {
                    if (!(previousMonthDate.getMonth().equals(lastAddedMonth.month))) {
                        allMonths.add(new CalendarMonth(previousMonthDate.getMonth(), previousMonthDate.getYear(), daysInMonth));
                        allMonths.add(new CalendarMonth(CalendarUtil.getMonth(day), CalendarUtil.getYear(day), daysInMonth));
                        lastAddedMonth = new CalendarMonth(previousMonthDate.getMonth(), previousMonthDate.getYear(), daysInMonth);
                    } else {
                        daysInMonth.clear();
                        daysInMonth.add(day);
                        allMonths.add(new CalendarMonth(CalendarUtil.getMonth(day), CalendarUtil.getYear(day), daysInMonth));
                        lastAddedMonth = new CalendarMonth(CalendarUtil.getMonth(day), CalendarUtil.getYear(day), daysInMonth);
                    }
                }

                // Current day object's month and year does not exist in allMonths.
                currentDayObject = day;

            } else {
            }
        }

        // Return all months.
        return allMonths;
    }
}
