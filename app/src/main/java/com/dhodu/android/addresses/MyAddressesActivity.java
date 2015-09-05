package com.dhodu.android.addresses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dhodu.android.R;
import com.dhodu.android.ui.DividerItemDecoration;
import com.parse.ParseUser;

import org.json.JSONArray;

public class MyAddressesActivity extends AppCompatActivity {

    Toolbar toolbar;

    RecyclerView recyclerView;
    MyAddressesAdapter adapter;

    View emptyLayout;
    Button addNewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerview_addresses);
        emptyLayout = findViewById(R.id.empty_layout);
        addNewAddress = (Button) findViewById(R.id.add_new_address);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Addresses");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAddressesAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));

        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAddressesActivity.this,AddAddressActivity.class);
                startActivity(intent);
            }
        });
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

    @Override
    public void onResume() {
        super.onResume();

        ParseUser currentuser = ParseUser.getCurrentUser();
        JSONArray address = currentuser.getJSONArray("address");

        if (address != null && address.length() != 0) {
            adapter.updateDataSet(address);
            adapter.notifyDataSetChanged();
        } else {
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }
}
