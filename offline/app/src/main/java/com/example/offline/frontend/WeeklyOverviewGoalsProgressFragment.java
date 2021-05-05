package com.example.offline.frontend;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.offline.R;
import com.example.offline.WeeklyProgressCirclesAdapter;
import com.example.offline.backend.day.CampusTime;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.week.Week;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class WeeklyOverviewGoalsProgressFragment extends Fragment {

    private Week week;
    private Long percentageOffScreen = 0L;

    // newInstance constructor for creating fragment with arguments
    public WeeklyOverviewGoalsProgressFragment newInstance(int page, String title, Week week) {
        WeeklyOverviewGoalsProgressFragment weeklyOverviewGoalsProgressFragment = new WeeklyOverviewGoalsProgressFragment();
        return weeklyOverviewGoalsProgressFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goal_progress_card_weekly_overview, container, false);

        week = getArguments().getParcelable("selectedWeek");

        GridView weeklyGoalsGridView = (GridView) rootView.findViewById(R.id.gvWeeklyGoalsOverview);

        WeeklyProgressCirclesAdapter weeklyProgressCirclesAdapter = new WeeklyProgressCirclesAdapter(getContext(), this.week.getDaysInWeek(), this.week); //getWeekdaysArrayOfCurrentWeekAsDates()); //
        weeklyGoalsGridView.setAdapter(weeklyProgressCirclesAdapter);

        LinearProgressIndicator weeklyOffScreenTimeProgressBar = rootView.findViewById(R.id.weeklyOffScreenTimeProgressBar);

        // Set the text for weekly time off screen out of the total time spent on campus
        TextView weeklyOffScrenTimeProgressText = (TextView) rootView.findViewById(R.id.weeklyOffScreenProgressTimeText);
        weeklyOffScrenTimeProgressText.setText(generateWeeklyOffScreenTimeProgressText());

        if(this.percentageOffScreen != null)
            weeklyOffScreenTimeProgressBar.setProgress(Math.toIntExact(this.percentageOffScreen));
        else
            weeklyOffScreenTimeProgressBar.setProgress(0);
        weeklyOffScreenTimeProgressBar.show();

        return rootView;
    }

    private ArrayList getWeekdaysArrayOfCurrentWeekAsDates(){

        ArrayList weekdays = new ArrayList();

        // Get calendar set to current date and time
        Calendar c = Calendar.getInstance();

        // Set the calendar to Monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        DateFormat df = new SimpleDateFormat("EEE dd/MM/yyyy");

        for (int i = 1; i < 6; i++) {
            weekdays.add(df.format(c.getTime()));
            System.out.println(df.format(c.getTime()));
            c.add(Calendar.DATE, 1);
        }

        return weekdays;
    }

    private String generateWeeklyOffScreenTimeProgressText() {
        Long totalSecondsOnCampus = 0L;
        Long totalSecondsOffScreen = 0L;
        String text = "";
        for(Day day: week.getDaysInWeek())
        {
            totalSecondsOnCampus += calculateTimeOnCampus(day);
            totalSecondsOffScreen += calculateTimeOffScreen(day);
        }

        if(totalSecondsOnCampus != 0) {
            this.percentageOffScreen = (totalSecondsOffScreen * 100) / totalSecondsOnCampus;

            Long minutesOnCampusCalculation = TimeUnit.MILLISECONDS.toMinutes(totalSecondsOnCampus);
            Long secondsOnCampusCalculation = TimeUnit.MILLISECONDS.toSeconds(totalSecondsOnCampus) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalSecondsOnCampus));

            Long minutesOffScreenCalculation = TimeUnit.MILLISECONDS.toMinutes(totalSecondsOffScreen);
            Long secondsOffScreenCalculation = TimeUnit.MILLISECONDS.toSeconds(totalSecondsOffScreen) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalSecondsOffScreen));

            String totalMinutesOffScreenFormattedValue = "";
            String totalSecondsOffScreenFormattedValue = "";

            String totalMinutesOnCampusFormattedValue = "";
            String totalSecondsOnCampusFormattedValue = "";

            if(minutesOffScreenCalculation != 0) {

                if(minutesOffScreenCalculation > 1) {
                    // Format the time as > 1 minutes
                    totalMinutesOffScreenFormattedValue = String.format("%d minutes", minutesOffScreenCalculation);
                } else {
                    // Format the time as 1 minute
                    totalMinutesOffScreenFormattedValue = String.format("%d minute", minutesOffScreenCalculation);
                }

                if(secondsOffScreenCalculation > 1 || secondsOffScreenCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d seconds", secondsOffScreenCalculation);
                } else {
                    // Format the time as 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d second", secondsOffScreenCalculation);
                }
            } else {

                if(secondsOffScreenCalculation > 1 || secondsOffScreenCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d seconds", secondsOffScreenCalculation);
                } else {
                    // Format the time as 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d second", secondsOffScreenCalculation);
                }
            }

            if(minutesOnCampusCalculation != 0) {

                if(minutesOnCampusCalculation > 1) {
                    // Format the time as > 1 minutes
                    totalMinutesOnCampusFormattedValue = String.format("%d minutes", minutesOnCampusCalculation);
                } else {
                    // Format the time as 1 minute
                    totalMinutesOnCampusFormattedValue = String.format("%d minute", minutesOnCampusCalculation);
                }

                if(secondsOnCampusCalculation > 1 || secondsOnCampusCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOnCampusFormattedValue = String.format("%d seconds", secondsOnCampusCalculation);
                } else {
                    // Format the time as 1 second
                    totalSecondsOnCampusFormattedValue = String.format("%d second", secondsOnCampusCalculation);
                }

            } else {

                if(secondsOnCampusCalculation > 1 || secondsOnCampusCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOnCampusFormattedValue = String.format("%d seconds", secondsOnCampusCalculation);
                } else {
                    // Format the time as 1 second
                    totalSecondsOnCampusFormattedValue = String.format("%d second", secondsOnCampusCalculation);
                }
            }

            // 0 mins off screen > 0 mins off campus
            if(totalMinutesOffScreenFormattedValue == "" && totalMinutesOnCampusFormattedValue != "") {
                text = "Achieved " + totalSecondsOffScreenFormattedValue +
                        " off screen out of " + totalMinutesOnCampusFormattedValue + " " + totalSecondsOnCampusFormattedValue + " total time on campus";
            }
            // 0 mins off screen 0 mins on campus
            else if (totalMinutesOffScreenFormattedValue == "" && totalMinutesOnCampusFormattedValue == "") {
                text = "Achieved " + totalSecondsOffScreenFormattedValue +
                        " off screen out of " + totalSecondsOnCampusFormattedValue + " total time on campus";
            }
            // > 0 mins off screen > 0 mins on campus
            else if (totalMinutesOffScreenFormattedValue != "" && totalMinutesOnCampusFormattedValue != "") {
                text = "Achieved " + totalMinutesOffScreenFormattedValue + " " + totalSecondsOffScreenFormattedValue +
                        " off screen out of " + totalMinutesOnCampusFormattedValue + " " + totalSecondsOnCampusFormattedValue + " total time on campus";
            }

            return text;
        }

        return "No time has been spent on campus this week";
    }

    private Long calculateTimeOnCampus(Day day) {
        Long totalCampusTime = 0L;

        if(day.getCampusTimes() != null) {
            for(CampusTime time : day.getCampusTimes()) {
                if(time.getLeaveTime() > time.getEnterTime()) {
                    Long campT = time.getLeaveTime() - time.getEnterTime();
                    totalCampusTime += campT;
                }
            }
        }

        return totalCampusTime;
    }

    private Long calculateTimeOnScreen(Day day) {
        Long timeOnScreen = 0L;

        if(day.getCampusTimes() != null) {
            for(CampusTime time : day.getCampusTimes()) {
                timeOnScreen += time.getOnScreenTime();
            }
        }

        return timeOnScreen;
    }

    private Long calculateTimeOffScreen(Day day) {

        return calculateTimeOnCampus(day) - calculateTimeOnScreen(day);
    }
}
