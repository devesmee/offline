package com.example.offline.frontend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.offline.MainActivity;
import com.example.offline.R;
import com.example.offline.backend.calendar.CalendarUtil;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;

public class DailyOverviewFragment extends Fragment {

    Day selectedDay;
    DayManager dayManager;
    TextView dayTitleTextView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dayManager = DayManager.getInstance(getContext());
        assert getArguments() != null;

        selectedDay = getArguments().getParcelable("selectedDay");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_daily_overview, container, false);

        dayTitleTextView = (TextView) rootView.findViewById(R.id.titleDailyOverviewFragment);
        setDayLabel();

        // Pass the selected day in bundle to other fragments
        Bundle bundle = new Bundle();
        bundle.putParcelable("selectedDay", selectedDay);

        DailyGoalProgressFragment dailyGoalProgressFragment = new DailyGoalProgressFragment();
        dailyGoalProgressFragment.setArguments(bundle);
        TopAppUsageFragment topAppUsageFragment = new TopAppUsageFragment(true);
        topAppUsageFragment.setArguments(bundle);
        DailyNrUnlocksFragment dailyNrUnlocksFragment = new DailyNrUnlocksFragment();
        dailyNrUnlocksFragment.setArguments(bundle);
        DailyProductivityFeelingFragment dailyProductivityFeelingFragment = new DailyProductivityFeelingFragment();
        dailyProductivityFeelingFragment.setArguments(bundle);

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();//create an instance of Fragment-transaction

        transaction.add(R.id.goalProgressCard, dailyGoalProgressFragment, "DailyGoalsProgressFragment");
        transaction.add(R.id.topAppUsageCard, topAppUsageFragment, "TopAppUsageFragment");
        transaction.add(R.id.numberOfUnlocksCard, dailyNrUnlocksFragment, "DailyNrUnlocksFragment");
        transaction.add(R.id.productivityFeelingCard, dailyProductivityFeelingFragment, "DailyProductivityFeelingFragment");

        transaction.commit();

        // Handle back arrow click
        ((MainActivity) getActivity()).setPreviousStatisticsFrag("DailyFragment");
        ImageView backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            final NavController navController = Navigation.findNavController(((AppCompatActivity) getContext()), R.id.fragment_container);
            navController.navigate(R.id.action_to_statistics);
        });

        return rootView;
    }

    public void setDayLabel() {
        String monthString = CalendarUtil.getMonth(selectedDay).toString();
        monthString = monthString.substring(0, 1).toUpperCase() + monthString.substring(1).toLowerCase();
        String dateString = CalendarUtil.getDayOfMonth(selectedDay) + " " + monthString + " " + CalendarUtil.getYear(selectedDay);
        dayTitleTextView.setText(dateString);
    }

}



