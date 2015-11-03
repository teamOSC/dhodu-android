package com.dhodu.android;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.parse.ParseObject;

import java.util.List;

public class RateCardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ParseObject> clothesList;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            Adapter adapter = new Adapter(getSupportFragmentManager());
            adapter.addFragment(RateCardFragment.newInstance(RateCardFragment.MENS_WEAR), RateCardFragment.MENS_WEAR);
            adapter.addFragment(RateCardFragment.newInstance(RateCardFragment.WOMEN_WEAR), RateCardFragment.WOMEN_WEAR);
            adapter.addFragment(RateCardFragment.newInstance(RateCardFragment.HOUSEHOLD), RateCardFragment.HOUSEHOLD);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(2);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
}
