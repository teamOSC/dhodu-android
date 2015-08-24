package com.dhodu.android;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView leftNavView, rightNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ParseUser parseUser = ParseUser.getCurrentUser();
//        if ((parseUser == null)) {
//            Intent i = new Intent(this, LoginActivity.class);
//            startActivity(i);
//            finish();
//        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftNavView = (NavigationView) findViewById(R.id.nav_drawer_left);
        rightNavView = (NavigationView) findViewById(R.id.nav_drawer_right);

        drawerLayout.setBackgroundResource(R.drawable.app_background_doodle);

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
