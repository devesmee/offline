package com.example.offline;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class WeeksPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private final List<Fragment> fragmentList = new ArrayList<>();

    public WeeksPagerAdapter(@NonNull FragmentManager fm, int behaviour, Context context) {
        super(fm, behaviour);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }
}
