package com.dhodu.android.addresses;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
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

    private Toolbar toolbar;
    private EditText name;
    private EditText flat;
    private EditText street;
    private EditText locality;
    private EditText city;
    private EditText pincode;
    private EditText referral;

    private String action;
    private int addressIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        action = getIntent().getAction();
        addressIndex = getIntent().getIntExtra("address_index", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        name = (EditText) findViewById(R.id.name);
        flat = (EditText) findViewById(R.id.address_flat);
        street = (EditText) findViewById(R.id.address_street);
        locality = (EditText) findViewById(R.id.address_locality);
        city = (EditText) findViewById(R.id.address_city);
        pincode = (EditText) findViewById(R.id.address_pincode);
        referral = (EditText) findViewById(R.id.referral);
        AppCompatButton submit = (AppCompatButton) findViewById(R.id.submit);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(getIntent().getBooleanExtra("HOME_UP", true));

        if (action.equals("edit_address")) {
            getSupportActionBar().setTitle("Edit Address");
            submit.setText("Update");
            fillEditTexts();
        } else {
            getSupportActionBar().setTitle("Add Address");
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (action.equals("edit_address"))
                    updateUserAddress();
                else
                    addUserAddress();
            }
        });

        referral.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (action.equals("edit_address"))
                        updateUserAddress();
                    else
                        addUserAddress();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addUserAddress() {
        final ProgressDialog pDialog = new ProgressDialog(AddAddressActivity.this);

        if (action.equals("edit_address"))
            pDialog.setMessage("Updating address");
        else
            pDialog.setMessage("Adding address...");

        pDialog.setCancelable(false);
        pDialog.show();
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            if (!name.getText().toString().equals("") && !flat.getText().toString().equals("") && !street.getText().toString().equals("")) {
                JSONObject address = new JSONObject();
                try {
                    address.put("house", flat.getText().toString());
                    address.put("street", street.getText().toString());
                    address.put("locality", locality.getText().toString());
                    address.put("city", city.getText().toString());
                    address.put("pin", pincode.getText().toString());
                    address.put("name", name.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                user.addUnique("address", address);
            } else {
                if (name.getText().toString().equals(""))
                    name.setError("Required");
                if (flat.getText().toString().equals(""))
                    flat.setError("Required");
                if (street.getText().toString().equals(""))
                    street.setError("Required");
            }

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        pDialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(AddAddressActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void fillEditTexts() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        JSONArray array = parseUser.getJSONArray("address");
        try {
            JSONObject jsonObject = array.getJSONObject(addressIndex);
            name.setText(jsonObject.getString("name"));
            flat.setText(jsonObject.getString("house"));
            street.setText(jsonObject.getString("street"));
            locality.setText(jsonObject.getString("locality"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUserAddress() {
        ParseUser user = ParseUser.getCurrentUser();
        JSONArray array = user.getJSONArray("address");
        array.remove(addressIndex);
        addUserAddress();

    }
}
