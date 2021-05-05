package com.example.offline.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.offline.R;
import com.example.offline.SystemAppAdapter;
import com.example.offline.backend.appusage.Statistics;
import com.example.offline.backend.appusage.UsageStatsHelper;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.week.Week;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AppUsageWeeklyFragment extends Fragment {
    UsageStatsHelper usageStatsHelper;
    DayManager dayManager;
    ArrayList<Statistics> appUsage;
    Week selectedWeek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_usage, container, false);
        usageStatsHelper = new UsageStatsHelper(getActivity());
        dayManager = new DayManager(getActivity());

        loadWeeklyAppUsageData();

        //setup adapter
        GridView gridView = (GridView) rootView.findViewById(R.id.appsUsageGridview);
        SystemAppAdapter systemAppAdapter = new SystemAppAdapter(getContext(), appUsage);
        gridView.setAdapter(systemAppAdapter);
        //change title
        TextView title = (TextView) rootView.findViewById(R.id.appUsageCardTitle);
        title.setText("Weekly overview");
        //set bottom nav bar to visible
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        // Back button
        ImageView backButton = rootView.findViewById(R.id.appUsageBackButton);
        backButton.setOnClickListener(view -> {
            final NavController navController = Navigation.findNavController(((AppCompatActivity) getContext()), R.id.fragment_container);
            navController.navigate(R.id.action_app_usage_to_weekly, getArguments());
        });
        return rootView;
    }

    private void loadWeeklyAppUsageData() {
        if (getArguments() != null) {
            selectedWeek = getArguments().getParcelable("selectedWeek");
        }
        ArrayList<Day> tempWeekDays = new ArrayList<>();
        for (Day day : selectedWeek.getDaysInWeek()) {
            if (day.getCampusTimes() != null && day.getCampusTimes().size() > 0) {
                tempWeekDays.add(day);
            }
        }
        appUsage = usageStatsHelper.getAllUsedAppsByWeek(tempWeekDays);

    }
}


