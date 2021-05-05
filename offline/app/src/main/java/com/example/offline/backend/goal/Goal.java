package com.example.offline.backend.goal;

import android.text.format.DateUtils;

import androidx.annotation.Nullable;

import java.util.UUID;

public class Goal {

    private int percentage;
    private double timeInCampus;
    private double progress;
    private Long totalOffScreenTime;
    private long timestamp;
    private String goalId;

    public void setTotalOffScreenTime(Long totalOffScreenTime) {
        this.totalOffScreenTime = totalOffScreenTime;
    }

    public Goal(int percentage, long timestamp) {
        this.percentage = percentage;
        this.timestamp = timestamp;
        this.goalId = UUID.randomUUID().toString();
        this.totalOffScreenTime = 0L;
    }

    /**
     * Empty constructor needed for JSON serialization
     */
    public Goal() {
    }

    /**
     * @return goal percentage that needs to bee achieved
     */
    public int getPercentage() {
        return percentage;
    }

    /**
     * Set the time percentage of the goal
     *
     * @param percentage to be set
     */
    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    /**
     * Set the total time in campus for the goal
     *
     * @param timeInCampus to be set
     */
    public void setTimeInCampus(double timeInCampus) {
        this.timeInCampus = timeInCampus;
    }

    /**
     * Check if goal is active using the timestamp when the goal was created
     *
     * @return
     */
    public boolean isActive() {
        return DateUtils.isToday(this.timestamp);
    }

    /**
     * @return the total time progress of the goal based on the percentage
     */
    public double getProgress() {
        return this.progress;
    }

    /**
     * Calculates and sets the progress of a goal as a percentage
     *
     * @param goalProgress
     */
    public void setProgress(double goalProgress) {
        this.progress = goalProgress;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Goal goalToCompare = (Goal) obj;
        if (goalToCompare.goalId.equalsIgnoreCase(this.goalId)) {
            return true;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Goal{" +
                "percentage=" + percentage +
                ", timeInCampus=" + timeInCampus +
                ", progress=" + progress +
                ", timestamp=" + timestamp +
                ", off screen=" + totalOffScreenTime +
                ", goalId='" + goalId + '\'' +
                '}';
    }
}
