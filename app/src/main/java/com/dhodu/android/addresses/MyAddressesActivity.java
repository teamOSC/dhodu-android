package com.dhodu.android.addresses;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dhodu.android.R;
import com.parse.ParseUser;

import org.json.JSONArray;

public class MyAddressesActivity extends AppCompatActivity {

    Toolbar toolbar;

    RecyclerView recyclerView;
    MyAddressesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerview_addresses);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Addresses");

        ParseUser currentuser = ParseUser.getCurrentUser();
        JSONArray address = currentuser.getJSONArray("address");

        if (address != null) {
            adapter = new MyAddressesAdapter(address);
            recyclerView.setAdapter(adapter);
        }
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
}
