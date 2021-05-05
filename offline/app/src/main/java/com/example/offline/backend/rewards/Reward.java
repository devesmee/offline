package com.example.offline.backend.rewards;

import androidx.annotation.Nullable;

import java.util.UUID;

public class Reward {
    private String id;
    private String name;
    private String description;
    private int goalCount;
    private int resourceId;
    private boolean isUnlocked;
    private boolean isOpened;

    public void setName(String name) {
        this.name = name;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public void setIsOpened(boolean opened) {
        isOpened = opened;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getGoalCount() {
        return goalCount;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Reward rewardToCompare = (Reward) obj;
        if (rewardToCompare.getName().equalsIgnoreCase(this.getName())) {
            return true;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Reward{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", goalCount=" + goalCount +
                ", resourceId=" + resourceId +
                ", isUnlocked=" + isUnlocked +
                ", isOpened=" + isOpened +
                '}';
    }

    public int getResourceId() {
        return resourceId;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public String getId() {
        return id;
    }

    public Reward(String name, String description, int goalCount, int resourceId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.goalCount = goalCount;
        this.resourceId = resourceId;
        this.isUnlocked = false;
        this.isOpened = false;
    }
}
