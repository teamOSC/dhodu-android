package com.dhodu.android.login;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.dhodu.android.R;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            LoginFragment newFragment = new LoginFragment();
            transaction.add(R.id.login_container, newFragment, "LoginFragment").commit();

        }

    }
}
