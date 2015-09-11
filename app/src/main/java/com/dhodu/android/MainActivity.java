package com.dhodu.android;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dhodu.android.addresses.MyAddressesActivity;
import com.dhodu.android.login.LoginActivity;
import com.dhodu.android.ui.CircleImageView;
import com.dhodu.android.ui.SpacesItemDecoration;
import com.google.android.gms.maps.model.LatLng;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    DrawerLayout drawerLayout;
    SlidingUpPanelLayout slideUpLayout;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    FrameLayout cameraContainer;
    LinearLayout topView;
    float pullDownRatio;
    boolean cameraShowing;

    EditText orderTime;
    Button submitOrder;
    Switch expressSwitch;
    TextView locationAddress;

    TextView tvProfileMobile;
    CircleImageView profilePhoto;
    Toolbar toolbar;

    int addressindex = 0;
    int locationShifted = 0;
    RecyclerView centerRecyclerview;
    private String photoPath;
    private LatLng latLng;
    private TextView orderStatus;
    private ImageView expandCreateOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseUser parseUser = ParseUser.getCurrentUser();
        if ((parseUser == null)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            // Instantiate the layouts
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            slideUpLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
            cameraContainer = (FrameLayout) findViewById(R.id.camera_container);
            topView = (LinearLayout) findViewById(R.id.topView);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            locationAddress = (TextView) findViewById(R.id.location_address);
            orderStatus = (TextView) findViewById(R.id.orderStatus);
            expandCreateOrder = (ImageView) findViewById(R.id.expand);
            centerRecyclerview = (RecyclerView) findViewById(R.id.center_recyclerview);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setTitle("Dhodu");

            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putBoolean("app_first_run", false).apply();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (navigationView != null) {
                setUpProfileView();
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_addresses:
                                Intent intent = new Intent(MainActivity.this, MyAddressesActivity.class);
                                intent.setAction("addAddress");
                                startActivity(intent);
                                break;
                            case R.id.nav_logout:
                                ParseUser.logOutInBackground(new LogOutCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
            }

//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.current_fragment_container, new CurrentOrderFragment());
//            transaction.commit();

            //Setup the order pulldown screen
            orderTime = (EditText) findViewById(R.id.order_time);
            orderTime.setInputType(EditorInfo.TYPE_NULL);
            orderTime.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        // TODO Auto-generated method stub
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                orderTime.setText(
                                        ((selectedHour < 10) ? "0" + selectedHour : selectedHour)
                                                + ":" +
                                                ((selectedMinute < 10) ? "0" + selectedMinute : selectedMinute));
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            locationAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MyAddressesActivity.class);
                    intent.setAction("chooseAddress");
                    startActivityForResult(intent, 0);
                }
            });

            expressSwitch = (Switch) findViewById(R.id.express_switch);
            expressSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        Toast.makeText(getBaseContext(), "Express service coming soon", Toast.LENGTH_SHORT).show();
                        expressSwitch.setChecked(false);
                    }
                }
            });

            submitOrder = (Button) findViewById(R.id.btn_submit_order);
            submitOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
                    query.countInBackground(new CountCallback() {
                        @Override
                        public void done(int count, ParseException e) {
                            if (e != null) {
                                Toast.makeText(getBaseContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                            } else {
                                final ParseObject transaction = new ParseObject("Transaction");
                                transaction.put("status", 0);
                                transaction.put("transaction_id", count + 1);
                                transaction.put("customer", ParseUser.getCurrentUser());
                                transaction.put("time_pick", orderTime.getText().toString());
                                transaction.put("address_index", addressindex);
                                transaction.put("comments", "");
                                ParseGeoPoint geoPoint = new ParseGeoPoint(latLng.latitude, latLng.longitude);
                                transaction.put("location", geoPoint);
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Manager");
                                query.whereNear("location", geoPoint);
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        if (e == null) {
                                            transaction.put("assigned_manager", object);
                                            transaction.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Toast.makeText(getBaseContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getBaseContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }
                        }
                    });
                }
            });

            surfaceView = (SurfaceView) findViewById(R.id.surface_view);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                }
            });

            cameraContainer.setVisibility(View.GONE);

            slideUpLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View view, float v) {
                    cameraContainer.setVisibility(View.VISIBLE);
                    cameraContainer.setAlpha(v);
                    topView.setAlpha(1 - v);
                }

                @Override
                public void onPanelCollapsed(View view) {
                    topView.setVisibility(View.VISIBLE);
                    //Stop the camera view here
                    try {
                        camera.stopPreview();
                        camera.release();
                        cameraShowing = false;
                    } catch (Exception e) {
                        Log.e(TAG, "onPanelCollapsed ", e);
                    }
                }

                @Override
                public void onPanelExpanded(View view) {
                    //Initialise the camera view here
                    topView.setVisibility(View.GONE);
                    try {
                        camera = Camera.open();
                        Camera.Parameters params = camera.getParameters();
                        camera.setDisplayOrientation(90);
                        camera.setParameters(params);

                        camera.setPreviewDisplay(surfaceHolder);
                        cameraShowing = true;
                    } catch (Exception e) {
                        Log.e(TAG, "onPanelExpanded ", e);
                    }
                    camera.startPreview();

                }

                @Override
                public void onPanelAnchored(View view) {

                }

                @Override
                public void onPanelHidden(View view) {

                }
            });

            //drawerLayout.setBackgroundResource(R.drawable.app_background_doodle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.dhodu_primary_dark));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.dhodu_primary_dark));
            }

            setUpAdapter();
            setCurrentLocation(this);
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
        } else if (requestCode == 69) {
            if (resultCode == RESULT_CANCELED) {
                File photoFile = new File(photoPath);
                if (photoFile.exists())
                    new File(photoPath).delete();
            } else if (resultCode == RESULT_OK) {
                if (data == null) {
                    Picasso.with(this).load(new File(photoPath)).into(profilePhoto);
                    saveImageToParse(photoPath);
                } else {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Picasso.with(this).load(new File(picturePath)).into(profilePhoto);
                    saveImageToParse(picturePath);
                }
            }
        }
    }

    private void saveImageToParse(String path){
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ParseUser parseUser = ParseUser.getCurrentUser();
        final ParseFile parseFile = new ParseFile("photo.png", bytes);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    parseUser.put("photo", parseFile.getUrl());
                    parseUser.saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (slideUpLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slideUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "7827121121"));
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpProfileView() {
        tvProfileMobile = (TextView) findViewById(R.id.profile_mobile);
        profilePhoto = (CircleImageView) findViewById(R.id.profile_pic);

        ParseUser pUser = ParseUser.getCurrentUser();
        if (pUser != null) {
            tvProfileMobile.setText(
                    pUser.getUsername()
            );
            if(pUser.getString("photo") != null)
                Picasso.with(this).load(pUser.getString("photo")).placeholder(R.drawable.avatar_blank).into(profilePhoto);
        }

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                }

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Choose Profile Picture");

                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, 69);
            }
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return image;
    }

    public void setUpAdapter() {
        centerRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fetchOrderHistory();
    }


    private void fetchOrderHistory() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
        query.whereEqualTo("customer", ParseUser.getCurrentUser());
        query.whereEqualTo("status", 5);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_card_order_history);
                    centerRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
                    centerRecyclerview.setAdapter(new CenterAdapter(MainActivity.this, list));
                } else {
                    e.printStackTrace();
                }
            }
        });
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

    public void setStatusToHeader(String status, int imageId) {
        if (imageId != 0) {
            expandCreateOrder.setImageResource(imageId);
            expandCreateOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder cancelDialog = new AlertDialog.Builder(MainActivity.this);
                    cancelDialog.setTitle("Cancel Booking");
                    cancelDialog.setCancelable(true);
                    cancelDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO:
                        }
                    });
                    cancelDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    cancelDialog.setMessage("Are you sure you want to cancel the booking?");
                    cancelDialog.show();
                }
            });
        }
        orderStatus.setText(status);
    }


}
