package com.example.offline.backend.goal;

import android.content.Context;

import com.example.offline.backend.rewards.RewardManager;
import com.example.offline.backend.storage.Key;
import com.example.offline.backend.storage.Storage;

import java.util.ArrayList;

public class GoalManager {

    private ArrayList<Goal> goals;
    private Goal activeGoal;
    private int nrOfGoalsCompleted;
    private Storage storage;
    private static GoalManager goalManagerInstance;
    private static RewardManager rewardManager;

    private GoalManager(Context context) {
        this.storage = new Storage(context);
        this.activeGoal = getActiveGoal();
        this.goals = getAllGoals();
        this.nrOfGoalsCompleted = getNrOfGoalsCompleted();
        rewardManager = RewardManager.getInstance(context);
    }

    /**
     * Get singleton instance of the GoalManager
     *
     * @param context
     * @return singletone instance
     */
    public static GoalManager getInstance(Context context) {
        if (goalManagerInstance == null) {
            goalManagerInstance = new GoalManager(context);
        }
        return goalManagerInstance;
    }

    /**
     * Gets the current active goal from local storage
     *
     * @return the current active
     */
    public Goal getActiveGoal() {
        this.activeGoal = storage.getObject(Key.ACTIVE_GOAL, Goal.class);
        return this.activeGoal;
    }

    /**
     * Sets the active goal in local storage
     *
     * @param goal to be set
     */
    public void setActiveGoal(Goal goal) {
        this.activeGoal = goal;
        this.storage.saveObject(Key.ACTIVE_GOAL, goal);
    }

    /**
     * Adds a goal to the list and saves it in the local storage
     *
     * @param goal added to the list
     */
    public void addGoalToList(Goal goal) {
        this.goals.add(goal);
        this.storage.saveListObject(Key.ALL_GOALS, goals);
    }

    /**
     * Get all goals from local storage
     *
     * @return list of all goals
     */
    public ArrayList<Goal> getAllGoals() {
        this.goals = new ArrayList<>();
        ArrayList<Object> goalObjects = storage.getListObject(Key.ALL_GOALS, Goal.class);
        for (Object object : goalObjects) {
            this.goals.add((Goal) object);
        }
        return this.goals;
    }

    /**
     * Check if a goal is accomplished
     *
     * @param goal to be checked
     * @return true or false
     */
    public boolean isGoalAccomplished(Goal goal) {
        return goal.getProgress() >= 100;
    }

    /**
     * Gets number of goals completed from local storage
     *
     * @return number of goals completed
     */
    public int getNrOfGoalsCompleted() {
        this.nrOfGoalsCompleted = storage.getInt(Key.NR_OF_GOALS);
        return nrOfGoalsCompleted;
    }

    /**
     * Save total number of goals completed in the local storage
     *
     * @param nrOfGoalsCompleted to be saved
     */
    public void setNrOfGoalsCompleted(int nrOfGoalsCompleted) {
        this.storage.saveInt(Key.NR_OF_GOALS, nrOfGoalsCompleted);
    }

}

