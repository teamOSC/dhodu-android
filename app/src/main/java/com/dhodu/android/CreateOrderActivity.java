package com.dhodu.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dhodu.android.addresses.MyAddressesActivity;
import com.google.android.gms.maps.model.LatLng;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by naman on 16/09/15.
 */
public class CreateOrderActivity extends AppCompatActivity {

    View submitOrder;
    TextView locationAddress;
    TimePicker timePicker;
    RadioButton today, tomorrow, datomorrow;
    CheckBox press;
    CheckBox washPress;
    CheckBox dryclean;
    Switch expressSwitch;

    LatLng latLng;
    int locationShifted = 0;

    int addressindex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        submitOrder = findViewById(R.id.submit_order);
        locationAddress = (TextView) findViewById(R.id.location_address);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        today = (RadioButton) findViewById(R.id.radio_today);
        tomorrow = (RadioButton) findViewById(R.id.radio_tomorrow);
        datomorrow = (RadioButton) findViewById(R.id.radio_datomorrow);
        press = (CheckBox) findViewById(R.id.cb_press);
        washPress = (CheckBox) findViewById(R.id.cb_wash_press);
        dryclean = (CheckBox) findViewById(R.id.cb_dryclean);
        expressSwitch = (Switch) findViewById(R.id.express_switch);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New order");

        setCurrentLocation(this);

        expressSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Toast.makeText(getBaseContext(), "Express service coming soon", Toast.LENGTH_SHORT).show();
                    expressSwitch.setChecked(false);
                }
            }
        });

        locationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateOrderActivity.this, MyAddressesActivity.class);
                intent.setAction("chooseAddress");
                startActivityForResult(intent, 0);
            }
        });

        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pDialog = new ProgressDialog(CreateOrderActivity.this);
                pDialog.setMessage("Placing order...");
                pDialog.setCancelable(false);
                pDialog.show();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
                query.countInBackground(new CountCallback() {
                    @Override
                    public void done(int count, com.parse.ParseException e) {
                        if (e != null) {
                            pDialog.dismiss();
                            Toast.makeText(getBaseContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                        } else {
                            String serviceTypes = "";
                            if (press.isChecked())
                                serviceTypes = serviceTypes + 0;
                            if (washPress.isChecked())
                                serviceTypes = serviceTypes + 1;
                            if (dryclean.isChecked())
                                serviceTypes = serviceTypes + 2;
                            final ParseObject transaction = new ParseObject("Transaction");
                            transaction.put("status", 0);
                            transaction.put("transaction_id", count + 1);
                            transaction.put("customer", ParseUser.getCurrentUser());
                            //TODO pick time_pick from TimePicker
                            transaction.put("time_pick", "");
                            transaction.put("pick_date", "12-09-2015");
                            transaction.put("address_index", addressindex);
                            transaction.put("comments", "");
                            ParseGeoPoint geoPoint = new ParseGeoPoint(latLng.latitude, latLng.longitude);
                            transaction.put("location", geoPoint);
                            transaction.put("service_type", serviceTypes);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Manager");
                            query.whereNear("location", geoPoint);
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, com.parse.ParseException e) {
                                    if (e == null) {
                                        transaction.put("assigned_manager", object);
                                        transaction.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(com.parse.ParseException e) {
                                                if (e == null) {
                                                    pDialog.dismiss();
                                                    Toast.makeText(getBaseContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    pDialog.dismiss();
                                                    Toast.makeText(getBaseContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        pDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }

                    }
                });

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                addressindex = data.getIntExtra("address_index", 0);
                String name = data.getStringExtra("address_name");
                locationAddress.setText(name);
            }
            if (resultCode == RESULT_CANCELED) {
            }

        }

    }

    public void setCurrentLocation(Context context) {

        latLng = new LatLng(0, 0);
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000, 0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            locationShifted++;
                            if (locationShifted > 2) {
                                try {
                                    locationManager.removeUpdates(this);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        try {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            locationShifted++;
                            if (locationShifted > 2) {
                                try {
                                    locationManager.removeUpdates(this);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
}
