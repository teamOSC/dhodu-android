package com.dhodu.android.ui;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.dhodu.android.R;
import com.parse.ParseUser;

/**
 * Created by championswimmer on 27/8/15.
 */
public class LeftNavView extends NavigationView{
    public static final String TAG = "LeftNavView";

    TextView tvProfileName, tvProfileMobile;

    public LeftNavView(Context context) {
        super(context);
        Log.i(TAG, "LeftNavView context");
    }

    public LeftNavView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "LeftNavView context attrs");
    }

    public LeftNavView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "LeftNavView context attrs defStyleAttr");
    }

    public void setUpProfileView() {
        tvProfileName = (TextView) getRootView().findViewById(R.id.profile_name);
        tvProfileMobile = (TextView) getRootView().findViewById(R.id.profile_mobile);

        ParseUser pUser = ParseUser.getCurrentUser();

        setProfileInfo(
                pUser.getString("name"),
                pUser.getUsername(),
                null, null, null, null, null, null
        );
    }

    private void setProfileInfo(
            String name,
            String phoneNumber,
            String addressHouse,
            String addressStreet,
            String addressLocality,
            String addressCity,
            String addressPincode,
            String walletAmount
    ){
        tvProfileName.setText(name);
        tvProfileMobile.setText(phoneNumber);
    }
}
