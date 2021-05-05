package com.example.offline.backend.rewards;

import android.content.Context;

import com.example.offline.R;
import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;

import java.util.ArrayList;

public class RewardManager {

    private static RewardManager rewardManagerInstance;
    private Storage storage;
    private ArrayList<Reward> rewards;
    private ArrayList<Reward> unopenedRewards;

    private RewardManager(Context context) {
        this.storage = new Storage(context);
        this.rewards = getRewards();
        this.unopenedRewards = getUnopenedRewards();
    }

    /**
     * Get singleton instance of the RewardManager
     *
     * @param context
     * @return singleton instance
     */
    public static RewardManager getInstance(Context context) {
        if (rewardManagerInstance == null) {
            rewardManagerInstance = new RewardManager(context);
        }
        return rewardManagerInstance;
    }

    /**
     * Save the list of rewards in local storage
     */
    //IMPORTANT: to be used only if the onboarding is not complete
    public void initRewards() {
        this.rewards = new ArrayList<>();

        rewards.add(new Reward("Baby steps", "Me when I finally accomplish something at Fontys", 1, R.drawable.baby_steps));
        rewards.add(new Reward("Peppy", "When you are actually learning things at Fontys", 3, R.drawable.peppy));
        rewards.add(new Reward("It ain’t much but it’s honest work", "Me when I use my phone productively for 3 hours", 5, R.drawable.farmer_guy));
        rewards.add(new Reward("Yoda best", "Truly wonderful, the mind of a productive student is\n", 7, R.drawable.yoda));

        storage.saveListObject(Key.REWARDS, rewards);
    }

    /**
     * Returns the list of rewards
     *
     * @return ArrayList<Reward>
     */
    public ArrayList<Reward> getRewards() {
        this.rewards = new ArrayList<>();
        ArrayList<Object> rewardObjects = storage.getListObject(Key.REWARDS, Reward.class);
        for (Object object : rewardObjects) {
            this.rewards.add((Reward) object);
        }
        return this.rewards;
    }

    /**
     * Sets the isUnlocked property to true of a reward that the user can get when they have a certain number of achieved goals
     */
    public void unlockReward(int totalCompletedGoals) {
        for (Reward reward :
                this.rewards) {
            if (reward.getGoalCount() == totalCompletedGoals) {
                reward.setUnlocked(true);
                storage.saveListObject(Key.REWARDS, this.rewards);
                getRewards();
                this.unopenedRewards.add(reward);
            }
        }
    }

    /**
     * Returns the reward that has been unlocked but not opened yet
     *
     * @return Reward
     */
    public ArrayList<Reward> getUnopenedRewards() {
        ArrayList<Reward> unopenedRewards = new ArrayList<>();
        for (Reward reward :
                this.rewards) {
            if (reward.isUnlocked() && !reward.isOpened()) {
                unopenedRewards.add(reward);
            }
        }
        return unopenedRewards;
    }

    /**
     * Sets the isOpened property of a reward with the given rewardId to true
     */
    public void openReward(String rewardId) {
        for (Reward reward :
                this.rewards) {
            if (reward.getId().equals(rewardId)) {
                reward.setIsOpened(true);
                unopenedRewards.remove(reward);
            }
        }
        storage.saveListObject(Key.REWARDS, this.rewards);
        getRewards();
    }
}
