package com.upkipp.bakingapp.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class JobServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = 0;

        id = WidgetJobService.JOBS_ID;

        if (id != 0) {
            WidgetJobService.queueWork(context, intent, id);
        }
    }
}