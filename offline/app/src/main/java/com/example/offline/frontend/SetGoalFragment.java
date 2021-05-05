package com.example.offline.frontend;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.offline.R;
import com.example.offline.backend.apps.WhitelistedAppsManager;
import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.goal.GoalManager;
import com.example.offline.backend.productivitytracker.ProductivityManager;
import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetGoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetGoalFragment extends Fragment {

    GoalManager goalManager;
    DayManager dayManager;
    WhitelistedAppsManager whitelistedAppsManager;
    SeekBar goalSeekbar;
    View currentView;
    TextView goalPercentageTextView;
    TextView infoGoalTextView;
    Button saveGoal;
    Storage storage;
    ProductivityManager managerProductivityMood;

    public SetGoalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        goalManager = GoalManager.getInstance(activity);
        dayManager = DayManager.getInstance(activity);
        whitelistedAppsManager = WhitelistedAppsManager.getInstance(activity);
    }

    public static SetGoalFragment newInstance() {
        SetGoalFragment fragment = new SetGoalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = new Storage(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentView = inflater.inflate(R.layout.fragment_set_goal, container, false);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
        goalSeekbar = (SeekBar) currentView.findViewById(R.id.seekBar);
        goalPercentageTextView = (TextView) currentView.findViewById(R.id.goalPercentageTextView);
        infoGoalTextView = (TextView) currentView.findViewById(R.id.textViewInfoGoal);
        saveGoal = (Button) currentView.findViewById(R.id.buttonSaveGoal);

        saveGoal.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment WhitelistedAppsFragment = new WhitelistedAppsFragment();
            fragmentTransaction.replace(R.id.fragment_container, WhitelistedAppsFragment);
            fragmentTransaction.commit();

            saveGoal();
        });

        goalSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 5;
                progress = progress * 5;
                String goalString = String.valueOf(progress) + "%";
                goalPercentageTextView.setText(goalString);
                goalSeekbar.setProgress(progress);
                Double totalMinutes = (double) progress / 100 * 8 * 60;
                Double leftOverMinutes = totalMinutes % 60;
                Double hours = (totalMinutes - leftOverMinutes) / 60;

                String goalInfoString = "On an average day of 8 hours, " + progress + "% is " + hours.intValue() + " hours " + leftOverMinutes.intValue() + " minutes staying off your phone";
                infoGoalTextView.setText(goalInfoString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Inflate the layout for this fragment
        return currentView;
    }

    public void saveGoal() {
        storage.saveInt(Key.GOAL_PERCENTAGE, goalSeekbar.getProgress());

        if (dayManager.getCurrentDay().getGoal().getPercentage() == 0) {
            dayManager.setGoalPercentage(storage.getInt(Key.GOAL_PERCENTAGE));
        }

        //Move the following lines to the point where we are going to save the whitelisted apps after selecting them
        ArrayList<String> whitelistedAppsPackageNames = new ArrayList<>();
        whitelistedAppsPackageNames.add("outlook");
        whitelistedAppsPackageNames.add("slack");

        whitelistedAppsManager.saveWhitelistedApps(whitelistedAppsPackageNames);
    }
}