package com.dhodu.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by naman on 16/09/15.
 */
public class CreateOrderActivity extends AppCompatActivity {

    View submitOrder;
    TextView locationAddress;
    RadioButton today, tomorrow, datomorrow;
    RadioGroup radioGroup;
    CheckBox press;
    CheckBox washPress;
    CheckBox dryclean;
    Switch expressSwitch;
    String serviceType;

    TextView terms;
    LinearLayout slot1, slot2, slot3, slot4, slot5, slot6;
    int selectedTimeSlot;

    LatLng latLng;
    int locationShifted = 0;

    int addressindex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        serviceType = getIntent().getStringExtra("servicetype");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        submitOrder = findViewById(R.id.submit_order);
        locationAddress = (TextView) findViewById(R.id.location_address);
        today = (RadioButton) findViewById(R.id.radio_today);
        tomorrow = (RadioButton) findViewById(R.id.radio_tomorrow);
        datomorrow = (RadioButton) findViewById(R.id.radio_datomorrow);
        press = (CheckBox) findViewById(R.id.cb_press);
        washPress = (CheckBox) findViewById(R.id.cb_wash_press);
        dryclean = (CheckBox) findViewById(R.id.cb_dryclean);
        expressSwitch = (Switch) findViewById(R.id.express_switch);
        terms = (TextView) findViewById(R.id.terms);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

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
//                            String serviceTypes = "";
//                            if (press.isChecked())
//                                serviceTypes = serviceTypes + 0;
//                            if (washPress.isChecked())
//                                serviceTypes = serviceTypes + 1;
//                            if (dryclean.isChecked())
//                                serviceTypes = serviceTypes + 2;
                            final ParseObject transaction = new ParseObject("Transaction");
                            transaction.put("status", 0);
                            transaction.put("transaction_id", count + 1);
                            transaction.put("customer", ParseUser.getCurrentUser());
                            //TODO pick time_pick from TimePicker
                            transaction.put("time_pick", getTimeslotForNumber(selectedTimeSlot));
                            transaction.put("pick_date", getDate());
                            transaction.put("address_index", addressindex);
                            transaction.put("comments", "");
                            ParseGeoPoint geoPoint = new ParseGeoPoint(latLng.latitude, latLng.longitude);
                            transaction.put("location", geoPoint);
                            transaction.put("service_type", serviceType);
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
                                                    startActivity(new Intent(CreateOrderActivity.this, MainActivity.class));
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

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://dhodu.com"));
                startActivity(intent);
            }
        });

        setTimeslotOnClickListener();

    }


    private String getDate() {
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(radioButton);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate ="";

        switch (idx) {
            case 0:
                formattedDate = df.format(c.getTime());
                return formattedDate;
            case 1:
                c.add(Calendar.DAY_OF_YEAR, 1);
                formattedDate = df.format(c.getTime());
                return formattedDate;
            case 2:
                c.add(Calendar.DAY_OF_YEAR, 2);
                formattedDate = df.format(c.getTime());
                return formattedDate;
            default:
                return "";
        }


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

    private void setTimeslotOnClickListener() {

        slot1 = (LinearLayout) findViewById(R.id.slot1);
        slot2 = (LinearLayout) findViewById(R.id.slot2);
        slot3 = (LinearLayout) findViewById(R.id.slot3);
        slot4 = (LinearLayout) findViewById(R.id.slot4);
        slot5 = (LinearLayout) findViewById(R.id.slot5);
        slot6 = (LinearLayout) findViewById(R.id.slot6);
        slot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllTimeSlots();
                slot1.setSelected(true);
                selectedTimeSlot = 0;
            }
        });
        slot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllTimeSlots();
                slot2.setSelected(true);
                selectedTimeSlot = 1;
            }
        });
        slot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllTimeSlots();
                slot3.setSelected(true);
                selectedTimeSlot = 2;
            }
        });
        slot4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllTimeSlots();
                slot4.setSelected(true);
                selectedTimeSlot = 3;
            }
        });
        slot5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllTimeSlots();
                slot5.setSelected(true);
                selectedTimeSlot = 4;
            }
        });
        slot6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllTimeSlots();
                slot6.setSelected(true);
                selectedTimeSlot = 5;
            }
        });
    }

    private void disableAllTimeSlots() {
        slot1.setSelected(false);
        slot2.setSelected(false);
        slot3.setSelected(false);
        slot4.setSelected(false);
        slot5.setSelected(false);
        slot6.setSelected(false);
    }

    private String getTimeslotForNumber(int number) {
        switch (number) {
            case 0:
                return "8-10 AM";
            case 1:
                return "10-12 AM";
            case 2:
                return "12-2 PM";
            case 3:
                return "2-4 PM";
            case 4:
                return "4-6 PM";
            case 5:
                return "6-8 PM";
            default:
                return "8-8 PM";
        }
    }
}
