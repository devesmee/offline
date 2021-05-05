package com.example.offline.frontend;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.offline.IWhiteListAppSelectorInterface;
import com.example.offline.R;
import com.example.offline.WhiteListAppRecyclerViewAdapter;
import com.example.offline.backend.apps.SystemApp;
import com.example.offline.backend.apps.SystemManager;
import com.example.offline.backend.apps.WhitelistedAppsManager;

import java.util.List;

public class WhitelistedAppsFragment extends Fragment {

    private int mColumnCount = 1;
    List<SystemApp> installedApps;
    SystemManager systemManager;
    WhitelistedAppsManager whitelistedAppsManager;
    Button setWhitelistedAppsButton;

    public void setSelectedAppListener(IWhiteListAppSelectorInterface selectedAppListener) {
        this.selectedAppListener = selectedAppListener;
    }

    private IWhiteListAppSelectorInterface selectedAppListener;

    public WhitelistedAppsFragment() {
    }

    public static WhitelistedAppsFragment newInstance() {
        WhitelistedAppsFragment fragment = new WhitelistedAppsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whitelistedAppsManager = WhitelistedAppsManager.getInstance(getContext());

        // Get system apps
        systemManager = new SystemManager(getContext());
        installedApps = systemManager.getSystemApps();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_whitelisted_apps, container, false);
        WhiteListAppRecyclerViewAdapter whiteListAppAdapter = new WhiteListAppRecyclerViewAdapter(installedApps, getContext());

        Context context = view.getContext();

        RecyclerView recyclerView = view.findViewById(R.id.whiteListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(whiteListAppAdapter);

        //assign a listener
        whiteListAppAdapter.setSelectedAppListener(selectedAppListener);

        setWhitelistedAppsButton = (Button) view.findViewById(R.id.btn_set_whitelisted_apps);

        setWhitelistedAppsButton.setOnClickListener(click -> {
            whitelistedAppsManager.saveWhitelistedApps(whiteListAppAdapter.selectedApps);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment HomeFragment = new HomeFragment();
            fragmentTransaction.replace(R.id.fragment_container, HomeFragment);
            fragmentTransaction.commit();

        });

        return view;
    }
}