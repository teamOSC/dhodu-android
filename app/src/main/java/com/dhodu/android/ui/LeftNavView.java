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

    TextView tvProfileName, tvProfileMobile, tvProfileAddress;

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
        tvProfileAddress = (TextView) findViewById(R.id.profile_address);

        ParseUser pUser = ParseUser.getCurrentUser();
        if(pUser != null){
            setProfileInfo(
                    pUser.getString("name"),
                    pUser.getUsername(),
                    pUser.getString("house"),
                    pUser.getString("street"),
                    pUser.getString("locality"),
                    pUser.getString("city"),
                    pUser.getString("pin")
            );
        }

    }

    private void setProfileInfo(
            String name,
            String phoneNumber,
            String addressHouse,
            String addressStreet,
            String addressLocality,
            String addressCity,
            String addressPincode
    ){
        tvProfileName.setText(name);
        tvProfileMobile.setText(phoneNumber);
        tvProfileAddress.setText(addressHouse + "\n" + addressStreet + "\n" + addressLocality + "\n" + addressCity + "\n" + addressPincode);
    }
}
