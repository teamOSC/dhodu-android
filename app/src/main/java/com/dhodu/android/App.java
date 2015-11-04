package com.dhodu.android;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import io.branch.referral.Branch;

/**
 * Created by prempal on 25/8/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "8hl3wDwaozzqWEGdkN8aCaLdWOLNnAxPlXXj53d0", "ado3ZoaVnQgpTixUIWiomvwzYcoPqAijSMWGISMM");
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        if(ParseUser.getCurrentUser() != null)
            installation.put("user", ParseUser.getCurrentUser());
        installation.addUnique("channels", "user");
        installation.saveInBackground();
        Branch.getAutoInstance(this);
    }
}
