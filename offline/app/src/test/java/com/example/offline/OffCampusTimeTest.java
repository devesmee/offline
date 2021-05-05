package com.example.offline;

import org.junit.Assert;
import org.junit.Test;

import org.junit.Assert.*;

public class OffCampusTimeTest {

    @Test
    public void should_increaseTotalTimeAfterEndTime() {
        OffCampusTime offCampusTime = new OffCampusTime();
        offCampusTime.setStartTime(System.currentTimeMillis());
        offCampusTime.setEndTime(System.currentTimeMillis() + 5000);

        Assert.assertTrue(OffCampusTime.getTotal() > 0);
    }
}
