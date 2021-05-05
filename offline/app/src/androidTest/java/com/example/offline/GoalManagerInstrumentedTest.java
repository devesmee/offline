package com.example.offline;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.offline.backend.goal.Goal;
import com.example.offline.backend.goal.GoalManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GoalManagerInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.offline", appContext.getPackageName());
    }


    @Test
    public void isGoalAccomplished_shouldReturnTrueWhenGoalAccomplished() {
        //arrange
        Goal goal = new Goal(50, 8, 0L);
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        GoalManager goalManager = GoalManager.getInstance(appContext);
        //act
        goal.setProgress(4);
        boolean result = goalManager.isGoalAccomplished(goal);
        //assert
        assertTrue(result);

    }

    @Test
    public void isGoalAccomplished_shouldReturnFalseWhenGoalIsNotAccomplished() {
        //arrange
        Goal goal = new Goal(50, 8, 0L);
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        GoalManager goalManager = GoalManager.getInstance(appContext);
        //act
        goal.setProgress(3);
        boolean result = goalManager.isGoalAccomplished(goal);
        //assert
        assertFalse(result);

    }

    @Test
    public void getNrOfGoalsCompleted_shouldGetTheNrOfGoalsCompletedInTheLocalStorage() {
        //arrange
        int expected = 2;
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        GoalManager goalManager = GoalManager.getInstance(appContext);
        //act
        goalManager.setNrOfGoalsCompleted(2);
        int actual = goalManager.getNrOfGoalsCompleted();
        //assert
        assertEquals(expected, actual);
    }

    @Test
    public void getAllGoals_shouldGetTheListOfGoalsFromLocalStorage() {
        //arrange
        List<Goal> expected = new ArrayList<>();
        Goal goal = new Goal(50, 8, 0L);
        expected.add(goal);
        expected.add(goal);
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        GoalManager goalManager = GoalManager.getInstance(appContext);
        //act
        goalManager.addGoalToList(goal);
        goalManager.addGoalToList(goal);
        List<Goal> actual = goalManager.getAllGoals();
        //assert
        assertEquals(expected, actual);
    }

    @Test
    public void setActiveGoal_shouldSaveTheCurrentActiveGoalInTheLocalStorage() {
        //arrange
        Goal activeGoal = new Goal(1,1,1);
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        GoalManager goalManager = GoalManager.getInstance(appContext);
        //act
        goalManager.setActiveGoal(activeGoal);
        Goal result = goalManager.getActiveGoal();
        //assert
        assertEquals(activeGoal,result);

    }


//    @Test
//    public void activate_shouldNotActivateNewGoalWhenCurrentOneIsStillActive() {
//        //arrange
//        Goal activeGoal = new Goal(1,1, new Date().getTime());
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        GoalManager goalManager = GoalManager.getInstance(appContext);
//        //act
//        goalManager.setActiveGoal(activeGoal);
//        boolean result = goalManager.activate(null);
//        //assert
//        assertFalse(result);
//    }
//
//    @Test
//    public void activate_shouldActivateNewGoalWhenCurrentOneIsNotActive() {
//        //arrange
//        Goal activeGoal = new Goal(50, 8, 0L);
//        Goal newGoal =  null;
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        GoalManager goalManager = GoalManager.getInstance(appContext);
//        //act
//        goalManager.setActiveGoal(activeGoal);
//        boolean result = goalManager.activate(newGoal);
//        //assert
//        assertTrue(result);
//
//    }







}