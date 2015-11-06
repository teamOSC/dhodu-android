package com.payUMoney.sdk.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.payUMoney.sdk.Constants;
import com.payUMoney.sdk.HomeActivity;
import com.payUMoney.sdk.Luhn;
import com.payUMoney.sdk.R;
import com.payUMoney.sdk.SetupCardDetails;
import com.payUMoney.sdk.dialog.CustomDatePicker;
import com.payUMoney.sdk.entity.Card;
import com.payUMoney.sdk.interfaces.FragmentLifecycle;

import org.json.JSONException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.Inflater;

/**
 * Created by sagar on 20/5/15.
 */
public class Debit extends Fragment implements FragmentLifecycle {

    MakePaymentListener mCallback;

    @Override
    public void onResumeFragment(HomeActivity activity) {


        EditText expiryDatePickerEditText = (EditText)activity.findViewById(R.id.expiryDatePickerEditText);
        String expiryDatePickerEditTextString = expiryDatePickerEditText.getText().toString();

        String delims = "[/]";
        String[] tokens = null;
        if(expiryDatePickerEditTextString != null)
            tokens = expiryDatePickerEditTextString.split(delims);

        if (tokens != null && tokens.length > 0 && isCvvValid && isCardNumberValid && isExpired) {


            int mnths = Integer.parseInt(tokens[0]);
            int yrs = Integer.parseInt(tokens[1]);

            checkExpiry(expiryDatePickerEditText, yrs, mnths, 0);

            valid(expiryDatePickerEditText, calenderDrawable);

        }

    }

    // Container Activity must implement this interface
    public interface MakePaymentListener {
        public void goToPayment(String mode, HashMap<String, Object> data) throws JSONException;
    }

    private int expiryMonth = 7;
    private int expiryYear = 2025;
    private String cardNumber = "";
    private String cvv = "";


    DatePickerDialog.OnDateSetListener mDateSetListener;
    int mYear;
    int mMonth;
    int mDay;

    Boolean isCardNumberValid = false;
    Boolean isExpired = true;
    Boolean isCvvValid = false;
    Boolean card_store_check = true;

    Drawable cardNumberDrawable;
    Drawable calenderDrawable;
    Drawable cvvDrawable;
    private CheckBox mCardStore;
    private EditText mCardLabel;
    View debitCardDetails;
    CustomDatePicker mDatePicker;

    public Debit() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Sagar", "DebitCardFragment" + "onCreateView");
        // Inflate the layout for this fragment
        debitCardDetails = inflater.inflate(R.layout.fragment_card_details, container, false);
        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = Calendar.getInstance().get(Calendar.MONTH);
        mDay = Calendar.getInstance().get(Calendar.DATE);
        mCardLabel = (EditText) debitCardDetails.findViewById(R.id.label);
        mCardStore = (CheckBox) debitCardDetails.findViewById(R.id.store_card);

        super.onActivityCreated(savedInstanceState);

        cardNumberDrawable = getResources().getDrawable(R.drawable.card);
        calenderDrawable = getResources().getDrawable(R.drawable.calendar);
        cvvDrawable = getResources().getDrawable(R.drawable.lock);

        cardNumberDrawable.setAlpha(100);
        calenderDrawable.setAlpha(100);
        cvvDrawable.setAlpha(100);


        ((TextView) debitCardDetails.findViewById(R.id.enterCardDetailsTextView)).setText(getString(R.string.enter_debit_card_details));

        ((EditText) debitCardDetails.findViewById(R.id.cardNumberEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, cardNumberDrawable, null);
        ((EditText) debitCardDetails.findViewById(R.id.expiryDatePickerEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, calenderDrawable, null);
        ((EditText) debitCardDetails.findViewById(R.id.cvvEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, cvvDrawable, null);


        ((EditText) debitCardDetails.findViewById(R.id.cardNumberEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                cardNumber = ((EditText) debitCardDetails.findViewById(R.id.cardNumberEditText)).getText().toString();

                if (cardNumber.startsWith("34") || cardNumber.startsWith("37"))
                    ((EditText) debitCardDetails.findViewById(R.id.cvvEditText)).setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                else
                    ((EditText) debitCardDetails.findViewById(R.id.cvvEditText)).setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});


                if (SetupCardDetails.findIssuer(cardNumber, "DC") == "MAES") {
                    // disable cvv and expiry

                    if (cardNumber.length() > 11 && Luhn.validate(cardNumber)) {
                        isCardNumberValid = true;
                        valid(((EditText) debitCardDetails.findViewById(R.id.cardNumberEditText)), SetupCardDetails.getCardDrawable(getResources(), cardNumber));
                    } else {
                        isCardNumberValid = false;
                        invalid(((EditText) debitCardDetails.findViewById(R.id.cardNumberEditText)), cardNumberDrawable);
                        cardNumberDrawable.setAlpha(100);
                        resetHeader();
                    }
                } else {
                    // enable cvv and expiry
                    debitCardDetails.findViewById(R.id.expiryCvvLinearLayout).setVisibility(View.VISIBLE);

                    if (cardNumber.length() > 11 && Luhn.validate(cardNumber)) {
                        isCardNumberValid = true;
                        valid(((EditText) debitCardDetails.findViewById(R.id.cardNumberEditText)), SetupCardDetails.getCardDrawable(getResources(), cardNumber));
                    } else {
                        isCardNumberValid = false;
                        invalid(((EditText) debitCardDetails.findViewById(R.id.cardNumberEditText)), cardNumberDrawable);
                        cardNumberDrawable.setAlpha(100);
                        resetHeader();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ((EditText) debitCardDetails.findViewById(R.id.cvvEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                cvv = ((EditText) debitCardDetails.findViewById(R.id.cvvEditText)).getText().toString();
                if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) {
                    if (cvv.length() == 4) {
                        //valid
                        isCvvValid = true;
                        valid(((EditText) debitCardDetails.findViewById(R.id.cvvEditText)), cvvDrawable);

                    } else {
                        //invalid
                        isCvvValid = false;
                        invalid(((EditText) debitCardDetails.findViewById(R.id.cvvEditText)), cvvDrawable);
                    }
                } else {
                    if (cvv.length() == 3) {
                        //valid
                        isCvvValid = true;
                        valid(((EditText) debitCardDetails.findViewById(R.id.cvvEditText)), cvvDrawable);
                    } else {
                        //invalid
                        isCvvValid = false;
                        invalid(((EditText) debitCardDetails.findViewById(R.id.cvvEditText)), cvvDrawable);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });


        debitCardDetails.findViewById(R.id.cardNumberEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    makeInvalid();
                }
            }
        });


        debitCardDetails.findViewById(R.id.cvvEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    makeInvalid();
                }
            }
        });

        debitCardDetails.findViewById(R.id.expiryDatePickerEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    makeInvalid();
                }
            }
        });

        mCardStore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mCardStore.isChecked()) {
                    card_store_check = true;
                    mCardLabel.setVisibility(View.VISIBLE);
                } else {
                    card_store_check = false;
                    mCardLabel.setVisibility(View.GONE);
                }
            }
        });


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {

                checkExpiry((EditText)debitCardDetails.findViewById(R.id.expiryDatePickerEditText),i,i2,i3);
            }
        };

        debitCardDetails.findViewById(R.id.expiryDatePickerEditText).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mDatePicker = new CustomDatePicker(getActivity());
                    mDatePicker.build(mMonth, mYear, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //positive button
                            checkExpiry((EditText)debitCardDetails.findViewById(R.id.expiryDatePickerEditText),mDatePicker.getSelectedYear(),mDatePicker.getSelectedMonth(),0);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //negative button
                            mDatePicker.dismissDialog();
                        }
                    });
                    mDatePicker.show();
                }
                return false;
            }
        });
        debitCardDetails.findViewById(R.id.makePayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNumber = ((TextView) debitCardDetails.findViewById(R.id.cardNumberEditText)).getText().toString();


                final HashMap<String, Object> data = new HashMap<String, Object>();
                try {
                    if (cvv.equals("") || cvv == null)
                        data.put(Constants.CVV, "123");
                    else
                        data.put(Constants.CVV, cvv);
                    data.put(Constants.EXPIRY_MONTH, expiryMonth);
                    data.put(Constants.EXPIRY_YEAR, expiryYear);
                    data.put(Constants.NUMBER, cardNumber);

                    data.put("key", ((HomeActivity) getActivity()).getBankObject().getJSONObject("paymentOption").getString("publicKey").replaceAll("\\r", ""));
                    if (Card.isAmex(cardNumber)) {
                        data.put("bankcode", Constants.AMEX);
                    } else {
                        data.put("bankcode", SetupCardDetails.findIssuer(cardNumber, "DC"));
                        //  data.put("bankcode", Card.getType(cardNumber));
                    }
                    if (card_store_check == true) {
                        if (mCardLabel.getText().toString().trim().length() == 0) {
                            data.put(Constants.LABEL, "PayUmoney Debit Card");
                            data.put(Constants.STORE, "1");
                        } else {
                            data.put(Constants.LABEL, "DC - "+ mCardLabel.getText().toString().toUpperCase());
                            data.put(Constants.STORE, "1");
                        }
                    }

                    mCallback.goToPayment("DC", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        return debitCardDetails;

    }


    private void checkExpiry(EditText expiryDatePickerEditText, int i, int i2, int i3){

        expiryDatePickerEditText.setText("" + (i2 + 1) + " / " + i);

        expiryMonth = i2 + 1;
        expiryYear = i;
        if (expiryYear > Calendar.getInstance().get(Calendar.YEAR)) {
            isExpired = false;
            valid(expiryDatePickerEditText, calenderDrawable);
        } else if (expiryYear == Calendar.getInstance().get(Calendar.YEAR) && expiryMonth - 1 >= Calendar.getInstance().get(Calendar.MONTH)) {
            isExpired = false;
            valid(expiryDatePickerEditText, calenderDrawable);
        } else {
            isExpired = true;
            invalid(expiryDatePickerEditText, calenderDrawable);
        }
    }

    private void valid(EditText editText, Drawable drawable) {
        drawable.setAlpha(255);
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        if (debitCardDetails.findViewById(R.id.expiryCvvLinearLayout).getVisibility() == View.GONE) {
            isExpired = false;
            isCvvValid = true;
        }
        if (isCardNumberValid && !isExpired && isCvvValid) {
            debitCardDetails.findViewById(R.id.makePayment).setEnabled(true);
//            debitCardDetails.findViewById(R.id.makePayment).setBackgroundResource(R.drawable.button_enabled);
        } else {
            debitCardDetails.findViewById(R.id.makePayment).setEnabled(false);
//            debitCardDetails.findViewById(R.id.makePayment).setBackgroundResource(R.drawable.button);
        }
    }

    private void invalid(EditText editText, Drawable drawable) {
        drawable.setAlpha(100);
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        debitCardDetails.findViewById(R.id.makePayment).setEnabled(false);
        debitCardDetails.findViewById(R.id.makePayment).setBackgroundResource(R.drawable.button);
    }

    private void makeInvalid() {
        if (!isCardNumberValid && cardNumber.length() > 0 && !debitCardDetails.findViewById(R.id.cardNumberEditText).isFocused())
            ((EditText) debitCardDetails.findViewById(R.id.cardNumberEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.error_icon), null);
        if (!isCvvValid && cvv.length() > 0 && !debitCardDetails.findViewById(R.id.cvvEditText).isFocused())
            ((EditText) debitCardDetails.findViewById(R.id.cvvEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.error_icon), null);
    }

    private void resetHeader() {
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (MakePaymentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public void onDestroy() {
        Log.e("ACT", "onDestroy called");

        debitCardDetails = null;

        super.onDestroy();
    }
}
