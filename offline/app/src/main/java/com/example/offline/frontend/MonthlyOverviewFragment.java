package com.example.offline.frontend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.offline.backend.calendar.Calendar;
import com.example.offline.MonthsAdapter;
import com.example.offline.R;
import com.example.offline.backend.day.DayManager;

import java.text.ParseException;

public class MonthlyOverviewFragment extends Fragment {
    String Tag = ".MonthlyOverviewFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DayManager dayManager = DayManager.getInstance(getContext());
        Log.i(Tag, "dayManager all days count: " + dayManager.getDays().size());

        Calendar calendar = new Calendar(dayManager);

        View rootView = inflater.inflate(R.layout.fragment_monthly_overview, container, false);
        TextView title = getParentFragment().getView().findViewById(R.id.title);
        title.setText(R.string.monthly_overview_title);
        TextView yearTextView = getParentFragment().getView().findViewById(R.id.subtitle);


        // Populate the ViewPager.
        ViewPager viewPager = rootView.findViewById(R.id.monthlyOverviewViewPager);
        MonthsAdapter monthsAdapter = null;
        try {
            monthsAdapter = new MonthsAdapter(this.getChildFragmentManager(), getContext(), calendar.getAllMonths());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewPager.setAdapter(monthsAdapter);
        // Change the year based on the current shown card.
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                try {
                    yearTextView.setText(String.valueOf(calendar.getAllMonths().get(position).getMonthYear()));
                } catch (ParseException e) {
                    e.printStackTrace();
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

}

