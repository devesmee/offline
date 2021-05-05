package com.example.offline;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.offline.backend.calendar.CalendarUtil;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.goal.Goal;
import com.example.offline.backend.week.Week;
import com.example.offline.backend.week.WeekManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeeklyProgressCirclesAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private Context mContext;
    ArrayList<Day> daysArray;
    ArrayList<Integer> daysIntegersArray;
    private DayManager dayManager = null;
    Day currentDay;
    Integer counter;
    Week currentWeek;
    WeekManager weekManager;

    public WeeklyProgressCirclesAdapter(Context context, ArrayList<Day> daysArray, Week week) {
        this.mContext = context;
        this.daysArray = daysArray;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dayManager = DayManager.getInstance(context);
        this.currentDay = dayManager.getCurrentDay();
        this.counter = 0;
        this.currentWeek = week;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        weekManager = WeekManager.getInstance(mContext);

        this.daysIntegersArray = new ArrayList<Integer>();
        this.daysIntegersArray.add(1);
        this.daysIntegersArray.add(2);
        this.daysIntegersArray.add(3);
        this.daysIntegersArray.add(4);
        this.daysIntegersArray.add(5);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_weekly_overview_goals_content, null);
        }

        PieChart pieChart;
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();

        double currentDayProgress = 0;
        try {
            LocalDate compareDate = CalendarUtil.getLocalDate(weekManager.getDayObjectOfWeek(currentWeek, position));
            for (Day day : daysArray) {
                if ((day.getDate().equals(CalendarUtil.getStringDate(compareDate)) && (day.getGoal().getPercentage() != 0))) {
                    currentDayProgress = day.getGoal().getProgress();
                }
            }
        } catch (IndexOutOfBoundsException e) {
            // Day object doesn't exist.
            // Do nothing so circle stays gray.
        }

        if (currentDayProgress > 100) {
            // When the progress is over 100%, it will show the full circle
            pieEntries.add(new PieEntry((float) 100));
        } else {
            pieEntries.add(new PieEntry((float) currentDayProgress));
            pieEntries.add(new PieEntry((float) (100 - currentDayProgress)));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");

        // To determine what colour the progress should be
        int progressColor;
        if (currentDayProgress >= dayManager.getCurrentDay().getGoal().getPercentage()) {
            progressColor = ContextCompat.getColor(mContext, R.color.blue_light);
        } else {
            progressColor = ContextCompat.getColor(mContext, R.color.orange_bright);
        }
        pieDataSet.setColors(progressColor, Color.LTGRAY);

        PieData pieData = new PieData(pieDataSet);

        // Set up the pie chart
        pieChart = (PieChart) convertView.findViewById(R.id.weeklyGoalProgressCircle);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setCenterTextSize(24);
        pieChart.setHoleRadius(68f);
        pieChart.setDrawRoundedSlices(true);
        pieChart.getData().setDrawValues(false);
        pieChart.invalidate();

        // Handle progress circle click
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                final NavController navController = Navigation.findNavController(((AppCompatActivity) mContext), R.id.fragment_container);

                LocalDate compareDate = CalendarUtil.getLocalDate(weekManager.getDayObjectOfWeek(currentWeek, position));

                for (Day day : daysArray) {
                    if ((day.getDate().equals(CalendarUtil.getStringDate(compareDate)) && (day.getGoal().getPercentage() != 0))) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("selectedDay", day);
                        navController.navigate(R.id.action_to_daily, bundle);
                    }
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        return convertView;
    }
}
