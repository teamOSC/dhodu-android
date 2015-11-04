package com.dhodu.android.addresses;


import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dhodu.android.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class AddAddressActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Toolbar toolbar;
    private EditText name;
    private EditText flat;
    private EditText street;
    private AutoCompleteTextView locality;
    private EditText city;
    private EditText pincode;
    private EditText referral;
    private View currentLocation;

    private String action;
    private int addressIndex;

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;

    private LatLng coordinates;
    double latitude = 0;
    double longitude = 0;
    int locationShifted = 0;

    private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
            new LatLng(72.77492067739843, -9.998857788741589));

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
        locality = (AutoCompleteTextView) findViewById(R.id.address_locality);
        city = (EditText) findViewById(R.id.address_city);
        pincode = (EditText) findViewById(R.id.address_pincode);
        referral = (EditText) findViewById(R.id.referral);
        currentLocation = findViewById(R.id.currentLocation);
        AppCompatButton submit = (AppCompatButton) findViewById(R.id.submit);
        AppCompatButton skip = (AppCompatButton) findViewById(R.id.skip);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(getIntent().getBooleanExtra("HOME_UP", true));

        if (action.equals("edit_address")) {
            getSupportActionBar().setTitle("Edit Address");
            submit.setText("Update");
            fillEditTexts();
        } else if (action.equals("add_address_withskip")) {
            getSupportActionBar().setTitle("Add Address");
            skip.setVisibility(View.VISIBLE);
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

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    setCurrentLocation(AddAddressActivity.this);
            }
        });

        setupLocation();
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
                    address.put("latitude", latitude);
                    address.put("longitude", longitude);
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void setupLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        mAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS, null);

        locality.setAdapter(mAdapter);

        locality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            places.release();
                            return;
                        }

                        final Place place = places.get(0);

                        coordinates = place.getLatLng();
                        latitude = coordinates.latitude;
                        longitude = coordinates.longitude;
                    }
                });

            }
        });
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
                            getAddressFromLatlng(latitude,longitude);
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
                            getAddressFromLatlng(latitude,longitude);
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

    private void getAddressFromLatlng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(null!=listAddresses&&listAddresses.size()>0){
                String address = listAddresses.get(0).getAddressLine(0);
                locality.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
