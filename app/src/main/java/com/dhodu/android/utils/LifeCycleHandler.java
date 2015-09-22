package com.dhodu.android.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class LifeCycleHandler implements Application.ActivityLifecycleCallbacks {
    private static int  resumed;
    private static int paused;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }
}