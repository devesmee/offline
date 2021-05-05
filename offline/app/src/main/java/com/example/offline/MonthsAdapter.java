package com.example.offline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.example.offline.backend.calendar.CalendarMonth;
import com.example.offline.backend.calendar.CalendarUtil;

import java.util.ArrayList;

public class MonthsAdapter extends PagerAdapter {

    private final ArrayList<CalendarMonth> allMonths;
    private final Context mContext;
    FragmentManager manager;

    public MonthsAdapter(FragmentManager manager, Context context, ArrayList<CalendarMonth> months) {
        this.allMonths = months;
        this.mContext = context;
        this.manager = manager;
    }

    @Override
    public int getCount() {
        return allMonths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.fragment_monthly_overview_cardview, container, false);
        TextView monthTextView = view.findViewById(R.id.calendarCurrentMonth);
        monthTextView.setText(allMonths.get(position).getMonthName());

        // Populate the GridView with the progress circles.
        GridView gridView = view.findViewById(R.id.monthlyOverviewGridview);
        ProgressCirclesAdapter progressCirclesAdapter = new ProgressCirclesAdapter(manager, mContext, allMonths.get(1).getDaysInMonth(), CalendarUtil.getDaysInMonth(allMonths.get(position)), allMonths.get(position).getMonth(), allMonths.get(position).getMonthYear());
        gridView.setAdapter(progressCirclesAdapter);

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
