package com.dhodu.android;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About Us");

        TextView version = (TextView) findViewById(R.id.version);
        final TextView aboutDhodu = (TextView) findViewById(R.id.about_dhodu);
        LinearLayout privacyPolicy = (LinearLayout) findViewById(R.id.privacy_policy);
        LinearLayout terms = (LinearLayout) findViewById(R.id.terms_conditions);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final Intent intent = new Intent(this, ParseTextActivity.class);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionname = pInfo.versionName;
            version.setText("v" + versionname);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutActivity.this, NyanCatActivity.class));
            }
        });

        ParseQuery<ParseObject> query = new ParseQuery<>("App");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if(e == null)
                    aboutDhodu.setText(object.getString("about"));
                else{
                    Toast.makeText(AboutActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("query", "terms_conditions");
                startActivity(intent);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("query", "privacy_policy");
                startActivity(intent);
            }
        });

    }
}
