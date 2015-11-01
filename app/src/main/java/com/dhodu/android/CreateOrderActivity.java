package com.dhodu.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dhodu.android.addresses.MyAddressesActivity;
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
    String serviceType;
    TextView terms;
    LinearLayout slot1, slot2, slot3, slot4, slot5, slot6;
    EditText promoCode;
    Button applyPromo;
    int selectedTimeSlot = -1;

    double latitude = 0;
    double longitude = 0;
    int locationShifted = 0;
    int addressindex = 0;
    Calendar c;

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
        terms = (TextView) findViewById(R.id.terms);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        promoCode = (EditText) findViewById(R.id.promo_code);
        applyPromo = (Button) findViewById(R.id.apply_promo);
        slot1 = (LinearLayout) findViewById(R.id.slot1);
        slot2 = (LinearLayout) findViewById(R.id.slot2);
        slot3 = (LinearLayout) findViewById(R.id.slot3);
        slot4 = (LinearLayout) findViewById(R.id.slot4);
        slot5 = (LinearLayout) findViewById(R.id.slot5);
        slot6 = (LinearLayout) findViewById(R.id.slot6);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New order");

        setCurrentLocation(this);

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

                if (locationAddress.getText().equals("Choose address")) {
                    Toast.makeText(view.getContext(), "Choose valid address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedTimeSlot == -1) {
                    Toast.makeText(view.getContext(), "Select time slot", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog pDialog = new ProgressDialog(CreateOrderActivity.this);
                pDialog.setMessage("Placing order...");
                pDialog.setCancelable(false);
                pDialog.show();
                final ParseObject transaction = new ParseObject("Transaction");
                transaction.put("status", 0);
                transaction.put("customer", ParseUser.getCurrentUser());
                transaction.put("time_pick", getTimeslotForNumber(selectedTimeSlot));
                transaction.put("pick_date", getDate());
                transaction.put("address_index", addressindex);
                transaction.put("comments", "");
                ParseGeoPoint geoPoint = new ParseGeoPoint(latitude, longitude);
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
                                        startActivity(new Intent(CreateOrderActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
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
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://dhodu.com/terms.html"));
                startActivity(intent);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(radioButtonID);
                selectedTimeSlot = -1;
                deselectAllTimeSlots();
                if (radioGroup.indexOfChild(radioButton) == 0) {
                    setTimeSlotValidation();
                } else {
                    enableAllTimeSlots();
                }
            }
        });

        c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour >= 20) {
            radioGroup.check(R.id.radio_tomorrow);
        }else{
            radioGroup.check(R.id.radio_today);
            setTimeSlotValidation();
        }

        setTimeslotOnClickListener();

        applyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Invalid promo code", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private String getDate() {
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(radioButton);

        c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = "";

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

        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000, 0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
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
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
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

    private void setTimeSlotValidation() {
        c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour >= 20) {
            disableAllTimeSlots();
            final AlertDialog.Builder alert = new AlertDialog.Builder(CreateOrderActivity.this);
            alert.setMessage("Uhoh! We are done with today. How about we show up tomorrow?");
            alert.setCancelable(true);
            alert.setNegativeButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
            alert.show();
        } else {
            if (hour >= 8)
                slot1.setEnabled(false);
            if (hour >= 10)
                slot2.setEnabled(false);
            if (hour >= 12)
                slot3.setEnabled(false);
            if (hour >= 14)
                slot4.setEnabled(false);
            if (hour >= 16)
                slot5.setEnabled(false);
            if (hour >= 18)
                slot6.setEnabled(false);
        }
    }

    private void setTimeslotOnClickListener() {

        slot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectAllTimeSlots();
                slot1.setSelected(true);
                selectedTimeSlot = 0;
            }
        });
        slot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectAllTimeSlots();
                slot2.setSelected(true);
                selectedTimeSlot = 1;
            }
        });
        slot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectAllTimeSlots();
                slot3.setSelected(true);
                selectedTimeSlot = 2;
            }
        });
        slot4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectAllTimeSlots();
                slot4.setSelected(true);
                selectedTimeSlot = 3;
            }
        });
        slot5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectAllTimeSlots();
                slot5.setSelected(true);
                selectedTimeSlot = 4;
            }
        });
        slot6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectAllTimeSlots();
                slot6.setSelected(true);
                selectedTimeSlot = 5;
            }
        });
    }

    private void deselectAllTimeSlots() {
        slot1.setSelected(false);
        slot2.setSelected(false);
        slot3.setSelected(false);
        slot4.setSelected(false);
        slot5.setSelected(false);
        slot6.setSelected(false);
    }

    private void enableAllTimeSlots() {
        slot1.setEnabled(true);
        slot2.setEnabled(true);
        slot3.setEnabled(true);
        slot4.setEnabled(true);
        slot5.setEnabled(true);
        slot6.setEnabled(true);
    }

    private void disableAllTimeSlots() {
        slot1.setEnabled(false);
        slot2.setEnabled(false);
        slot3.setEnabled(false);
        slot4.setEnabled(false);
        slot5.setEnabled(false);
        slot6.setEnabled(false);
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
