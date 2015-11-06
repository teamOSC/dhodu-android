package com.dhodu.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_text);

        final String key = getIntent().getStringExtra("query");
        String title = key.equals("terms_conditions") ? "Terms & Conditions" : "Privacy Policy";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        final TextView textView = (TextView) findViewById(R.id.text);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final ParseQuery<ParseObject> query = new ParseQuery<>("App");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if(e == null)
                    textView.setText(object.getString(key));
                else{
                    Toast.makeText(ParseTextActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });



    }
}
