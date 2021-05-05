package com.example.offline.backend.week;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.offline.backend.day.Day;

import java.time.LocalDate;
import java.util.ArrayList;

public class Week implements Parcelable {

    private LocalDate startDate;
    private LocalDate endDate;
    private ArrayList<Day> daysInWeek;

    public Week(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.daysInWeek = new ArrayList<>();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getTuesday() {
        return startDate.plusDays(1);
    }

    public LocalDate getWednesday() {
        return startDate.plusDays(2);
    }

    public LocalDate getThursday() {
        return startDate.plusDays(3);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ArrayList<Day> getDaysInWeek() {
        return this.daysInWeek;
    }

    public void setDaysInWeek(ArrayList<Day> daysInWeek) {
        this.daysInWeek = daysInWeek;
    }

    public void addDay(Day day) {
        this.daysInWeek.add(day);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @Override
    public String toString() {
        return "Week{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", daysInWeek=" + daysInWeek +
                '}';
    }
}
