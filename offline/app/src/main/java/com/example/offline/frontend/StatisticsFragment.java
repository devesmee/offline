package com.example.offline.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.offline.MainActivity;
import com.example.offline.R;

public class StatisticsFragment extends Fragment implements IWeeklyPageChanged {

    TextView titleTextView;
    TextView subtitleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        ImageButton ibCalendarSwitchToMonthlyOverview = (ImageButton) rootView.findViewById(R.id.ibCalendarSwitchToMonthly);

        if (((MainActivity) getActivity()).getPreviousStatisticsFrag().equals("WeeklyFragment")) {
            getChildFragmentManager().beginTransaction().replace(R.id.statisticsFragment, new WeeklyOverviewContainerFragment(this), "WeeklyOverviewContainerFragment").commit();
            ibCalendarSwitchToMonthlyOverview.setImageResource(R.drawable.ic_calendar_30);
        } else {
            getChildFragmentManager().beginTransaction().replace(R.id.statisticsFragment, new MonthlyOverviewFragment(), "MonthlyOverviewFragment").commit();
            ibCalendarSwitchToMonthlyOverview.setImageResource(R.drawable.ic_calendar_7);
            ((MainActivity) getActivity()).setPreviousStatisticsFrag("MonthlyFragment");
        }

        this.titleTextView = rootView.findViewById(R.id.title);
        this.subtitleTextView = rootView.findViewById(R.id.subtitle);

        ibCalendarSwitchToMonthlyOverview.setOnClickListener(a -> {
            // TODO: load fragment_monthly_overview
            if (((MainActivity) getActivity()).getPreviousStatisticsFrag().equals("MonthlyFragment")) {
                getChildFragmentManager().beginTransaction().replace(R.id.statisticsFragment, new WeeklyOverviewContainerFragment(this), "WeeklyOverviewContainerFragment").commit();
                ((MainActivity) getActivity()).setPreviousStatisticsFrag("WeeklyFragment");

                ibCalendarSwitchToMonthlyOverview.setImageResource(R.drawable.ic_calendar_30);
            } else {
                getChildFragmentManager().beginTransaction().replace(R.id.statisticsFragment, new MonthlyOverviewFragment(), "MonthlyOverviewFragment").commit();
                ((MainActivity) getActivity()).setPreviousStatisticsFrag("MonthlyFragment");

                ibCalendarSwitchToMonthlyOverview.setImageResource(R.drawable.ic_calendar_7);

            }

        });

        return rootView;
    }

    @Override
    public void onPageChanged(WeeklyOverviewFragment fragment) {
        this.titleTextView.setText(fragment.getTitle());
        this.subtitleTextView.setText(fragment.getSubtitle());
    }
}


