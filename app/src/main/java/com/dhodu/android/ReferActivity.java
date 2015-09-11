package com.dhodu.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        Button shareCode = (Button) findViewById(R.id.share_code);
        final TextView code = (TextView) findViewById(R.id.referral_code);

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
