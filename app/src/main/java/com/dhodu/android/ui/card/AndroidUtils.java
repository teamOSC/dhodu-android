package com.dhodu.android.ui.card;

import android.os.Build;

public class AndroidUtils {

    //SDK 14 Version 4.0
    public final static boolean icsOrBetter(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    private AndroidUtils(){}
}