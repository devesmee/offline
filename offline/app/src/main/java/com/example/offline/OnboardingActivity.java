package com.example.offline;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;
import com.google.android.material.tabs.TabLayout;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager onboardingViewPager;
    TabLayout onBoardingTab;
    ConstraintLayout onboardingContainer;
    TextView getStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make onboarding activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_onboarding);

        onboardingContainer = findViewById(R.id.onboardingContainer);
        onboardingViewPager = findViewById(R.id.onBoarding_vp);
        onboardingViewPager.setAdapter(new OnboardingPageAdapter(getSupportFragmentManager(), 0));

        onBoardingTab = findViewById(R.id.onboarding_tab);
        onBoardingTab.setupWithViewPager(onboardingViewPager, true);
        getStarted = findViewById(R.id.getStartedButton);

        Storage storage = new Storage(this);

        // set onboarding to true and start MainActivity
        getStarted.setOnClickListener(v -> {
            storage.saveBoolean(Key.IS_ONBOARDING_COMPLETE, true);
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            OnboardingActivity.this.startActivity(intent);
            OnboardingActivity.this.finish();
            finish();
        });

        this.onboardingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        onboardingContainer.setBackgroundColor(ContextCompat.getColor(OnboardingActivity.this, R.color.onboardingPurple));
                        onBoardingTab.setBackgroundColor(ContextCompat.getColor(OnboardingActivity.this, R.color.onboardingPurple));
                        getStarted.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        onboardingContainer.setBackgroundColor(ContextCompat.getColor(OnboardingActivity.this, R.color.onboardingMint));
                        onBoardingTab.setBackgroundColor(ContextCompat.getColor(OnboardingActivity.this, R.color.onboardingMint));
                        getStarted.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        onboardingContainer.setBackgroundColor(ContextCompat.getColor(OnboardingActivity.this, R.color.onboardingYellow));
                        onBoardingTab.setBackgroundColor(ContextCompat.getColor(OnboardingActivity.this, R.color.onboardingYellow));
                        getStarted.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
