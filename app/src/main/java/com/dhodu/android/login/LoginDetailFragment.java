package com.dhodu.android.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dhodu.android.MainActivity;
import com.dhodu.android.R;
import com.dhodu.android.Utils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginDetailFragment extends Fragment {

    private EditText name;
    private EditText flat;
    private EditText street;
    private EditText locality;
    private EditText city;
    private EditText pincode;
    private EditText referral;


    public LoginDetailFragment() {
        // Required empty public constructor
    }

    public static LoginDetailFragment newInstance(String username) {
        LoginDetailFragment f = new LoginDetailFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        f.setArguments(args);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = (EditText) view.findViewById(R.id.name);
        flat = (EditText) view.findViewById(R.id.address_flat);
        street = (EditText) view.findViewById(R.id.address_street);
        locality = (EditText) view.findViewById(R.id.address_locality);
        city = (EditText) view.findViewById(R.id.address_city);
        pincode = (EditText) view.findViewById(R.id.address_pincode);
        referral = (EditText) view.findViewById(R.id.referral);
        AppCompatButton submit = (AppCompatButton) view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });

        referral.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    signUpUser();
                    return true;
                }
                return false;
            }
        });
    }

    private void signUpUser() {
        ParseUser user = new ParseUser();
        String username = getArguments().getString("username");
        user.setUsername(username);
        user.setPassword(Utils.generatePassword(username));

        user.put("name", name.getText().toString());
        user.put("house", flat.getText().toString());
        user.put("street", street.getText().toString());
        user.put("locality", locality.getText().toString());
        user.put("city", city.getText().toString());
        user.put("pin", pincode.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }
}
