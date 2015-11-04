package com.dhodu.android;

import android.app.Application;

import com.parse.Parse;

import io.branch.referral.Branch;

/**
 * Created by prempal on 25/8/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "8hl3wDwaozzqWEGdkN8aCaLdWOLNnAxPlXXj53d0", "ado3ZoaVnQgpTixUIWiomvwzYcoPqAijSMWGISMM");
        Branch.getAutoInstance(this);
    }
}
