package com.example.offline.backend.appusage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.offline.backend.day.DayManager;

public class UnlockBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            DayManager.getInstance(context).saveScreenUnlock();
        }
    }
}

