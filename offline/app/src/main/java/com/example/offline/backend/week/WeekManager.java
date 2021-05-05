package com.example.offline.backend.week;

import android.content.Context;

import com.example.offline.backend.Util;
import com.example.offline.backend.calendar.CalendarUtil;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeekManager {

    private ArrayList<Week> fontysWeeks;
    private DayManager dayManager;
    private Context mContext;
    private static WeekManager weekManagerInstance;

    public WeekManager(Context context) {
        this.mContext = context;
        this.fontysWeeks = new ArrayList<>();
        this.fontysWeeks.add(new Week(LocalDate.of(2021, 1, 4), LocalDate.of(2021, 1, 8)));
        this.fontysWeeks.add(new Week(LocalDate.of(2021, 1, 11), LocalDate.of(2021, 1, 15)));
        this.fontysWeeks.add(new Week(LocalDate.of(2021, 1, 18), LocalDate.of(2021, 1, 22)));
        this.fontysWeeks.add(new Week(LocalDate.of(2021, 1, 25), LocalDate.of(2021, 1, 29)));
    }

    public ArrayList<Week> getFontysWeeks() {
        return fontysWeeks;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }

    public static WeekManager getInstance(Context context) {
        if (weekManagerInstance == null) {
            weekManagerInstance = new WeekManager(context);
        }
        return weekManagerInstance;
    }

    public void setDayObjectsInWeek(Week week) {
        dayManager = DayManager.getInstance(mContext);
        ArrayList<Day> currentDaysArrayToSetToWeek = new ArrayList<Day>();
        currentDaysArrayToSetToWeek.add(new Day(Util.getDateAsString(week.getStartDate())));
        currentDaysArrayToSetToWeek.add(new Day(Util.getDateAsString(week.getTuesday())));
        currentDaysArrayToSetToWeek.add(new Day(Util.getDateAsString(week.getWednesday())));
        currentDaysArrayToSetToWeek.add(new Day(Util.getDateAsString(week.getThursday())));
        currentDaysArrayToSetToWeek.add(new Day(Util.getDateAsString(week.getEndDate())));
        int position = 0;
        for (Day day : dayManager.getDays()) {
            if (isDateWithinBounds(week.getStartDate(), week.getEndDate(), CalendarUtil.getLocalDate(day))) {
                position = (CalendarUtil.getLocalDate(day).getDayOfWeek().getValue() - 1);
                for (int i = 0; i <= 4; i++) {
                    if (i == position) {
                        currentDaysArrayToSetToWeek.set(position, day);
                        break;
                    } else {
                    }
                }
            }
            week.setDaysInWeek(currentDaysArrayToSetToWeek);
        }
    }

    public Day getDayObjectOfWeek(Week week, int position) {
        return week.getDaysInWeek().get(position);
    }

    public Boolean isDateWithinBounds(LocalDate startDate, LocalDate endDate, LocalDate dateToCheck) {
        return (dateToCheck.compareTo(startDate) >= 0 && dateToCheck.compareTo(endDate) <= 0);//dateToCheck.after(startDate) && dateToCheck.before(endDate);
    }
}
