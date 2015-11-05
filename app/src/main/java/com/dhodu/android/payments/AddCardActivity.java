package com.dhodu.android.payments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dhodu.android.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.dhodu.android.ui.card.CreditCardView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by naman on 06/11/15.
 */
public class AddCardActivity extends AppCompatActivity {

    Toolbar toolbar;
    CreditCardView creditCardView;
    RadioGroup cardType;
    RadioButton cardTypeDebit, cardTypeCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        creditCardView = (CreditCardView) findViewById(R.id.credit_card);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add card");

        cardType = (RadioGroup) findViewById(R.id.radioGroup);
        cardTypeDebit = (RadioButton) findViewById(R.id.radio_card_debit);
        cardTypeCredit = (RadioButton) findViewById(R.id.radio_card_credit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_card, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_card:
                saveCard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCard() {
        final ProgressDialog pDialog = new ProgressDialog(AddCardActivity.this);


        pDialog.setMessage("Saving card...");

        pDialog.setCancelable(false);
        pDialog.show();
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            String cardName = creditCardView.getCardName();
            String cardNumber = creditCardView.getCardNumber();
            String expiry = creditCardView.getExpiryDate();
            if (!cardName.equals("") && !cardNumber.equals("") && !expiry.equals("")) {
                JSONObject card = new JSONObject();
                try {
                    card.put("card_number", cardNumber);
                    card.put("card_name", cardName);
                    card.put("card_expiry", expiry);
                    card.put("card_type", getCardType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                user.addUnique("cards", card);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            pDialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(AddCardActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                pDialog.dismiss();

            }

        }
    }

    private String getCardType() {
        int id = cardType.getCheckedRadioButtonId();

        switch (id) {
            case R.id.radio_card_debit:
                return "debit";
            case R.id.radio_card_credit:
                return "credit";
            default:
                return "";
        }

    }
}
    