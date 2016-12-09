package com.androidrecipes.sms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

@TargetApi(19)
public class SmsActivity extends Activity {
    //Device address where we would like to send (phone number, shortcode, etc.)
    private static final String RECIPIENT_ADDRESS = "<ENTER YOUR NUMBER HERE>";

    /* Custom Action Strings for Result Delivery */
    private static final String ACTION_SENT =
            "com.examples.sms.SENT";
    private static final String ACTION_DELIVERED =
            "com.examples.sms.DELIVERED";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button sendButton = new Button(this);
        sendButton.setText("Hail the Mothership");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS("Beam us up!");
            }
        });

        setContentView(sendButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Monitor status of the operations
        registerReceiver(sent, new IntentFilter(ACTION_SENT));
        registerReceiver(delivered, new IntentFilter(ACTION_DELIVERED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Make sure receivers aren't active while we are in the background
        unregisterReceiver(sent);
        unregisterReceiver(delivered);
    }

    private void sendSMS(String message) {
        //Construct a PendingIntent to fire on SMS sent
        PendingIntent sIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(ACTION_SENT), 0);
        //Construct a PendingIntent to fire on SMS delivery confirmation
        PendingIntent dIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(ACTION_DELIVERED), 0);

        //Send the message
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(RECIPIENT_ADDRESS, null, message,
                sIntent, dIntent);
    }

    /*
     * BroadcastReceiver that is registered to receive events when
     * an SMS message is sent; with the result code.
     */
    private BroadcastReceiver sent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    //Handle sent success
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    //Handle sent error
                    break;
            }
        }
    };

    /*
     * BroadcastReceiver that is registered to receive events when
     * an SMS delivery confirmation is received; with the result code.
     */
    private BroadcastReceiver delivered = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    //Handle delivery success
                    break;
                case Activity.RESULT_CANCELED:
                    //Handle delivery failure
                    break;
            }
        }
    };
}

