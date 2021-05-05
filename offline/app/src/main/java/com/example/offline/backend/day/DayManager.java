package com.example.offline.backend.day;

import android.content.Context;
import android.util.Log;

import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DayManager {
    private Storage storage;
    private ArrayList<Day> days;
    private static DayManager dayManagerInstance;

    public DayManager(Context context) {
        this.storage = new Storage(context);
        this.days = getDays();
    }

    public DayManager() {

    }

    /**
     * Get singleton instance of the DayManager
     *
     * @param context
     * @return singleton instance
     */
    public static DayManager getInstance(Context context) {
        if (dayManagerInstance == null) {
            dayManagerInstance = new DayManager(context);
        }
        dayManagerInstance.days = dayManagerInstance.getDays();
        return dayManagerInstance;
    }

    /**
     * Returns the list of days
     *
     * @return ArrayList<Day>
     */
    public ArrayList<Day> getDays() {
        this.days = new ArrayList<>();
        ArrayList<Object> dayObjects = storage.getListObject(Key.DAYS, Day.class);
        for (Object object : dayObjects) {
            this.days.add((Day) object);
        }
        return this.days;
    }

    public Day getCurrentDay() {
        Day currentDay = null;
        if (days.size() > 0) {
            currentDay = this.days.get(this.days.size() - 1);
        }
        return currentDay;
    }

    public void addNewDay(Day newDay) {
        this.days.add(newDay);
        storage.saveListObject(Key.DAYS, this.days);
    }

    public void saveScreenUnlock() {
        this.days.get(this.days.size() - 1).addOneUnlock(storage);
        storage.saveListObject(Key.DAYS, this.days);
    }

    public void saveEnterTime() {
        this.days.get(this.days.size() - 1).startCampusTime();
        storage.saveListObject(Key.DAYS, this.days);
    }

    public void saveLeaveTime(Long leaveTime) {
        this.days.get(this.days.size() - 1).endCampusTime(leaveTime);
        storage.saveListObject(Key.DAYS, this.days);
    }

    public void saveOnScreenTime(Long onScreenTime) {
        this.days.get(this.days.size() - 1).getCampusTimes().get(this.days.get(this.days.size() - 1).getCampusTimes().size() - 1).setOnScreenTime(onScreenTime);
        storage.saveListObject(Key.DAYS, this.days);
    }

    public void setGoalPercentage(Integer goalPercentage) {
        this.days.get(this.days.size() - 1).getGoal().setPercentage(goalPercentage);
        storage.saveListObject(Key.DAYS, this.days);
    }

    public void endDay(double goalProgress, Long offScreenTime, Long timeOnCampus) {
        this.days.get(this.days.size() - 1).setEnded(true);
        this.getCurrentDay().setGoalProgress(goalProgress);
        this.getCurrentDay().setOffScreenTime(offScreenTime);
        this.getCurrentDay().setTimeOnCampus(timeOnCampus);
        storage.saveListObject(Key.DAYS, this.days);
    }

    public void saveProductivityFeeling(ProductivityFeeling productivityFeeling) {
        this.days.get(this.days.size() - 1).setProductivityFeeling(productivityFeeling);
        storage.saveListObject(Key.DAYS, this.days);
    }

    public void saveAwaitingProductivityFeeling(Boolean awaitingProductivityFeeling) {
        this.days.get(this.days.size() - 1).setAwaitingProductivityInput(awaitingProductivityFeeling);
        storage.saveListObject(Key.DAYS, this.days);
    }
}
