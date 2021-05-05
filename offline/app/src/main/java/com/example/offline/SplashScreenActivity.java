package com.example.offline;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    Intent appIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            Storage storage = new Storage(this);
            // check if onboarding is complete
            Boolean isOnboardingComplete = storage.getBoolean(Key.IS_ONBOARDING_COMPLETE);
            // determine which activity to launch
            if (isOnboardingComplete) {
                appIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            } else {
                appIntent = new Intent(SplashScreenActivity.this, OnboardingActivity.class);
            }
            SplashScreenActivity.this.startActivity(appIntent);
            SplashScreenActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}