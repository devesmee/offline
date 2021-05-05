package com.example.offline.frontend;

import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.offline.R;
import com.example.offline.WeeksPagerAdapter;
import com.example.offline.backend.calendar.CalendarUtil;
import com.example.offline.backend.week.Week;
import com.example.offline.backend.week.WeekManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WeeklyOverviewContainerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IWeeklyPageChanged iWeeklyPageChanged;
    private WeekManager weekManager;
    private CalendarUtil calendarUtil;

    public WeeklyOverviewContainerFragment(IWeeklyPageChanged iWeeklyPageChangedListener) {
        // Required empty public constructor
        this.iWeeklyPageChanged = iWeeklyPageChangedListener;
        this.weekManager = WeekManager.getInstance(getContext());
        this.calendarUtil = new CalendarUtil();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weekly_overview_container, container, false);

        // Populate the ViewPager with weekly content
        ViewPager viewPager = rootView.findViewById(R.id.weeklyViewPager);
        WeeksPagerAdapter weeksPagerAdapter = new WeeksPagerAdapter(getChildFragmentManager(), 0, getContext());

        int index = 1;
        for (Week week : this.weekManager.getFontysWeeks()) {
            weeksPagerAdapter.addFragment(new WeeklyOverviewFragment("Week " + index, generateWeekBoundsSubtitle(week), week));
            index++;
        }

        viewPager.setAdapter(weeksPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (iWeeklyPageChanged != null) {
                    iWeeklyPageChanged.onPageChanged((WeeklyOverviewFragment) weeksPagerAdapter.getItem(position));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }

    private String generateWeekBoundsSubtitle(Week week) {

        String month = week.getStartDate().getMonth().toString();
        String firstCapitalizedCharacterMonth = month.charAt(0) + month.substring(1).toLowerCase();

        return week.getStartDate().getDayOfMonth() + " - " +
                week.getEndDate().getDayOfMonth() + " " +
                firstCapitalizedCharacterMonth + " " +
                week.getStartDate().getYear();
    }

    private String generateWeekBoundsSubtitleOfCurrentWeek() {
        // Get calendar set to current date and time
        Calendar c = Calendar.getInstance();

        ArrayList weekdays = new ArrayList();

        // Set the calendar to Monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        DateFormat dayFormat = new SimpleDateFormat("dd");
        DateFormat monthFormat = new SimpleDateFormat("MMMM");
        DateFormat yearFormat = new SimpleDateFormat("yyyy");

        for (int i = 1; i < 6; i++) {

            // loop through Monday to Friday
            if (i == 1 || i == 5) {

                // remove 0 from dates earlier than the 10th
                if (dayFormat.format(c.getTime()).charAt(0) == '0') {
                    weekdays.add(dayFormat.format(c.getTime()).substring(1));
                } else {
                    weekdays.add(dayFormat.format(c.getTime()));
                }
            }

            c.add(Calendar.DATE, 1);
        }

        return weekdays.get(0) + " - " + weekdays.get(1) + " " + monthFormat.format(c.getTime()) + " " + yearFormat.format(c.getTime());
    }
}