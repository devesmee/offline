package com.example.offline.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.offline.R;
import com.example.offline.StickersAdapter;
import com.example.offline.backend.goal.GoalManager;
import com.example.offline.backend.rewards.RewardManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class RewardsFragment extends Fragment {

    RewardManager rewardManager = RewardManager.getInstance(getContext());
    GoalManager goalManager = GoalManager.getInstance(getContext());
    ArrayList rewards;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rewards, container, false);

        rewards = rewardManager.getRewards();
        GridView gridView = (GridView) rootView.findViewById(R.id.rewardsGridview);
        StickersAdapter stickersAdapter = new StickersAdapter(getContext(), rewards, goalManager.getNrOfGoalsCompleted());
        gridView.setAdapter(stickersAdapter);

        TextView goalsCompletedView = (TextView) rootView.findViewById(R.id.goalsCompletedValue);
        goalsCompletedView.setText(String.valueOf(goalManager.getNrOfGoalsCompleted()));

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        return rootView;
    }
}


