package com.dhodu.android.login;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dhodu.android.MainActivity;
import com.dhodu.android.R;
import com.dhodu.android.addresses.AddAddressActivity;
import com.dhodu.android.utils.SMSReceiver;
import com.dhodu.android.utils.Utils;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class OtpFragment extends Fragment {

    public static final String TAG = "OtpFragment";

    SMSReceiver smsReceiver = null;
    IntentFilter otpIntentFilter;

    EditText password;
    String phone;
    TextView verifying, resendOtp, noOtp;
    Button verifyManual;

    public static OtpFragment newInstance(String phone) {
        OtpFragment f = new OtpFragment();
        Bundle args = new Bundle();
        args.putString("phone", phone);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_otp, container, false);

        phone = getArguments().getString("phone");

        password = (EditText) view.findViewById(R.id.password);
        verifying = (TextView) view.findViewById(R.id.verifying);
        verifyManual = (Button) view.findViewById(R.id.verify_manual);
        noOtp = (TextView) view.findViewById(R.id.no_otp);
        resendOtp = (TextView) view.findViewById(R.id.resend_otp);

        verifying.setText("We have sent an SMS with an activation code to +91- " + phone + " ...");

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    verifyOTP(password.getText().toString());
                }
                return false;
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 4) { //TODO: Just ==4, after Shubham fixes this on the server
                    verifyManual.setVisibility(View.VISIBLE);
                } else {
                    verifyManual.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        verifyManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP(password.getText().toString());
            }
        });

        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.requestOtp(getActivity(), phone);
                Toast.makeText(getActivity(), "We've re sent you an OTP", Toast.LENGTH_SHORT).show();
            }
        });

        noOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setupOTPListener();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (smsReceiver != null) {
            try {
                getActivity().unregisterReceiver(smsReceiver);
            } catch (Exception e) {
                Log.e(TAG, "onPause ", e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (smsReceiver != null)
            getActivity().registerReceiver(smsReceiver, otpIntentFilter);
    }

    private void setupOTPListener() {
        smsReceiver = new SMSReceiver() {
            @Override
            protected void smsReceived(String code) {
                password.setText(code);
                verifyManual.setVisibility(View.GONE);
                verifyOTP(code);
                getActivity().unregisterReceiver(this);
            }
        };
        otpIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        otpIntentFilter.setPriority(999);
        getActivity().registerReceiver(smsReceiver, otpIntentFilter);
        Utils.requestOtp(getActivity(), phone);
    }

    private void verifyOTP(final String code) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("OTP");
        query.whereEqualTo("username", phone);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (code.equals(list.get(0).getString("otp"))) {
                        proceed();
                    } else {
                        Toast.makeText(getActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void proceed() {
        final String username = phone.trim();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {
                        ParseUser.logInInBackground(username, Utils.generatePassword(username), new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        signUpUser(username);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }

    private void signUpUser(String username) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(Utils.generatePassword(username));

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    startActivity(new Intent(getActivity(), AddAddressActivity.class));
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }
}
