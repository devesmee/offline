package com.example.offline.frontend;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.offline.R;
import com.example.offline.backend.appusage.Statistics;
import com.example.offline.backend.appusage.UsageStatsHelper;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.week.Week;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopAppUsageFragment extends Fragment {

    private View currentView;
    private ImageView appLogo;
    UsageStatsHelper usageStatsHelper;
    DayManager dayManager;
    ArrayList<Statistics> appUsage = new ArrayList<>();
    private final boolean isDaily;
    Bundle arguments;


    public TopAppUsageFragment(boolean isDaily) {
        this.isDaily = isDaily;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_top_app_usage, container, false);
        arguments = getArguments();
        usageStatsHelper = new UsageStatsHelper(getActivity());
        dayManager = new DayManager(getActivity());

        this.loadAppUsageData();

        Button seeAllBtn = currentView.findViewById(R.id.seeAllBtn);
        ImageView appLogoContainer = currentView.findViewById(R.id.topAppUsageBubble);

        seeAllBtn.setOnClickListener(v -> {
            final NavController navController = Navigation.findNavController(((AppCompatActivity) getContext()), R.id.fragment_container);
            if (isDaily) {
                navController.navigate(R.id.action_to_app_usage, arguments);
            } else {
                navController.navigate(R.id.action_to_weekly_app_usage, arguments);
            }

        });

        if (appUsage.size() > 0) {
            appLogoContainer.setVisibility(View.VISIBLE);
            seeAllBtn.setVisibility(View.VISIBLE);
            showTopAppUsage(usageStatsHelper.getTop5Apps(appUsage));
        }

        return currentView;
    }

    public void showTopAppUsage(ArrayList<Statistics> topAppsList) {
        PieChart chart = (PieChart) currentView.findViewById(R.id.topAppUsageChart);
        appLogo = currentView.findViewById(R.id.topAppUsageLogo);

        chart.getDescription().setEnabled(false);

        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(90f);
        chart.setTransparentCircleRadius(120f);
        ArrayList<Integer> dataObjects = new ArrayList<>();

        chart.setEntryLabelColor(Color.BLACK);
        for (Statistics statistics :
                topAppsList) {

            dataObjects.add((int) statistics.usage);
        }

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (Statistics statistics :
                topAppsList) {
            entries.add(new PieEntry(statistics.usage, statistics.appName));
        }

        appLogo.setBackground(topAppsList.get(topAppsList.size() - 1).getIcon());

        chart.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(entries, ""); // add entries to dataset
        dataSet.setSliceSpace(7f);
        dataSet.setSelectionShift(5f);

        // colors of chart
        int chartColor1 = ContextCompat.getColor(getContext(), R.color.purple_fontys);
        int chartColor2 = ContextCompat.getColor(getContext(), R.color.purple_muted);
        int chartColor3 = ContextCompat.getColor(getContext(), R.color.blue_light);
        int chartColor4 = ContextCompat.getColor(getContext(), R.color.blue_dark);
        int chartColor5 = ContextCompat.getColor(getContext(), R.color.yellow);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(chartColor1);
        colors.add(chartColor2);
        colors.add(chartColor3);
        colors.add(chartColor4);
        colors.add(chartColor5);
        dataSet.setColors(colors);

        dataSet.setValueTextColor(Color.RED);

        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLineColor(Color.GRAY);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.TRANSPARENT);

        final String[] lastSelectedName = {topAppsList.get(topAppsList.size() - 1).getAppName()};
        final Integer[] lastSelectedPieEntryIndex = {topAppsList.size() - 1};
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                entries.get(lastSelectedPieEntryIndex[0]).setLabel(lastSelectedName[0]);
                PieEntry entry = (PieEntry) e;
                for (Statistics app : topAppsList) {
                    if (app.appName == entry.getLabel()) {
                        appLogo.setBackground(app.getIcon());
                        entry.setLabel(usageStatsHelper.convertTimeToText(app.getUsage()));
                        lastSelectedPieEntryIndex[0] = topAppsList.indexOf(app);
                        lastSelectedName[0] = app.getAppName();
                    }
                }
            }

            @Override
            public void onNothingSelected() {
                //show the logo of the most used app
            }
        });


        chart.setData(data);
        chart.invalidate();
    }

    public Map<Integer, String> getPercentage(ArrayList<Statistics> topAppsList) {
        Map map = new HashMap();
        double totalUsage = 0L;

        for (Statistics app : topAppsList) {
            totalUsage = totalUsage + (double) app.usage;
        }

        for (Statistics app : topAppsList) {
            double percent = (double) app.usage / totalUsage * 100;
            map.put(app.appName, percent);
        }

        return map;
    }

    private void loadAppUsageData() {

        if (this.isDaily) {
            appUsage = usageStatsHelper.getUsageOfDay(arguments.getParcelable("selectedDay"));
        } else {
            Week selectedWeek = arguments.getParcelable("selectedWeek");
            ArrayList<Day> tempWeekDays = new ArrayList<>();

            for (Day day : selectedWeek.getDaysInWeek()) {
                if (day.getCampusTimes() != null && day.getCampusTimes().size() > 0) {
                    tempWeekDays.add(day);
                }
                ;
            }

            if (tempWeekDays.size() > 0) {
                appUsage = usageStatsHelper.getTop5AppsByWeek(tempWeekDays);
            }
        }
    }
}