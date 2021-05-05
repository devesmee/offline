package com.example.offline;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.offline.backend.appusage.UnlockBroadcastReceiver;
import com.example.offline.backend.appusage.UsageStatsHelper;
import com.example.offline.backend.location.LocationManager;;
import com.example.offline.backend.notifications.NotificationManager;
import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;
import com.example.offline.frontend.HomeFragment;
import com.example.offline.frontend.SetGoalFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private NavController mNavController;
    private LocationManager mLocationManager;
    private String previousStatisticsFrag = "MonthlyFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        BroadcastReceiver unlocksBroadCastReceiver = new UnlockBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        this.registerReceiver(unlocksBroadCastReceiver, filter);
        mLocationManager = LocationManager.getInstance(this);
        askSystemPermissions(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_reward)
                .build();
        mNavController = Navigation.findNavController(this, R.id.fragment_container);

        Storage storage = new Storage(getApplicationContext());

        NavigationUI.setupWithNavController(bottomNav, mNavController);
        if (savedInstanceState == null) {
            if (storage.getInt(Key.GOAL_PERCENTAGE) == 0) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SetGoalFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.mLocationManager.startLocationServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        mNavController.navigateUp();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == LocationManager.PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.startLocationServices();
                }

            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    mLocationManager.onRequestPermissionRationale();
                    return;
                }
                mLocationManager.onLocationPermissionDeniedOnce();
            }
        }
    }

    /**
     * Ask all the necessary permissions that we need in the app
     *
     * @param context
     */
    private void askSystemPermissions(Context context) {
        UsageStatsHelper.askUsageStatsPermission(context);
        mLocationManager.requestPermissions();
        NotificationManager notificationManager = NotificationManager.getInstance(this);
        notificationManager.askForNotificationPermissions();
    }

    public void setPreviousStatisticsFrag(String prev) {
        this.previousStatisticsFrag = prev;
    }

    public String getPreviousStatisticsFrag() {
        return this.previousStatisticsFrag;
    }
}