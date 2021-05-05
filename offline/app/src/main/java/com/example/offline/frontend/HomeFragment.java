package com.example.offline.frontend;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.offline.MainActivity;
import com.example.offline.R;
import com.example.offline.backend.Util;
import com.example.offline.backend.day.CampusTime;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.day.DayManager;
import com.example.offline.backend.day.ProductivityFeeling;
import com.example.offline.backend.goal.Goal;
import com.example.offline.backend.goal.GoalManager;
import com.example.offline.backend.notifications.NotificationBroadcastReceiver;
import com.example.offline.backend.notifications.NotificationHelper;
import com.example.offline.backend.productivitytracker.ProductivityManager;
import com.example.offline.backend.rewards.Reward;
import com.example.offline.backend.rewards.RewardManager;
import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;
import com.example.offline.service.ScreenTimeTrackingService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private GoalManager goalManager;
    private TextView setGoalTextView;
    private TextView timeInCampusTextView;
    private TextView averageProductivityFeelingTextView;
    private MaterialCardView inputProductivityFeelingCardView;
    private MaterialCardView averageProductivityFeelingCardView;
    private ImageView averageProductivityFeelingImageView;
    private ImageView productivityFeelingInputSad;
    private ImageView productivityFeelingInputNeutral;
    private ImageView productivityFeelingInputHappy;
    private Button screentimeTrackButton;
    private Button endDayButton;
    private View currentView;
    private RewardManager rewardManager;
    private DayManager dayManager;
    private ArrayList<Reward> unopenedRewards;
    private LottieAnimationView avToolbarPresent;
    private Storage storage;
    private PieChart pieChart;
    private ProductivityManager managerProductivityMood;
    private ProductivityFeeling averageProductivityMood;
    private ScreenTimeTrackingService screenTimeTrackingService;
    private NotificationHelper notificationHelper;
    private NotificationBroadcastReceiver notificationBroadcastReceiver;
    private ScrollView scrollView;


    private Long timeOnCampus = 0L;
    private Long timeOffScreen = 0L;
    private double testProgress = 0L;
    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = new Storage(getContext());
        goalManager = GoalManager.getInstance(getContext());
        dayManager = DayManager.getInstance(getContext());
        notificationHelper = NotificationHelper.getInstance(getContext());
        notificationBroadcastReceiver = new NotificationBroadcastReceiver();

        // Check if a Day instance for the current has been created
        if (dayManager.getCurrentDay() == null) {
            Day today = new Day(new Goal(storage.getInt(Key.GOAL_PERCENTAGE), new Date().getTime()));
            dayManager.addNewDay(today);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_home, container, false);
        screentimeTrackButton = (Button) currentView.findViewById(R.id.screentimeTrackButton);
        endDayButton = (Button) currentView.findViewById(R.id.btn_end_day);
        screentimeTrackButton.setVisibility(View.GONE);
        screenTimeTrackingService = new ScreenTimeTrackingService();
        swipeRefreshLayout = currentView.findViewById(R.id.swiperefresh);
        scrollView = currentView.findViewById(R.id.scrollview);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                timeOnCampus = 0L;
                timeOffScreen = 0L;
                testProgress = 0L;
                if (dayManager.getCurrentDay().getCampusTimes().size() != 0) {
                    getScreenTimeForPreviousCampusTimes();

                    if (dayManager.getCurrentDay().getCampusTimes().get(dayManager.getCurrentDay().getCampusTimes().size() - 1).getLeaveTime() == 0) {
                        getScreenTimeForCurrentCampusPeriod(getContext(), 0L);
                    }
                }
                if (storage.getBoolean(Key.IS_TRACKING)) {
//            screentimeTrackButton.setText(R.string.leave_campus_button);
                    endDayButton.setEnabled(false);
                } else {
//            screentimeTrackButton.setText(R.string.enter_campus_button);
                    endDayButton.setEnabled(true);
                }
                showTodayProgress();
                scrollView.invalidate();
                scrollView.requestLayout();
            }
        });

        // Set text of labels
        setGoalTextView = (TextView) currentView.findViewById(R.id.SetGoalPercentage);
        timeInCampusTextView = (TextView) currentView.findViewById(R.id.TimeInformation);

        if (dayManager.getCurrentDay().isEnded()) {
            // day has already ended
            // Update circle
            testProgress = dayManager.getCurrentDay().getGoal().getProgress();
            showTodayProgress();
            endDayButton.setVisibility(View.GONE);
//           screentimeTrackButton.setVisibility(View.GONE);
        }

        // CAN BE REMOVED ONCE BUG IS FIXED
        if (!(dayManager.getCurrentDay().getProductivityFeeling() == ProductivityFeeling.NONE)) {
            endDayButton.setVisibility(View.GONE);
            screentimeTrackButton.setVisibility(View.GONE);
        }

        if (storage.getBoolean(Key.IS_TRACKING)) {
//            screentimeTrackButton.setText(R.string.leave_campus_button);
            endDayButton.setEnabled(false);
        } else {
//            screentimeTrackButton.setText(R.string.enter_campus_button);
            endDayButton.setEnabled(true);
        }

        endDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDay();
            }
        });

        rewardManager = RewardManager.getInstance(getContext());
        if (rewardManager.getRewards().size() == 0) {
            rewardManager.initRewards();
        }

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        unopenedRewards = rewardManager.getUnopenedRewards();

        if (dayManager.getCurrentDay().getCampusTimes().size() != 0) {
            getScreenTimeForPreviousCampusTimes();

            if (dayManager.getCurrentDay().getCampusTimes().get(dayManager.getCurrentDay().getCampusTimes().size() - 1).getLeaveTime() == 0) {
                getScreenTimeForCurrentCampusPeriod(getContext(), 0L);
            }
        }

        // Initialize Productivity Manager
        managerProductivityMood = ProductivityManager.getInstance(getContext());

        showTodayProgress();

        // Show correct card
        averageProductivityFeelingCardView = (MaterialCardView) currentView.findViewById(R.id.homeCardProductivityTrackerOverview);
        inputProductivityFeelingCardView = (MaterialCardView) currentView.findViewById(R.id.homeCardProductivityTrackerInput);
        setProductivityFeelingCard(dayManager.getCurrentDay().getAwaitingProductivityInput());

        avToolbarPresent = (LottieAnimationView) currentView.findViewById(R.id.av_unwrap_present_in_toolbar);
        avToolbarPresent.setVisibility(View.INVISIBLE);
        if (unopenedRewards.size() > 0) {
            avToolbarPresent.setVisibility(View.VISIBLE);

            avToolbarPresent.setOnClickListener(v -> {
                buildPresentDialogs(unopenedRewards.get(0));
            });
        }

        // Inflate the layout for this fragment
        return currentView;
    }

    public void showTodayProgress() {
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();

        if (testProgress > 100) {
            // When the progress is over 100%, it will show the full circle
            pieEntries.add(new PieEntry((float) 100));
        } else {
            if (testProgress < 0) {
                pieEntries.add(new PieEntry((float) 0));
            } else {
                pieEntries.add(new PieEntry((float) testProgress));
            }
            pieEntries.add(new PieEntry((float) (100 - testProgress)));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");

        // To determine what colour the progress should be
        int progressColor;
        TextView title = (TextView) currentView.findViewById(R.id.TodayProgressTitle);

        if (testProgress == 100) {
            progressColor = ContextCompat.getColor(getActivity(), R.color.blue_light);
            title.setText(R.string.home_goal_completed);
        } else if (testProgress >= dayManager.getCurrentDay().getGoal().getPercentage()) {
            progressColor = ContextCompat.getColor(getActivity(), R.color.blue_light);
            title.setText(R.string.home_on_track_progress);
        } else {
            progressColor = ContextCompat.getColor(getActivity(), R.color.orange_bright);
            title.setText(R.string.home_off_track_progress);
        }

        Long minutesOnCampusCalculation = TimeUnit.MILLISECONDS.toMinutes(timeOnCampus);
        Long secondsOnCampusCalculation = TimeUnit.MILLISECONDS.toSeconds(timeOnCampus) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeOnCampus));

        Long minutesOffScreenCalculation = TimeUnit.MILLISECONDS.toMinutes(timeOffScreen);
        Long secondsOffScreenCalculation = TimeUnit.MILLISECONDS.toSeconds(timeOffScreen) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeOffScreen));

        String totalMinutesOffScreenFormattedValue = "";
        String totalSecondsOffScreenFormattedValue = "";

        String totalMinutesOnCampusFormattedValue = "";
        String totalSecondsOnCampusFormattedValue = "";

        if (timeOnCampus > 0) {
            if (minutesOffScreenCalculation != 0) {

                if (minutesOffScreenCalculation > 1) {
                    // Format the time as > 1 minutes
                    totalMinutesOffScreenFormattedValue = String.format("%d minutes", minutesOffScreenCalculation);
                } else {
                    // Format the time as 1 minute
                    totalMinutesOffScreenFormattedValue = String.format("%d minute", minutesOffScreenCalculation);
                }

                if (secondsOffScreenCalculation > 1 || secondsOffScreenCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d seconds", secondsOffScreenCalculation);
                } else {
                    // Format the time as 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d second", secondsOffScreenCalculation);
                }
            } else {

                if (secondsOffScreenCalculation > 1 || secondsOffScreenCalculation == 0) {
                    // Format the time as > 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d seconds", secondsOffScreenCalculation);
                } else {
                    // Format the time as 1 seconds
                    totalSecondsOffScreenFormattedValue = String.format("%d second", secondsOffScreenCalculation);
                }
            }

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

            // 0 mins off screen > 0 mins off campus
            if (totalMinutesOffScreenFormattedValue == "" && totalMinutesOnCampusFormattedValue != "") {
                timeInCampusTextView.setText("You were off screen for " + totalSecondsOffScreenFormattedValue +
                        " out of " + totalMinutesOnCampusFormattedValue + " " + totalSecondsOnCampusFormattedValue + " spent on campus");
            }
            // 0 mins off screen 0 mins on campus
            else if (totalMinutesOffScreenFormattedValue == "" && totalMinutesOnCampusFormattedValue == "") {
                timeInCampusTextView.setText("You were off screen for " + totalSecondsOffScreenFormattedValue +
                        " out of " + totalSecondsOnCampusFormattedValue + " spent on campus");
            }
            // > 0 mins off screen > 0 mins on campus
            else if (totalMinutesOffScreenFormattedValue != "" && totalMinutesOnCampusFormattedValue != "") {
                timeInCampusTextView.setText("You were off screen for " + totalMinutesOffScreenFormattedValue + " " + totalSecondsOffScreenFormattedValue +
                        " out of " + totalMinutesOnCampusFormattedValue + " " + totalSecondsOnCampusFormattedValue + " spent on campus");
            }

        } else {
            timeInCampusTextView.setText("No time has been spent on campus today");
        }

        setGoalTextView.setText(dayManager.getCurrentDay().getGoal().getPercentage() + "% off screen");
        pieDataSet.setColors(progressColor, Color.LTGRAY);

        PieData pieData = new PieData(pieDataSet);

        // Set up the pie chart
        pieChart = (PieChart) currentView.findViewById(R.id.GoalProgressCircle);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setCenterText(setPieChartCenterText((int) testProgress));
        pieChart.setCenterTextColor(Color.GRAY);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setCenterTextSize(24);
        pieChart.setHoleRadius(80f);
        pieChart.setDrawRoundedSlices(true);
        pieChart.getData().setDrawValues(false);
        pieChart.invalidate();

        timeInCampusTextView.invalidate();
        swipeRefreshLayout.setRefreshing(false);
    }

    private String setPieChartCenterText(int todaysProgress) {
        return todaysProgress + "% \n off screen";
    }

    @Override
    public void onResume() {
        super.onResume();
        if (storage.getBoolean(Key.IS_TRACKING)) {
            this.updateGoalProgress();
        }
        if (!dayManager.getCurrentDay().getDate().equals(Util.getCurrentDate())) {
            startNewDay();
        }
    }

    private void buildPresentDialogs(Reward reward) {

        // build the unwrap reward dialog
        AlertDialog.Builder unwrapPresentDialogBuilder = new AlertDialog.Builder(getContext());
        View viewUnwrapPresentDialog = getLayoutInflater().inflate(R.layout.dialog_unwrap_present, null);
        LottieAnimationView avDialogPresent = (LottieAnimationView) viewUnwrapPresentDialog.findViewById(R.id.av_present_to_unwrap);

        unwrapPresentDialogBuilder.setView(viewUnwrapPresentDialog);
        AlertDialog unwrapPresentDialog = unwrapPresentDialogBuilder.create();
        unwrapPresentDialog.show();

        // open dialog with revealed reward when the present animation is pressed
        avDialogPresent.setOnClickListener(a -> {
            unwrapPresentDialog.dismiss();
            // build the reveal reward dialog
            AlertDialog.Builder revealPresentDialogBuilder = new AlertDialog.Builder(getContext());
            View viewRevealPresentDialog = getLayoutInflater().inflate(R.layout.dialog_reveal_present, null);

            // find text views and image view which describe the reward
            ImageView ivStickerImage = (ImageView) viewRevealPresentDialog.findViewById(R.id.iv_revealed_sticker);
            TextView tvStickerName = (TextView) viewRevealPresentDialog.findViewById(R.id.tv_revealed_sticker_name);
            TextView tvStickerDescription = (TextView) viewRevealPresentDialog.findViewById(R.id.tv_revealed_sticker_meme);

            // set the reward image, name and description values dynamically based on the earned reward
            ivStickerImage.setImageResource(reward.getResourceId());
            tvStickerName.setText(reward.getName());
            tvStickerDescription.setText(reward.getDescription());

            Button btnClaimSticker = (Button) viewRevealPresentDialog.findViewById(R.id.btn_claim_sticker);
            revealPresentDialogBuilder.setView(viewRevealPresentDialog);
            AlertDialog revealPresentDialog = revealPresentDialogBuilder.create();
            revealPresentDialog.show();

            btnClaimSticker.setOnClickListener(b -> {

                revealPresentDialog.dismiss();

                rewardManager.openReward(reward.getId());
                unopenedRewards = rewardManager.getUnopenedRewards();

                if (unopenedRewards.size() == 0) {
                    avToolbarPresent.setVisibility(View.GONE);
                }
            });
        });
    }

    private void updateGoalProgress() {
        // update UI
        showTodayProgress();
    }

    public Long getScreenTimeForCurrentCampusPeriod(Context context, Long campusLeaveTime) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        long campusEnter = dayManager.getCurrentDay().getCampusTimes().get(dayManager.getCurrentDay().getCampusTimes().size() - 1).getEnterTime(); //campus enter time
        long campusLeave = campusLeaveTime == 0 ? System.currentTimeMillis() : campusLeaveTime; //campus leave time

        UsageEvents usageEvents = usageStatsManager.queryEvents(campusEnter, campusLeave);

        long timeScreenActive = 0;
        long startTime = campusEnter;
        long endTime = 0;

        UsageEvents.Event e = new UsageEvents.Event();
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(e);
            if (e.getEventType() == UsageEvents.Event.SCREEN_INTERACTIVE) {
                startTime = e.getTimeStamp();
            } else if (e.getEventType() == UsageEvents.Event.SCREEN_NON_INTERACTIVE || e.getEventType() == UsageEvents.Event.DEVICE_SHUTDOWN) {
                endTime = e.getTimeStamp();
            }

            if (startTime > 0 && endTime > 0 && endTime > startTime) {
                timeScreenActive += (endTime - startTime);
                startTime = 0;
                endTime = 0;
            }
        }

        endTime = campusLeaveTime == 0 ? System.currentTimeMillis() : campusLeaveTime;
        timeScreenActive += (endTime - startTime);

        Long totalCampusTime = campusLeave - campusEnter;
        Long totalOffScreenTime = totalCampusTime - timeScreenActive;

        timeOffScreen += totalOffScreenTime;
        timeOnCampus += totalCampusTime;

        if (timeOffScreen == 0) {
            testProgress = 0;
        } else {
            testProgress = Math.round(((double) timeOffScreen / (double) timeOnCampus) * 100);
            System.out.println(totalOffScreenTime);
            System.out.println(totalCampusTime);
            System.out.println(testProgress);
        }
        return timeScreenActive;
    }

    private void setProductivityFeelingCard(boolean awaitingProductivityInput) {
        if (awaitingProductivityInput) { // to show input productivity feeling card
            averageProductivityFeelingCardView.setVisibility(View.INVISIBLE);
            inputProductivityFeelingCardView.setVisibility(View.VISIBLE);
            productivityFeelingInputSad = (ImageView) currentView.findViewById(R.id.ProductivityFeelingEmojiSad);
            productivityFeelingInputNeutral = (ImageView) currentView.findViewById(R.id.ProductivityFeelingEmojiNeutral);
            productivityFeelingInputHappy = (ImageView) currentView.findViewById(R.id.ProductivityFeelingEmojiHappy);
            setOnClickListenersInputProductivityFeeling();
        } else { // to show average productivity feeling card
            inputProductivityFeelingCardView.setVisibility(View.INVISIBLE);
            averageProductivityFeelingCardView.setVisibility(View.VISIBLE);
            if (endDayButton.VISIBLE == 0) {
                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) averageProductivityFeelingCardView.getLayoutParams();
                newLayoutParams.topMargin = 92;
                averageProductivityFeelingCardView.setLayoutParams(newLayoutParams);
            }

            averageProductivityFeelingTextView = (TextView) currentView.findViewById(R.id.ProductivityFeelingAverage);
            averageProductivityFeelingImageView = (ImageView) currentView.findViewById(R.id.ProductivityFeelingEmoji);
            setAverageProductivityFeeling();
        }
    }

    public void setAverageProductivityFeeling() {
        averageProductivityMood = managerProductivityMood.getAverage();
        switch (averageProductivityMood) {
            case SAD:
                averageProductivityFeelingTextView.setText(R.string.home_productivity_feeling_average_sad);
                averageProductivityFeelingImageView.setImageResource(R.drawable.sad);
                break;
            case NEUTRAL:
                averageProductivityFeelingTextView.setText(R.string.home_productivity_feeling_average_neutral);
                averageProductivityFeelingImageView.setImageResource(R.drawable.neutral);
                break;
            case HAPPY:
                averageProductivityFeelingTextView.setText(R.string.home_productivity_feeling_average_happy);
                averageProductivityFeelingImageView.setImageResource(R.drawable.happy);
                break;
        }
    }

    private void setOnClickListenersInputProductivityFeeling() {
        productivityFeelingInputSad.setOnClickListener(v -> {
            dayManager.saveProductivityFeeling(ProductivityFeeling.SAD);
            dayManager.saveAwaitingProductivityFeeling(false);
            setProductivityFeelingCard(dayManager.getCurrentDay().getAwaitingProductivityInput());
            notificationHelper.cancelNotification();
        });
        productivityFeelingInputNeutral.setOnClickListener(v -> {
            dayManager.saveProductivityFeeling(ProductivityFeeling.NEUTRAL);
            dayManager.saveAwaitingProductivityFeeling(false);
            setProductivityFeelingCard(dayManager.getCurrentDay().getAwaitingProductivityInput());
            notificationHelper.cancelNotification();
        });
        productivityFeelingInputHappy.setOnClickListener(v -> {
            dayManager.saveProductivityFeeling(ProductivityFeeling.HAPPY);
            dayManager.saveAwaitingProductivityFeeling(false);
            setProductivityFeelingCard(dayManager.getCurrentDay().getAwaitingProductivityInput());
            notificationHelper.cancelNotification();
        });
    }

    private void getScreenTimeForPreviousCampusTimes() {
        for (CampusTime campusTime : dayManager.getCurrentDay().getCampusTimes()) {
            if (campusTime.getLeaveTime() != 0) {
                Long currentOnCampusTime = campusTime.getLeaveTime() - campusTime.getEnterTime();
                timeOnCampus += currentOnCampusTime;
                Long currentTimeOffScreen = currentOnCampusTime - campusTime.getOnScreenTime();
                timeOffScreen += currentTimeOffScreen;

                double targetedGoalTime = (timeOnCampus * dayManager.getCurrentDay().getGoal().getPercentage()) / 100.0;

                if (timeOffScreen == 0) {
                    testProgress = 0;
                } else {
                    // testProgress changes value here
                    testProgress = Math.round(((double) timeOffScreen / (double) timeOnCampus) * 100);
                }
            }
        }
    }

    public void startNewDay() {

        if (storage.getBoolean(Key.IS_TRACKING)) {
            storage.saveBoolean(Key.IS_TRACKING, false);
        }

        Day today = new Day(new Goal(storage.getInt(Key.GOAL_PERCENTAGE), new Date().getTime()));
        dayManager.addNewDay(today);
    }

    //the day can be ended only if there is no active campus time
    public void endDay() {
        dayManager.endDay(Math.round(testProgress), timeOffScreen, timeOnCampus);
        if (testProgress >= dayManager.getCurrentDay().getGoal().getPercentage()) {
            int numberOfGoalsCompleted = goalManager.getNrOfGoalsCompleted();
            numberOfGoalsCompleted++;
            goalManager.setNrOfGoalsCompleted(numberOfGoalsCompleted);
            rewardManager.unlockReward(numberOfGoalsCompleted);

            unopenedRewards = rewardManager.getUnopenedRewards();
            if (unopenedRewards.size() > 0) {
                avToolbarPresent.setVisibility(View.VISIBLE);

                avToolbarPresent.setOnClickListener(v -> {
                    buildPresentDialogs(unopenedRewards.get(0));
                });
            }
        }
        endDayButton.setVisibility(View.GONE);
//        screentimeTrackButton.setVisibility(View.GONE);
        // to give the users the ability to input their productivity feeling
        dayManager.saveAwaitingProductivityFeeling(true);
        setProductivityFeelingCard(dayManager.getCurrentDay().getAwaitingProductivityInput());
        notificationHelper.sendNotification("Productivity Feeling", "How do you feel about your productivity today?", MainActivity.class);

        // Update the progress circle value
        testProgress = dayManager.getCurrentDay().getGoal().getProgress();
        showTodayProgress();
    }
}