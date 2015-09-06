package com.dhodu.android;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dhodu.android.addresses.MyAddressesActivity;
import com.dhodu.android.login.LoginActivity;
import com.dhodu.android.ui.SpacesItemDecoration;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Calendar;
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
    RecyclerView ordersView;
    Toolbar toolbar;
    TextView orderCount;

    int addressindex = 0;


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
            orderCount = (TextView) findViewById(R.id.order_count);
            locationAddress = (TextView) findViewById(R.id.location_address);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setTitle("Dhodu");

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
                        }
                        return false;
                    }
                });
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.current_fragment_container, new CurrentOrderFragment());
            transaction.commit();

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
                                ParseObject transaction = new ParseObject("Transaction");
                                transaction.put("status", 0);
                                transaction.put("transaction_id", count + 1);
                                transaction.put("customer", ParseUser.getCurrentUser());
                                transaction.put("time_pick", orderTime.getText().toString());
                                transaction.put("address_index", addressindex);
                                transaction.put("comments", "");
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

            setUpOrderList();
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

        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpProfileView() {
        tvProfileMobile = (TextView) findViewById(R.id.profile_mobile);

        ParseUser pUser = ParseUser.getCurrentUser();
        if (pUser != null) {
            tvProfileMobile.setText(
                    pUser.getUsername()
            );
        }

    }

    public void setUpOrderList() {
        ordersView = (RecyclerView) findViewById(R.id.list_order_history);
        ordersView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_card_order_history);
        ordersView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
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
                    ordersView.setAdapter(new OrderListAdapter(list));
                    if (list.size() == 0) {
                        orderCount.setText("Your order history will appear here.");
                    } else {
                        if (list.size() == 1)
                            orderCount.setText("1 order placed");
                        else orderCount.setText(list.size() + " orders placed");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


}
