package com.dhodu.android.payments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dhodu.android.Adapter;
import com.dhodu.android.R;

import in.juspay.godel.ui.JuspayBrowserFragment;
import in.juspay.juspaysafe.BrowserCallback;

/**
 * Created by naman on 05/11/15.
 */
public class PayNowActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    Button pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paynow);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pay now");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(2);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        pay = (Button) findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JuspayBrowserFragment.openJuspayConnection();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new CardsFragment(), "Cards");
        adapter.addFragment(new NetBankingFragment(), "Net Banking");
        viewPager.setAdapter(adapter);
    }

    public BrowserCallback callBack = new BrowserCallback() {
        @Override
        public void ontransactionAborted() {
            Toast.makeText(getApplicationContext(), "Transaction cancelled!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void endUrlReached(String url) {
            Toast.makeText(getApplicationContext(), "Transaction successful!", Toast.LENGTH_LONG).show();
        }
    };

}
