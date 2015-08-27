package com.dhodu.android;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dhodu.android.ui.LeftNavView;
import com.dhodu.android.ui.RightNavView;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftNavView = (LeftNavView) findViewById(R.id.nav_drawer_left);
        rightNavView = (RightNavView) findViewById(R.id.nav_drawer_right);
        slideUpLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        cameraContainer = (FrameLayout) findViewById(R.id.camera_container);
        hangerPulldown = (ImageView) findViewById(R.id.hanger_pulldown);

        leftNavView.setUpProfileView();
        rightNavView.setUpOrderList();

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

            }

            @Override
            public void onPanelCollapsed(View view) {
                hangerPulldown.setVisibility(View.VISIBLE);
                //Stop the camera view here
                try {
                    camera.stopPreview();
                    camera.release();
                } catch (Exception e) {
                    Log.e(TAG, "onPanelCollapsed ", e);
                }
                cameraContainer.setVisibility(View.GONE);
            }

            @Override
            public void onPanelExpanded(View view) {
                hangerPulldown.setVisibility(View.GONE);
                //Initialise the camera view here
                cameraContainer.setVisibility(View.VISIBLE);
                try {
                    camera = Camera.open();
                    Camera.Parameters params = camera.getParameters();
                    camera.setDisplayOrientation(90);
                    camera.setParameters(params);

                    camera.setPreviewDisplay(surfaceHolder);
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
