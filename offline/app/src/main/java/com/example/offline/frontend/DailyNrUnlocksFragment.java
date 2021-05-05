package com.example.offline.frontend;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.offline.R;
import com.example.offline.backend.day.CampusTime;
import com.example.offline.backend.day.Day;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyNrUnlocksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyNrUnlocksFragment extends Fragment {

    private TextView numberOfUnlocksValueTextView;
    private TextView timeSpentOnCampusTextView;
    private View currentView;
    Day selectedDay;

    public DailyNrUnlocksFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DailyNrUnlocksFragment newInstance(String param1, String param2) {
        DailyNrUnlocksFragment fragment = new DailyNrUnlocksFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        selectedDay = (Day) getArguments().getParcelable("selectedDay");
        currentView = inflater.inflate(R.layout.fragment_daily_nr_unlocks, container, false);

        // Set text of labels
        numberOfUnlocksValueTextView = (TextView) currentView.findViewById(R.id.NumberofUnlocksValue);
        timeSpentOnCampusTextView = (TextView) currentView.findViewById(R.id.NumberofUnlocksHoursOnCampus);
        setLabels();

        // Inflate the layout for this fragment
        return currentView;
    }

    public void setLabels() {
        // for now hardcoded values
        numberOfUnlocksValueTextView.setText(String.valueOf(selectedDay.getNumberOfUnlocks()));
        timeSpentOnCampusTextView.setText(getCampusTime());
    }

    public String getCampusTime() {
        Long totalCampusTime = calculateTimeOnCampus(selectedDay);

        Long minutesOnCampusCalculation = TimeUnit.MILLISECONDS.toMinutes(totalCampusTime);
        Long secondsOnCampusCalculation = TimeUnit.MILLISECONDS.toSeconds(totalCampusTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalCampusTime));

        String totalMinutesOnCampusFormattedValue = "";
        String totalSecondsOnCampusFormattedValue = "";

        String text = "";

        if (totalCampusTime > 0) {

            if (minutesOnCampusCalculation != 0) {

                if (minutesOnCampusCalculation > 1) {
                    // Format the time as > 1 minutes
                    totalMinutesOnCampusFormattedValue = String.format("%d minutes", minutesOnCampusCalculation);
                } else {
                    // Format the time as 1 minute
                    totalMinutesOnCampusFormattedValue = String.format("%d minute", minutesOnCampusCalculation);
                }

                if (secondsOnCampusCalculation > 1 || secondsOnCampusCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOnCampusFormattedValue = String.format("%d seconds", secondsOnCampusCalculation);
                } else {
                    // Format the time as 1 second
                    totalSecondsOnCampusFormattedValue = String.format("%d second", secondsOnCampusCalculation);
                }

            } else {

                if (secondsOnCampusCalculation > 1 || secondsOnCampusCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOnCampusFormattedValue = String.format("%d seconds", secondsOnCampusCalculation);
                } else {
                    // Format the time as 1 second
                    totalSecondsOnCampusFormattedValue = String.format("%d second", secondsOnCampusCalculation);
                }
            }

            // 0 mins > 0 seconds
            if (totalMinutesOnCampusFormattedValue == "" && totalSecondsOnCampusFormattedValue != "") {
                text = totalSecondsOnCampusFormattedValue;
            }
            // > 0 mins > 0 seconds
            else if (totalMinutesOnCampusFormattedValue != "" && totalMinutesOnCampusFormattedValue != "") {
                text = totalMinutesOnCampusFormattedValue + " " + totalSecondsOnCampusFormattedValue;
            }

        }

        return text;
    }

    private Long calculateTimeOnCampus(Day day) {
        Long totalCampusTime = 0L;

        if (day.getCampusTimes() != null) {
            for (CampusTime time : day.getCampusTimes()) {
                if (time.getLeaveTime() > time.getEnterTime()) {
                    Long campT = time.getLeaveTime() - time.getEnterTime();
                    totalCampusTime += campT;
                }
            }
        }

        return totalCampusTime;
    }

}