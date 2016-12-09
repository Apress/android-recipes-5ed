package com.androidrecipes.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    //Device address we would like to listen for (phone number, shortcode, etc.)
    private static final String SENDER_ADDRESS = "<ENTER YOUR NUMBER HERE>";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        Object[] messages = (Object[]) bundle.get("pdus");
        SmsMessage[] sms = new SmsMessage[messages.length];
        //Create messages for each incoming PDU
        for (int n = 0; n < messages.length; n++) {
            sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }
        for (SmsMessage msg : sms) {
            //Verify if the message came from our known sender
            if (TextUtils.equals(msg.getOriginatingAddress(), SENDER_ADDRESS)) {
                //Keep other apps from processing this incoming message
                abortBroadcast();

                //Display our own notification
                Toast.makeText(context,
                        "Received message from the mothership: "
                                + msg.getMessageBody(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

