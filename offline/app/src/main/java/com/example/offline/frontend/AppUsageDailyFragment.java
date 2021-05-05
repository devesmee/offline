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
import com.example.offline.backend.calendar.CalendarUtil;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AppUsageDailyFragment extends Fragment {
    UsageStatsHelper usageStatsHelper;
    DayManager dayManager;
    ArrayList<Statistics> appUsage;
    Day selectedDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_usage, container, false);
        usageStatsHelper = new UsageStatsHelper(getActivity());
        dayManager = new DayManager(getActivity());

        if (getArguments() != null) {
            selectedDay = getArguments().getParcelable("selectedDay");
        }

        appUsage = usageStatsHelper.getUsageOfDay(selectedDay);

        //setup adapter
        GridView gridView = (GridView) rootView.findViewById(R.id.appsUsageGridview);
        SystemAppAdapter systemAppAdapter = new SystemAppAdapter(getContext(), appUsage);
        gridView.setAdapter(systemAppAdapter);
        //change title
        TextView title = (TextView) rootView.findViewById(R.id.titleAppUsageFragment);
        String monthString = CalendarUtil.getMonth(selectedDay).toString();
        monthString = monthString.substring(0, 1).toUpperCase() + monthString.substring(1).toLowerCase();
        String dateString = CalendarUtil.getDayOfMonth(selectedDay) + " " + monthString + " " + CalendarUtil.getYear(selectedDay);
        title.setText(dateString);
        //set bottom nav bar to visible
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        // Back button
        ImageView backButton = rootView.findViewById(R.id.appUsageBackButton);
        backButton.setOnClickListener(view -> {
            final NavController navController = Navigation.findNavController(((AppCompatActivity) getContext()), R.id.fragment_container);
            navController.navigate(R.id.action_app_usage_to_daily, getArguments());
        });

        return rootView;
    }
}