package com.dhodu.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public abstract class SMSReceiver extends BroadcastReceiver {

    public static final String TAG = "Receiver";

    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.d(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains("dm-notify")) {
                        return;
                    } else {
                        // verification code from sms
                        String verificationCode = getVerificationCode(message);
                        Log.d(TAG, "OTP received: " + verificationCode);

                        smsReceived(verificationCode);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    protected abstract void smsReceived(String s);

    private String getVerificationCode(String message) {
        return String.valueOf(Integer.parseInt(message.replaceAll("[\\D]", "")));
    }
}
