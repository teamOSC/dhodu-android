package com.dhodu.android.addresses;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dhodu.android.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AddAddressActivity extends AppCompatActivity {

    private EditText name;
    private EditText flat;
    private EditText street;
    private EditText locality;
    private EditText city;
    private EditText pincode;
    private EditText referral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        name = (EditText) findViewById(R.id.name);
        flat = (EditText) findViewById(R.id.address_flat);
        street = (EditText) findViewById(R.id.address_street);
        locality = (EditText) findViewById(R.id.address_locality);
        city = (EditText) findViewById(R.id.address_city);
        pincode = (EditText) findViewById(R.id.address_pincode);
        referral = (EditText) findViewById(R.id.referral);
        AppCompatButton submit = (AppCompatButton) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserAddress();
            }
        });

        referral.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                   addUserAddress();
                    return true;
                }
                return false;
            }
        });
    }

    private void addUserAddress() {
        ParseUser user = ParseUser.getCurrentUser();

        if (user !=null) {
            if (!flat.getText().toString().equals("")) {
                JSONObject address = new JSONObject();
                try {
                    address.put("house", flat.getText().toString());
                    address.put("street", street.getText().toString());
                    address.put("locality", locality.getText().toString());
                    address.put("city", city.getText().toString());
                    address.put("pin", pincode.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                user.addUnique("address", address);
            }

            if (!name.getText().toString().equals("")) {
                user.put("name", name.getText().toString());
            }


            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        notifyAddressAdapter();
                        finish();
                    } else {
                        Toast.makeText(AddAddressActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void notifyAddressAdapter() {
        MyAddressesActivity activity = new MyAddressesActivity();
        MyAddressesAdapter adapter =activity.getAddressAdpater();

        if (adapter != null) {
            ParseUser currentuser = ParseUser.getCurrentUser();
            JSONArray address = currentuser.getJSONArray("address");
            adapter.updateDataSet(address);
            adapter.notifyDataSetChanged();
        }
    }
}