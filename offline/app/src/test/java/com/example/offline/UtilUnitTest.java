package com.example.offline;

import com.example.offline.backend.Util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilUnitTest {

    @Test
    public void millisecondsToMinutes_shouldReturnMinutesWithOneDecimalPrecision() {
        //arrange
        double expected = 1.8;
        long millis = 110000;
        //act
        double actual = Util.millisecondsToMinutes(millis);
        //assert
        assertEquals(expected, actual, 0);
    }

    @Test
    public void millisecondToHours_shouldReturnHoursWithOneDecimalPrecision() {
        //arrange
        double expected = 2.7;
        long millis = 10000000;
        //act
        double actual = Util.millisecondToHours(millis);
        //assert
        assertEquals(expected, actual, 0);
    }
}
