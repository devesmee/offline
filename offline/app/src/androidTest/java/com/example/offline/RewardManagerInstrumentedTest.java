package com.example.offline;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.offline.backend.rewards.Reward;
import com.example.offline.backend.rewards.RewardManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RewardManagerInstrumentedTest {

    // Context of the app under test.
    Context appContext;
    RewardManager rewardManager;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        rewardManager = RewardManager.getInstance(appContext);
    }

    @Test
    public void getRewards_shouldReturnRewardsFromStorage() {
        //arrange
        ArrayList<Reward> expectedRewards = new ArrayList<>();

        expectedRewards.add(new Reward("Baby steps", "Me when I finally accomplish something at Fontys", 1, R.drawable.baby_steps));
        expectedRewards.add(new Reward("Peppy", "When you are actually learning things at Fontys", 10, R.drawable.peppy));
        expectedRewards.add(new Reward("It ain’t much but it’s honest work", "Me when I use my phone productively for 3 hours", 20, R.drawable.farmer_guy));
        expectedRewards.add(new Reward("Yoda best", "Truly wonderful, the mind of a productive student is\n", 30, R.drawable.yoda));

        RewardManager rewardManager = RewardManager.getInstance(appContext);
        rewardManager.initRewards();

        //act
        ArrayList<Reward> actualRewards = rewardManager.getRewards();

        //assert
        assertEquals(expectedRewards, actualRewards);
    }

    @Test
    public void unlockReward_shouldIncreaseUnopenedRewardsSize() {
        //arrange
        RewardManager.getInstance(appContext);
        int expectedResult = 1;
        rewardManager.initRewards();

        //act
        rewardManager.unlockReward(10);
        int actualResult = rewardManager.getUnopenedRewards().size();

        //assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void openReward_shouldDecreaseUnopenedRewardsSize() {
        //arrange
        int expectedResult = 0;
        rewardManager.initRewards();
        rewardManager.unlockReward(10);
        ArrayList<Reward> unopenedRewards = rewardManager.getUnopenedRewards();

        //act
        rewardManager.openReward(unopenedRewards.get(0).getId());
        int actualResult = rewardManager.getUnopenedRewards().size();

        //assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getUnopenedRewards_shouldReturnUnopenedRewards() {
        //arrange
        int expectedResult = 1;
        rewardManager.initRewards();
        rewardManager.unlockReward(10);

        //act
        int actualResult = rewardManager.getUnopenedRewards().size();

        //assert
        assertEquals(expectedResult, actualResult);
    }
}