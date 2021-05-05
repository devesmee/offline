package com.example.offline.frontend;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.offline.R;
import com.example.offline.backend.day.CampusTime;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DailyGoalProgressFragment extends Fragment {
    private TextView setGoalTextView;
    private TextView timeInCampusTextView;
    private PieChart pieChart;
    private View currentView;
    private DayManager dayManager;
    private Day selectedDay;
    private Long timeOffScreen;
    private Long timeOnCampus;
    private double goalProgresss;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_daily_goal_progress, container, false);

        dayManager = DayManager.getInstance(getContext());
        setGoalTextView = (TextView) currentView.findViewById(R.id.SetDailyGoal);
        timeInCampusTextView = (TextView) currentView.findViewById(R.id.DailyGoalTimeInformation);
        selectedDay = (Day) getArguments().getParcelable("selectedDay");
        timeOnCampus = calculateTimeOnCampus();
        timeOffScreen = calculateTimeOffScreen();

        goalProgresss = selectedDay.getGoal().getProgress();
        showDayGoalProgress(selectedDay);

        return currentView;
    }

    public void showDayGoalProgress(Day theDay) {
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();

        if (goalProgresss > 100) {
            // When the progress is over 100%, it will show the full circle
            pieEntries.add(new PieEntry((float) 100));
        } else {
            if (goalProgresss < 0) {
                pieEntries.add(new PieEntry((float) 0));
            } else {
                pieEntries.add(new PieEntry((float) goalProgresss));
            }
            pieEntries.add(new PieEntry((float) (100 - goalProgresss)));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");

        // To determine what colour the progress should be
        int progressColor;
        TextView goalStatus = (TextView) currentView.findViewById(R.id.DailyGoalTitle);

        if (goalProgresss >= theDay.getGoal().getPercentage()) {
            progressColor = ContextCompat.getColor(getActivity(), R.color.blue_light);
            goalStatus.setText(R.string.daily_goal_status_acheived);
        } else {
            progressColor = ContextCompat.getColor(getActivity(), R.color.orange_bright);
            goalStatus.setText(R.string.daily_goal_status_not_acheived);
        }

        setGoalTextView.setText(theDay.getGoal().getPercentage() + "% off screen");

        Long minutesOnCampusCalculation = TimeUnit.MILLISECONDS.toMinutes(timeOnCampus);
        Long secondsOnCampusCalculation = TimeUnit.MILLISECONDS.toSeconds(timeOnCampus) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeOnCampus));

        Long minutesOffScreenCalculation = TimeUnit.MILLISECONDS.toMinutes(timeOffScreen);
        Long secondsOffScreenCalculation = TimeUnit.MILLISECONDS.toSeconds(timeOffScreen) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeOffScreen));

        String totalMinutesOffScreenFormattedValue = "";
        String totalSecondsOffScreenFormattedValue = "";

        String totalMinutesOnCampusFormattedValue = "";
        String totalSecondsOnCampusFormattedValue = "";

        String text = "";

        if (timeOnCampus > 0) {

            if (minutesOffScreenCalculation != 0) {

                if (minutesOffScreenCalculation > 1) {
                    // Format the time as > 1 minutes
                    totalMinutesOffScreenFormattedValue = String.format("%d minutes", minutesOffScreenCalculation);
                } else {
                    // Format the time as 1 minute
                    totalMinutesOffScreenFormattedValue = String.format("%d minute", minutesOffScreenCalculation);
                }

                if (secondsOffScreenCalculation > 1 || secondsOffScreenCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d seconds", secondsOffScreenCalculation);
                } else {
                    // Format the time as 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d second", secondsOffScreenCalculation);
                }
            } else {

                if (secondsOffScreenCalculation > 1 || secondsOffScreenCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d seconds", secondsOffScreenCalculation);
                } else {
                    // Format the time as 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d second", secondsOffScreenCalculation);
                }
            }

            if (minutesOnCampusCalculation != 0) {

                if (minutesOnCampusCalculation > 1) {
                    // Format the time as > 1 minutes
                    totalMinutesOnCampusFormattedValue = String.format("%d minutes", minutesOnCampusCalculation);
                } else {
                    // Format the time as 1 minute
                    totalMinutesOnCampusFormattedValue = String.format("%d minute", minutesOnCampusCalculation);
                }

                if (secondsOnCampusCalculation > 1 || secondsOnCampusCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOnCampusFormattedValue = String.format("%d seconds", secondsOnCampusCalculation);
                } else {
                    // Format the time as 1 second
                    totalSecondsOnCampusFormattedValue = String.format("%d second", secondsOnCampusCalculation);
                }

            } else {

                if (secondsOnCampusCalculation > 1 || secondsOnCampusCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOnCampusFormattedValue = String.format("%d seconds", secondsOnCampusCalculation);
                } else {
                    // Format the time as 1 second
                    totalSecondsOnCampusFormattedValue = String.format("%d second", secondsOnCampusCalculation);
                }
            }

            // 0 mins off screen > 0 mins off campus
            if (totalMinutesOffScreenFormattedValue == "" && totalMinutesOnCampusFormattedValue != "") {
                text = "You were off screen for " + totalSecondsOffScreenFormattedValue +
                        " out of " + totalMinutesOnCampusFormattedValue + " " + totalSecondsOnCampusFormattedValue + " spent on campus";
            }
            // 0 mins off screen 0 mins on campus
            else if (totalMinutesOffScreenFormattedValue == "" && totalMinutesOnCampusFormattedValue == "") {
                text = "You were off screen for " + totalSecondsOffScreenFormattedValue +
                        " out of " + totalSecondsOnCampusFormattedValue + " spent on campus";
            }
            // > 0 mins off screen > 0 mins on campus
            else if (totalMinutesOffScreenFormattedValue != "" && totalMinutesOnCampusFormattedValue != "") {
                text = "You were off screen for " + totalMinutesOffScreenFormattedValue + " " + totalSecondsOffScreenFormattedValue +
                        " out of " + totalMinutesOnCampusFormattedValue + " " + totalSecondsOnCampusFormattedValue + " spent on campus";
            }
        }

        timeInCampusTextView.setText(text);
        pieDataSet.setColors(progressColor, Color.LTGRAY);

        PieData pieData = new PieData(pieDataSet);

        // Set up the pie chart
        pieChart = (PieChart) currentView.findViewById(R.id.GoalProgressCircle);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setCenterText(setPieChartCenterText((int) goalProgresss));
        pieChart.setCenterTextColor(Color.GRAY);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setCenterTextSize(24);
        pieChart.setHoleRadius(80f);
        pieChart.setDrawRoundedSlices(true);
        pieChart.getData().setDrawValues(false);
        pieChart.invalidate();
    }

    private String setPieChartCenterText(int todaysProgress) {
        return todaysProgress + "% \n off screen";
    }

    private Long calculateTimeOnCampus() {
        Long totalCampusTime = 0L;

        for (CampusTime time : selectedDay.getCampusTimes()) {
            Long campT = time.getLeaveTime() - time.getEnterTime();
            totalCampusTime = Long.sum(totalCampusTime, campT);
        }

        return totalCampusTime;
    }

    private Long calculateTimeOnScreen() {
        Long timeOnScreen = 0L;

        for (CampusTime time : selectedDay.getCampusTimes()) {
            timeOnScreen = Long.sum(timeOnScreen, time.getOnScreenTime());
        }

        return timeOnScreen;
    }

    private Long calculateTimeOffScreen() {
        Long offScreenTime = 0L;

        offScreenTime = calculateTimeOnCampus() - calculateTimeOnScreen();

        return offScreenTime;
    }
}