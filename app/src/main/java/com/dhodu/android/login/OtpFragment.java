package com.dhodu.android.login;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.dhodu.android.utils.SMSReceiver;
import com.dhodu.android.utils.Utils;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class OtpFragment extends Fragment {

    public static final String TAG = "OtpFragment";

    SMSReceiver smsReceiver = null;
    IntentFilter otpIntentFilter;

    EditText password;
    String phone;
    TextView verifying;
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

        phone=getArguments().getString("phone");

        password = (EditText) view.findViewById(R.id.password);
        verifying=(TextView) view.findViewById(R.id.verifying);
        verifyManual=(Button) view.findViewById(R.id.verify_manual);

        verifying.setText("We have sent an SMS with an activation code to +91- "+phone+" ...");

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
                if (s.length()>=6){
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

        setupOTPListener();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (smsReceiver!= null) {
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
        if (smsReceiver!= null)
            getActivity().registerReceiver(smsReceiver, otpIntentFilter);
    }

    private void setupOTPListener() {
        smsReceiver = new SMSReceiver() {
            @Override
            protected void smsReceived(String code) {
                password.setText(code);
                verifyOTP(code);
                getActivity().unregisterReceiver(this);
            }
        };
        otpIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        otpIntentFilter.setPriority(999);
        getActivity().registerReceiver(smsReceiver, otpIntentFilter);
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... strings) {
                URL obj = null;
                try {
                    obj = new URL("http://128.199.128.227:9865/api/v1/otp?username=" + strings[0]);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (!jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(phone);
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
                        FragmentTransaction transaction = LoginActivity.fragmentManager.beginTransaction();
                        LoginDetailFragment loginDetailFragment = LoginDetailFragment.newInstance(username);
                        transaction.replace(R.id.login_container, loginDetailFragment).commit();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }
}
