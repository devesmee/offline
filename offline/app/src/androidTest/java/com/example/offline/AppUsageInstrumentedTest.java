package com.example.offline;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.offline.backend.appusage.Statistics;
import com.example.offline.backend.appusage.UsageStatsHelper;
import com.example.offline.backend.day.CampusTime;
import com.example.offline.backend.day.Day;
import com.example.offline.backend.goal.Goal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AppUsageInstrumentedTest {
    Context appContext;

    @Before
    public void setUp(){
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Log.d("Test", "useAppContext");
        assertEquals("com.example.offline", appContext.getPackageName());
    }


    /**
     * To use this test swap the times slots around to a period you want to test.
     * Keep in mind the date is in UTC so you'll have to take that into account.
     * Then debug on a device or emulator and check your results.
     */
    @Test
    public void getTodayUsage(){
        UsageStatsHelper helper = new UsageStatsHelper(appContext);
        helper.getUsageOfThisWeek();
    }

}