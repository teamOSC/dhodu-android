package com.dhodu.android;

import com.dhodu.android.utils.LifeCycleHandler;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

public class ParsePushReceiver extends ParsePushBroadcastReceiver {
    @Override
    public void onPushReceive(Context context, Intent intent) {
        if (LifeCycleHandler.isApplicationInForeground()){
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtras(intent.getExtras());
            context.startActivity(i);
        }
    }
}
