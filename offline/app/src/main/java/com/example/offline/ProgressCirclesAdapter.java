package com.example.offline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.offline.backend.calendar.CalendarUtil;
import com.example.offline.backend.day.Day;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

public class ProgressCirclesAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private final Context mContext;
    ArrayList<Integer> daysArray;
    ArrayList<Day> allDays;
    int counter;
    Month currentMonth;
    int currentYear;
    FragmentManager manager;

    public ProgressCirclesAdapter(FragmentManager manager, Context context, ArrayList<Day> allDaysInMonth, ArrayList<Integer> daysArray, Month currentMonth, int currentYear) {
        this.mContext = context;
        this.allDays = allDaysInMonth;
        this.daysArray = daysArray;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.counter = 0;
        this.currentMonth = currentMonth;
        this.manager = manager;
        this.currentYear = currentYear;
    }

    @Override
    public int getCount() {
        return daysArray.size();
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_monthly_overview_cardview_content, null);
        }

        PieChart pieChart;
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();

        // Get goal.
        double currentDayGoalPercentage = 0;
        double currentDayProgress = 0;
        Integer monthStartPosition = 0;

        try {
            for (Integer integerDay : this.daysArray) {
                if (integerDay.equals(0))
                    monthStartPosition++;
            }

            LocalDate compareDate;

            if (daysArray.get(position) >= 1) {
                compareDate = LocalDate.of(currentYear, currentMonth, daysArray.get(position));
            } else {
                compareDate = LocalDate.of(currentYear, currentMonth, daysArray.get(monthStartPosition));
            }

            for (Day day : allDays) {
                if ((day.getDate().equals(CalendarUtil.getStringDate(compareDate)) && (day.getGoal().getPercentage() != 0))) {
                    currentDayGoalPercentage = day.getGoal().getPercentage();
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

        if (currentDayProgress >= currentDayGoalPercentage) {
            progressColor = ContextCompat.getColor(mContext, R.color.blue_light);
        } else {
            progressColor = ContextCompat.getColor(mContext, R.color.orange_bright);
        }
        pieDataSet.setColors(progressColor, Color.LTGRAY);

        PieData pieData = new PieData(pieDataSet);

        pieChart = convertView.findViewById(R.id.monthlyGoalProgressCircle);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setHoleRadius(65f);
        pieChart.setDrawRoundedSlices(true);
        pieChart.getData().setDrawValues(false);

        pieChart.invalidate();

        TextView dateText = convertView.findViewById(R.id.monthlyOverviewDate);
        // If 0 then empty view so the calendar starts on the right day of the week.
        if (daysArray.get(position) != 0) {
            String day = daysArray.get(position).toString();
            dateText.setText(day);
        } else {
            pieChart.setVisibility(View.INVISIBLE);
            dateText.setText("");
        }

        // Handle progress circle click
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                final NavController navController = Navigation.findNavController(((AppCompatActivity) mContext), R.id.fragment_container);
                LocalDate compareDate = LocalDate.of(currentYear, currentMonth, daysArray.get(position));
                for (Day day : allDays) {
                    if (day.getDate().equals(CalendarUtil.getStringDate(compareDate))) {
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

        convertView.setOnClickListener(view -> {
            mContext.sendBroadcast(new Intent("call.fragment.daily"));
        });
        return convertView;
    }
}
