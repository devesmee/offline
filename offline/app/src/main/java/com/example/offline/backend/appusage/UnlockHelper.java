package com.example.offline.backend.appusage;

import android.content.Context;
import android.util.Pair;

import com.example.offline.backend.calendar.CalendarUtil;
import com.example.offline.backend.day.CampusTime;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.week.Week;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UnlockHelper {
    Context context;

    public UnlockHelper(Context context) {
        this.context = context;
    }


    public ArrayList<Pair<String, Integer>> getWeeklyUnlocks() {
        ArrayList<Pair<String, Integer>> result = new ArrayList<>();
        ArrayList<Day> days = DayManager.getInstance(context).getDays();

        ArrayList<Day> sublistDays;
        if (days.size() > 7) {
            sublistDays = new ArrayList<Day>(days.subList(days.size() - 7, days.size()));
        } else {
            sublistDays = days;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Day day : sublistDays) {
            DayOfWeek dayOfWeek = DayOfWeek.from(LocalDate.parse(day.getDate(), dtf));
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                result.add(new Pair<>(dayOfWeek.toString(), day.getNumberOfUnlocks()));
            }
        }
        return result;
    }

    public ArrayList<Pair<String, Integer>> getWeeklyUnlocksDynamically(Week week) {
        ArrayList<Pair<String, Integer>> result = new ArrayList<>();
        ArrayList<Day> days = week.getDaysInWeek();

        for (Day day : days) {
            DayOfWeek dayOfWeek = CalendarUtil.getLocalDate(day).getDayOfWeek();
            result.add(new Pair<>(dayOfWeek.toString(), day.getNumberOfUnlocks()));
        }
        return result;
    }

    /**
     * Calculates the total time on campus for set days.
     *
     * @param days days to calculate the time on campus for
     * @return total time on campus
     */
    public long getTimeSpendOnCampus(ArrayList<Day> days) {
        long result = 0L;
        for (Day day : days) {
            for (CampusTime campusTime : day.getCampusTimes()) {
                if (campusTime.getLeaveTime() != 0) {
                    result += (campusTime.getLeaveTime() - campusTime.getEnterTime());
                } else {
                    result += (System.currentTimeMillis() - campusTime.getEnterTime());
                }
            }
        }
        return result;
    }
}
