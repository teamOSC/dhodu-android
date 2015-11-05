package com.dhodu.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.BranchShortLinkBuilder;

public class ReferActivity extends AppCompatActivity {

    public static final String TAG = "ReferActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Branch branch = Branch.getInstance(getApplicationContext());

        Button shareCode = (Button) findViewById(R.id.share_code);
        final TextView code = (TextView) findViewById(R.id.referral_code);
        final TextView credits = (TextView) findViewById(R.id.credits);

        credits.setText(branch.getCredits() + "");
        branch.loadRewards(new Branch.BranchReferralStateChangedListener() {
            @Override
            public void onStateChanged(boolean changed, BranchError error) {
                if (error == null && changed)
                    credits.setText(branch.getCredits() + "");
                else if (error != null) {
                    Log.d(TAG, error.getMessage());
                }
            }
        });

        BranchShortLinkBuilder shortUrlBuilder = new BranchShortLinkBuilder(ReferActivity.this);
        shortUrlBuilder.generateShortUrl(new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error != null) {
                    Toast.makeText(ReferActivity.this, "Error generating referral URL", Toast.LENGTH_SHORT).show();
                    Log.e("Branch Error", "Branch create short url failed. Caused by -" + error.getMessage());
                } else {
                    code.setText(url);
                }
            }
        });

        shareCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, "Hey! I just used Dhodu for laundry and it is amazing. Try it out and get ₹100 off on your first order, use my code " +
                        code.getText().toString() + ". Download now - http://www.dhodu.com");
                try {
                    startActivity(Intent.createChooser(i, "Share via"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ReferActivity.this, "No application available to share code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
