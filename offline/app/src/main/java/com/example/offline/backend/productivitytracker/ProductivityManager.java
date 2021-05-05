package com.example.offline.backend.productivitytracker;

import android.content.Context;

import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.day.ProductivityFeeling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProductivityManager {
    private static ProductivityManager productivityManagerInstance;
    private DayManager dayManager;
    private ArrayList<Day> days;
    private ProductivityFeeling average;

    private ProductivityManager(Context context) {
        this.dayManager = DayManager.getInstance(context);
    }

    public static ProductivityManager getInstance(Context context) {
        if (productivityManagerInstance == null) {
            productivityManagerInstance = new ProductivityManager(context);
        }
        return productivityManagerInstance;
    }

    /**
     * private method, only invoked by getAverage() now
     *
     * @param i is the number of week in the year
     * @return all days in week
     */
    private ArrayList<Day> getCurrentWeekMoods(int i) {
        ArrayList<Day> weekDays = new ArrayList<>();
        this.days = dayManager.getDays();
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        for (Day day : this.days) {
            calendar.setTime(day.getToday());
            if (calendar.get(Calendar.WEEK_OF_YEAR) == i) {
                weekDays.add(day);
            }
        }
        return weekDays;
    }

    /**
     * @return ProductivityFeeling
     */
    public ProductivityFeeling getAverage() {
        Date date = new Date();
        int thisWeek;
        int i = 0;
        double sum = 0;
        int result;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        thisWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        days = getCurrentWeekMoods(thisWeek);
        // to only include those days where productivity feeling is HAPPY, NEUTRAL or SAD
        int daysWithProductivityFeeling = 0;

        if (!days.isEmpty()) {
            int index = 0;
            for (Day day : days) {
                if (day.getProductivityFeeling() != ProductivityFeeling.NONE) {
                    this.average = day.getProductivityFeeling();
                    switch (this.average) {
                        case HAPPY:
                            i = 3;
                            break;
                        case NEUTRAL:
                            i = 2;
                            break;
                        case SAD:
                            i = 0;
                            break;
                    }
                    daysWithProductivityFeeling += 1;
                    sum += i;
                    result = (int) Math.ceil(sum / daysWithProductivityFeeling);
                    switch (result) {
                        case 1:
                            this.average = ProductivityFeeling.SAD;
                            break;
                        case 2:
                            this.average = ProductivityFeeling.NEUTRAL;
                            break;
                        case 3:
                            this.average = ProductivityFeeling.HAPPY;
                            break;
                    }
                    index++;
                } else {
                    if (index < 1) {// to ensure this average is only set on first start up
                        this.average = ProductivityFeeling.NONE;
                    }
                    index++;
                }
            }
        } else {
            this.average = ProductivityFeeling.NONE;
        }
        return this.average;
    }

}