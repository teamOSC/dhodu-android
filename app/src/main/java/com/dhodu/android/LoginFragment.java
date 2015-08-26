package com.dhodu.android;


import android.content.IntentFilter;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.parse.FindCallback;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";
    EditText phone;
    EditText password;
    Button buttonDone;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phone = (EditText) view.findViewById(R.id.phone);
        password = (EditText) view.findViewById(R.id.password);
        buttonDone = (Button) view.findViewById(R.id.button_done);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneSubmit();
            }
        });

        phone.setImeOptions(EditorInfo.IME_ACTION_DONE);
        phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "onEditorAction");
                    phoneSubmit();

                    return true;
                }
                return false;
            }
        });
    }

    private void phoneSubmit() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", phone.getText().toString().trim());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        showPasswordField();
                    } else {
                        showOTPField();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showOTPField() {
        password.setVisibility(View.VISIBLE);
        SMSReceiver smsReceiver = new SMSReceiver() {
            @Override
            protected void smsReceived(String code) {
                password.setText(code);
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        getActivity().registerReceiver(smsReceiver, intentFilter);
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
        }.execute(phone.getText().toString());

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("OTP");
                query.whereEqualTo("username", phone.getText().toString());
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> scoreList, ParseException e) {
                        if (e == null) {
                            proceed();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
        });

    }

    private void showPasswordField() {
        password.setVisibility(View.VISIBLE);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });
    }

    private void proceed() {
        FragmentTransaction transaction = LoginActivity.fragmentManager.beginTransaction();
        LoginDetailFragment loginDetailFragment = LoginDetailFragment.newInstance(phone.getText().toString());
        transaction.replace(R.id.login_container, loginDetailFragment).commit();
    }
}
