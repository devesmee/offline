package com.example.offline.frontend;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.offline.R;
import com.example.offline.backend.week.Week;
import com.example.offline.backend.week.WeekManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WeeklyOverviewFragment extends Fragment {

    private String title;
    private String subtitle;
    private Week week;
    private WeekManager weekManager;

    public WeeklyOverviewFragment(String title, String subtitle, Week week) {
        this.title = title;
        this.subtitle = subtitle;
        this.week = week;
        this.weekManager = WeekManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_weekly_overview, container, false);
        weekManager.setDayObjectsInWeek(this.week);

        // passes the week object to the WeeklyOverviewGoalsProgressFragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("selectedWeek", this.week);

        WeeklyOverviewGoalsProgressFragment weeklyOverviewGoalsProgressFragment = new WeeklyOverviewGoalsProgressFragment();
        weeklyOverviewGoalsProgressFragment.setArguments(bundle);

        TopAppUsageFragment topAppUsageFragment = new TopAppUsageFragment(false);
        topAppUsageFragment.setArguments(bundle);

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();//create an instance of Fragment-transaction

        transaction.add(R.id.goalProgressCardWeeklyOverviewFrameLayout, weeklyOverviewGoalsProgressFragment, "WeeklyOverviewGoalsCard");
        transaction.commit();

        // passes the week object to the weeklyNrUnlocksFragment
        Bundle nrUnlocksBundle = new Bundle();
        nrUnlocksBundle.putParcelable("selectedWeek", this.week);
        WeeklyNrUnlocksFragment weeklyNrUnlocksFragment = new WeeklyNrUnlocksFragment();
        weeklyNrUnlocksFragment.setArguments(nrUnlocksBundle);

        FragmentTransaction transaction2 = manager.beginTransaction();//create an instance of Fragment-transaction

        transaction2.add(R.id.weeklyNrUnlocksFrLayout, weeklyNrUnlocksFragment, "weeklyNrUnlocksFragment");
        transaction2.add(R.id.weeklyTopApps, topAppUsageFragment, "topAppUsageFragment");

        transaction2.commit();

        return rootView;
    }

    private ArrayList getWeekdaysArrayOfCurrentWeekAsDates() {

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

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }
}


