package com.dhodu.android.payments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dhodu.android.R;
import com.vinaygaba.creditcardview.CreditCardView;

/**
 * Created by naman on 06/11/15.
 */
public class AddCardActivity extends AppCompatActivity {

    Toolbar toolbar;
    CreditCardView creditCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        creditCardView = (CreditCardView) findViewById(R.id.credit_card);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add card");
    }
}
