package com.dhodu.android.login;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dhodu.android.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";
    EditText phone;
    Button accept;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_login, container, false);

        phone = (EditText) view.findViewById(R.id.phone);
        accept = (Button) view.findViewById(R.id.activate);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phone.setImeOptions(EditorInfo.IME_ACTION_DONE);
        phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "onEditorAction");
                    proceed();
                }
                return false;
            }
        });

    }

    private void proceed() {
        FragmentTransaction transaction = LoginActivity.fragmentManager.beginTransaction();
        OtpFragment otpFragment = new OtpFragment().newInstance(phone.getText().toString());
        transaction.replace(R.id.login_container, otpFragment).commit();
    }

}
