package com.dhodu.android;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dhodu.android.login.LoginActivity;
import com.dhodu.android.ui.LeftNavView;
import com.dhodu.android.ui.RightNavView;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    DrawerLayout drawerLayout;
    LeftNavView leftNavView;
    RightNavView rightNavView;
    SlidingUpPanelLayout slideUpLayout;
    ImageView hangerPulldown;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    FrameLayout cameraContainer;
    float pullDownRatio;
    boolean cameraShowing;

    EditText orderTime;
    Button submitOrder;
    Switch expressSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseUser parseUser = ParseUser.getCurrentUser();
        if ((parseUser == null)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        // Instantiate the layouts
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftNavView = (LeftNavView) findViewById(R.id.nav_drawer_left);
        rightNavView = (RightNavView) findViewById(R.id.nav_drawer_right);
        slideUpLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        cameraContainer = (FrameLayout) findViewById(R.id.camera_container);
        hangerPulldown = (ImageView) findViewById(R.id.hanger_pulldown);

        //Set up the left and right drawers
        leftNavView.setUpProfileView();
        rightNavView.setUpOrderList();

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
                        if(e != null){
                            Toast.makeText(getBaseContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                        }else{
                            ParseObject transaction = new ParseObject("Transaction");
                            transaction.put("status", 0);
                            transaction.put("transaction_id", count + 1);
                            transaction.put("customer", ParseUser.getCurrentUser());
                            transaction.put("time_pick", orderTime.getText().toString());
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
                hangerPulldown.setAlpha(1 - v);
                cameraContainer.setVisibility(View.VISIBLE);
                cameraContainer.setAlpha(v);
            }

            @Override
            public void onPanelCollapsed(View view) {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
