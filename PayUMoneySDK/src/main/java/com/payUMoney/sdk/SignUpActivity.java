package com.payUMoney.sdk;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by amit on 25/07/13.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SignUpActivity extends FragmentActivity implements Validator.ValidationListener, LoaderManager.LoaderCallbacks<Cursor> {

    Validator mValidator;
    private Button mSignUp;
    @Required(order = 1, message = "Your email is required")
    @Email(order = 2, message = "This email appears to be invalid")
    private AutoCompleteTextView mEmail;
    @Required(order = 3, message = "Please enter your phone number")
    private AutoCompleteTextView mPhone;
    @Password(order = 5, message = "Password is required")
    @TextRule(order = 6, minLength = 6, message = "Password should be minimum 6 character with atleast 1 alphabet and 1 number")
    private EditText mPassword;
    final String regex = "^(?=.{6,}$)((.*[A-Za-z]+.*[0-9]+|.*[0-9]+.*[A-Za-z]+).*$)";
    Pattern patt = Pattern.compile(regex);
    private Crouton mCrouton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//		if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.FACEBOOK)) {
//			mToken = getIntent().getExtras().getString(Constants.FACEBOOK);
//		}

        setTitle(null);

        mValidator = new Validator(this);

        mValidator.setValidationListener(this);

        mEmail = (AutoCompleteTextView) findViewById(R.id.email);
        mPhone = (AutoCompleteTextView) findViewById(R.id.phone_number);
        mPassword = (EditText) findViewById(R.id.password);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.USER_EMAIL)) {
            mEmail.setText(getIntent().getExtras().getString(Constants.USER_EMAIL));
            mPhone.setText(getIntent().getExtras().getString(Constants.USER_PHONE));
//            new ProgressDialog.Builder(this).setCancelable(false).setMessage(R.string.signing_in).create().show();
        }
        mSignUp = (Button) findViewById(R.id.done);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCrouton != null) {
                    mCrouton.cancel();
                    mCrouton = null;
                }

                mValidator.validate();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            ArrayList<String> myNumbers = new ArrayList<String>();
            Cursor cursor = getContentResolver().query(ContactsContract.Profile.CONTENT_URI, new String[]{ContactsContract.Profile._ID}, null, null, null);

            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                // ok so we have the profile info. Lets get contact info
                Cursor pCur = getContentResolver().query(Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, Uri.encode(cursor.getString(cursor.getColumnIndex(ContactsContract.Profile._ID)))), new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                while (pCur.moveToNext()) {
                    myNumbers.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                }
            }

            mPhone.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, myNumbers));

            mPhone.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        mPhone.showDropDown();
                    }
                    return false;
                }
            });
        }

        mEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mEmail.showDropDown();
                }
                return false;
            }
        });

        ((TextView) findViewById(R.id.tos_n_privacy)).setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    private void populateAutoComplete() {
        if (Build.VERSION.SDK_INT >= 14) {
            // Use ContactsContract.Profile (API 14+)
            getLoaderManager().initLoader(0, null, this);
        } else if (Build.VERSION.SDK_INT >= 8) {
            // Use AccountManager (API 8+)
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        populateAutoComplete();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    public void onEventMainThread(CobbocEvent event) {
        if (event.getType() == CobbocEvent.SIGN_UP) {
            if (event.getStatus()) {

                // we'll now take you to login screen, which will, automatically put in your number, and will now listen for an incoming SMS
                //setResult(RESULT_OK);
                //finish();

                JSONObject jsonObject = (JSONObject) event.getValue();
                Session.getInstance(getApplicationContext()).create(mEmail.getText().toString(), mPassword.getText().toString());
            } else {
                // so we have an error
                mCrouton = Crouton.makeText(this, (String) event.getValue(), Style.ALERT).setConfiguration(Constants.CONFIGURATION_LONG);
                mCrouton.show();
                resetButton();
            }
//		} else if(event.getType() == CobbocEvent.UNKNOWN_ERROR) {
//			Crouton.makeText(this, R.string.unknown_error, Style.ALERT).setConfiguration(Constants.CONFIGURATION_INFINITE).show();
//			resetButton();
        } else if (event.getType() == CobbocEvent.LOGIN) {
            if (event.getStatus()) {

                // we'll now take you to login screen, which will, automatically put in your number, and will now listen for an incoming SMS
                SharedPreferences.Editor editor = getSharedPreferences(Constants.SP_SP_NAME, MODE_PRIVATE).edit();
                editor.putString(Constants.TOKEN, Session.getInstance(getApplicationContext()).getToken());
                editor.putString(Constants.EMAIL, Session.getInstance(getApplicationContext()).getUser().getEmail());

                editor.commit();
                setResult(RESULT_OK);
                finish();
            } else {

            }
//		} else if(event.getType() == CobbocEvent.UNKNOWN_ERROR) {
//			Crouton.makeText(this, R.string.unknown_error, Style.ALERT).setConfiguration(Constants.CONFIGURATION_INFINITE).show();
//			resetButton();
        }
    }

    void resetButton() {
        mPhone.setText("");
        mSignUp.setText(R.string.sign_up);
        mSignUp.setEnabled(true);
    }

    @Override
    public void onValidationSucceeded() {
        if (mCrouton != null) {
            mCrouton.cancel();
        }
        Matcher matcher = patt.matcher(mPassword.getText().toString());
        if (mPhone.getText().toString().length() < 10 || mPhone.getText().toString().charAt(0) == '0') {
            mCrouton = Crouton.makeText(this, "The phone number entered is invalid", Style.ALERT).setConfiguration(Constants.CONFIGURATION_LONG);
            mCrouton.show();
        } else if (!mPassword.getText().toString().matches("^([a-zA-Z+]+[a-zA-Z0-9$&+,:;=?@#|'<>.-^*()%!]*)$")){
            mCrouton = Crouton.makeText(this, "Password must be at least 6 characters and must have an alphabet followed by a number", Style.ALERT).setConfiguration(Constants.CONFIGURATION_LONG);
            mCrouton.show();

        } else {

            Session.getInstance(getApplicationContext()).sign_up(mEmail.getText().toString(), mPhone.getText().toString(), mPassword.getText().toString());
            mSignUp.setText(R.string.please_wait);
            mSignUp.setEnabled(false);
        }
    }

    @Override
    public void onValidationFailed(View view, Rule<?> rule) {
        mCrouton = Crouton.makeText(this, rule.getFailureMessage(), Style.ALERT).setConfiguration(Constants.CONFIGURATION_LONG);
        mCrouton.show();
        view.requestFocus();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmail.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Use an AsyncTask to fetch the user's email addresses on a background thread, and update
     * the email text field with results on the main UI thread.
     */
    private class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<String>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

            return emailAddressCollection;
        }

        @Override
        protected void onPostExecute(List<String> emailAddressCollection) {
            addEmailsToAutoComplete(emailAddressCollection);
        }
    }
}
