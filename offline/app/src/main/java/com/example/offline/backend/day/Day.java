package com.example.offline.backend.day;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.offline.backend.Util;
import com.example.offline.backend.goal.Goal;
import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Day implements Parcelable {
    private String date;
    private Date today;
    private Goal goal;
    private List<CampusTime> campusTimes;
    private ProductivityFeeling productivityFeeling;
    private int numberOfUnlocks;
    private boolean isEnded;
    private Boolean awaitingProductivityInput;

    public Day(Goal goal) {
        this.date = Util.getCurrentDate();
        this.today = Util.getToday();
        this.goal = goal;
        this.campusTimes = new ArrayList<>();
        this.productivityFeeling = ProductivityFeeling.NONE;
        this.numberOfUnlocks = 0;
        this.isEnded = false;
        this.awaitingProductivityInput = false;
    }

    public Day(String date) {
        this.date = date;
        this.goal = new Goal();
        this.goal.setProgress(0);
    }

    public String getDate() {
        return date;
    }

    public Date getToday() {
        return today;
    }

    public Goal getGoal() {
        return goal;
    }

    @Override
    public String toString() {
        return "Day{" +
                "date='" + date + '\'' +
                ", goal=" + goal +
                ", campusTimes=" + campusTimes +
                ", productivityFeeling=" + productivityFeeling +
                ", numberOfUnlocks=" + numberOfUnlocks +
                ", isEnded=" + isEnded +
                '}';
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public List<CampusTime> getCampusTimes() {
        return campusTimes;
    }

    public ProductivityFeeling getProductivityFeeling() {
        return productivityFeeling;
    }

    public void setProductivityFeeling(ProductivityFeeling productivityFeeling) {
        this.productivityFeeling = productivityFeeling;
    }

    public Boolean getAwaitingProductivityInput() {
        return this.awaitingProductivityInput;
    }

    public void setAwaitingProductivityInput(Boolean awaitingProductivityInput) {
        this.awaitingProductivityInput = awaitingProductivityInput;
    }

    public int getNumberOfUnlocks() {
        return numberOfUnlocks;
    }

    public void addOneUnlock(Storage storage) {
        if (storage.getBoolean(Key.IS_TRACKING)) {
            numberOfUnlocks += 1;
        }
    }

    public void startCampusTime() {
        CampusTime newCampusTime = new CampusTime();
        this.campusTimes.add(newCampusTime);
    }

    public void endCampusTime(Long leaveTime) {
        this.campusTimes.get(this.campusTimes.size() - 1).setLeaveTime(leaveTime);
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }

    public void setGoalProgress(double goalProgress) {
        this.goal.setProgress(goalProgress);
    }

    public void setOffScreenTime(Long offScreenTime) {
        this.goal.setTotalOffScreenTime(offScreenTime);
    }

    public void setTimeOnCampus(Long campusTime) {
        this.goal.setTimeInCampus(campusTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
