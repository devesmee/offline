package com.example.offline.backend.day;

import java.util.Date;

public class CampusTime {
    private long enterTime;
    private long leaveTime;
    private long onScreenTime;

    public CampusTime() {
        this.enterTime = new Date().getTime();
    }

    public CampusTime(long enterTime, long leaveTime) {
        this.enterTime = enterTime;
        this.leaveTime = leaveTime;
    }

    @Override
    public String toString() {
        return "CampusTime{" +
                "enterTime=" + enterTime +
                ", leaveTime=" + leaveTime +
                ", onScreenTime=" + onScreenTime +
                '}';
    }

    public void setLeaveTime(long leaveTime) {
        this.leaveTime = leaveTime;
    }

    public void setOnScreenTime(long onScreenTime) {
        this.onScreenTime = onScreenTime;
    }

    public long getEnterTime() {
        return enterTime;
    }

    public long getLeaveTime() {
        return leaveTime;
    }

    public long getOnScreenTime() {
        return onScreenTime;
    }

}
