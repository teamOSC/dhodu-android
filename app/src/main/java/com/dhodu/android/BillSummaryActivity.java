package com.dhodu.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dhodu.android.ui.DividerItemDecoration;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by naman on 16/09/15.
 */
public class BillSummaryActivity extends AppCompatActivity {

    TextView amount;
    View loadingView;
    RecyclerView clothesRecyclerview;
    ClothesDataAdpater adpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bill Sumamry");

        amount = (TextView) findViewById(R.id.amount);
        loadingView = findViewById(R.id.loadingView);
        clothesRecyclerview =(RecyclerView) findViewById(R.id.recyclerview_bill);
        clothesRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        fetchBillSummary();
    }

    private void fetchBillSummary() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
        query.getInBackground(getIntent().getStringExtra("transaction_id"), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                setBillSummary(object);
                loadingView.setVisibility(View.GONE);
            }
        });
    }

    private void setBillSummary(ParseObject transaction) {
        amount.setText("₹ "+String.valueOf(transaction.getNumber("amount")));
        clothesRecyclerview.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        adpater = new ClothesDataAdpater(transaction.getJSONArray("clothes_data"));
        clothesRecyclerview.setAdapter(adpater);

    }
}
