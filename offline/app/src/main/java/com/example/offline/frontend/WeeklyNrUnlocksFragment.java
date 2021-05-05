package com.example.offline.frontend;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.offline.R;
import com.example.offline.backend.appusage.UnlockHelper;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.week.Week;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeeklyNrUnlocksFragment extends Fragment {

    UnlockHelper unlockHelper;
    ArrayList<Pair<String, Integer>> results;
    View currentView;
    TextView totalUnlocksText;
    TextView frequencyText;
    BarChart barChart;
    private Week week;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_weekly_nr_unlocks, container, false);

        this.week = getArguments().getParcelable("selectedWeek");

        totalUnlocksText = (TextView) currentView.findViewById(R.id.totalUnlocksText);
        frequencyText = (TextView) currentView.findViewById(R.id.frequencyText);
        barChart = (BarChart) currentView.findViewById(R.id.barChart);

        unlockHelper = new UnlockHelper(getContext());

        results = unlockHelper.getWeeklyUnlocksDynamically(this.week);

        int totalUnlocks = 0;
        for (Pair pair : results) {
            totalUnlocks += (Integer) pair.second;
        }

        ArrayList<Day> days = DayManager.getInstance(getContext()).getDays();

        ArrayList<Day> sublistDays;
        if (days.size() > 7) {
            sublistDays = new ArrayList<Day>(days.subList(days.size() - 7, days.size()));
        } else {
            sublistDays = days;
        }

        long timeSpentOnCampus = unlockHelper.getTimeSpendOnCampus(sublistDays);
        Integer unlockFrequency = totalUnlocks > 0 ? (Math.toIntExact(timeSpentOnCampus) / totalUnlocks) / 1000 / 60 : 0;


        totalUnlocksText.setText(getString(R.string.total_unlocks) + " " + totalUnlocks);
        frequencyText.setText(getString(R.string.frequency_unlocks) + " " + unlockFrequency + " " + getString(R.string.minutes));

        BarData data = getBarChartData();
        barChart.setData(data);
        Description description = new Description();
        description.setText("Number of unlocks");
        barChart.setDescription(description);
        barChart.setDrawGridBackground(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);

        List<String> xAxisValues = new ArrayList<>(Arrays.asList("M", "T", "W", "T", "F"));
        barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        return currentView;
    }

    public BarData getBarChartData() {

        ArrayList<BarEntry> entries = new ArrayList<>();

        ArrayList<String> weekdayLetters = new ArrayList<>();
        weekdayLetters.add("M");
        weekdayLetters.add("T");
        weekdayLetters.add("W");
        weekdayLetters.add("T");
        weekdayLetters.add("F");

        for (int i = 0; i < results.size(); i++) {
            entries.add(new BarEntry(i, results.get(i).second));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Unlocks between " + week.getStartDate() + " and " + week.getEndDate());

        List<Integer> colors = new ArrayList();
        colors.add(ContextCompat.getColor(requireContext(), R.color.blue_dark));
        colors.add(ContextCompat.getColor(requireContext(), R.color.blue_light));
        barDataSet.setColors(colors);
        BarData data = new BarData(barDataSet);

        return data;
    }
}