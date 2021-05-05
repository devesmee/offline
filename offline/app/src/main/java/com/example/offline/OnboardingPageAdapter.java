package com.example.offline;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.offline.frontend.onboarding.FirstOnboardingScreenFragment;
import com.example.offline.frontend.onboarding.SecondOnboardingScreenFragment;
import com.example.offline.frontend.onboarding.ThirdOnboardingScreenFragment;

public class OnboardingPageAdapter extends FragmentPagerAdapter {

    public OnboardingPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FirstOnboardingScreenFragment();
            case 1:
                return new SecondOnboardingScreenFragment();
            case 2:
                return new ThirdOnboardingScreenFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}