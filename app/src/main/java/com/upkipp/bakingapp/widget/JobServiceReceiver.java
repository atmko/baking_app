/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.bakingapp.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class JobServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = WidgetJobService.JOBS_ID;

        //queue and stare service
        WidgetJobService.queueWork(context, intent, id);
    }
}