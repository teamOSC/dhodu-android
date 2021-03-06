package com.dhodu.android.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.List;

import io.branch.referral.Branch;

public class OtpFragment extends Fragment {

    public static final String TAG = "OtpFragment";

    SMSReceiver smsReceiver = null;
    IntentFilter otpIntentFilter;

    EditText password;
    String phone;
    TextView verifying, resendOtp, noOtp, timer;
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
        timer = (TextView) view.findViewById(R.id.timer);

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
                if (s.length() == 4) {
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
                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        resendOtp.setEnabled(false);
                        resendOtp.setTextColor(Color.parseColor("#22ffffff"));
                        timer.setVisibility(View.VISIBLE);
                        timer.setText(millisUntilFinished / 1000 + "");
                    }

                    public void onFinish() {
                        timer.setVisibility(View.GONE);
                        resendOtp.setTextColor(Color.parseColor("#ffffff"));
                        resendOtp.setEnabled(true);
                    }
                }.start();
                requestOtp(phone);
                Toast.makeText(getActivity(), "We've re sent you an OTP", Toast.LENGTH_SHORT).show();
            }
        });

        noOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Give us a call");
                alert.setMessage("No code? No worries, please give us a call on our number and our operator will help you out.");
                alert.setCancelable(true);
                alert.setPositiveButton("Call Us", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "7827121121")));
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
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
        requestOtp(phone);
    }

    private void verifyOTP(final String code) {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Verifying OTP...");
        pDialog.setCancelable(false);
        pDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("OTP");
        query.whereEqualTo("username", phone);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (code.equals(object.getString("otp"))) {
                        pDialog.dismiss();
                        proceed();
                    } else {
                        pDialog.dismiss();
                        Toast.makeText(getActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pDialog.dismiss();
                    Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void proceed() {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        final String username = phone.trim();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {
                        ParseUser.logInInBackground(username, "dhodu", new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                    Branch.getInstance().setIdentity(user.getObjectId());
                                    installation.put("user", user);
                                    installation.addUnique("channels", "user");
                                    installation.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                startActivity(new Intent(getActivity(), MainActivity.class));
                                                pDialog.dismiss();
                                                getActivity().finish();
                                            } else {
                                                pDialog.dismiss();
                                                Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                } else {
                                    pDialog.dismiss();
                                    Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        pDialog.dismiss();
                        signUpUser(username);
                    }
                } else {
                    pDialog.dismiss();
                    e.printStackTrace();
                }
            }
        });

    }

    private void signUpUser(String username) {

        final ParseUser user = new ParseUser();
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Signing Up...");
        pDialog.setCancelable(false);
        pDialog.show();
        user.setUsername(username);
        user.setPassword("dhodu");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Branch.getInstance().setIdentity(user.getObjectId());
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("user", user);
                    installation.addUnique("channels", "user");
                    installation.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "installation saved");
                                pDialog.dismiss();
                                Intent intent = new Intent(new Intent(getActivity(), AddAddressActivity.class));
                                intent.setAction("add_address_withskip");
                                intent.putExtra("HOME_UP", false);
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                e.printStackTrace();
                                pDialog.dismiss();
                                Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    pDialog.dismiss();
                    Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

    private void requestOtp(String phone){
        HashMap<String, Object> params = new HashMap<>();
        params.put("number", phone);
        ParseCloud.callFunctionInBackground("sendOTP", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
