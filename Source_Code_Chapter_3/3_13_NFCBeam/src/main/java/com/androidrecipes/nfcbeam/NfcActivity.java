package com.androidrecipes.nfcbeam;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

public class NfcActivity extends Activity implements
        CreateNdefMessageCallback, OnNdefPushCompleteCallback {
    private static final String TAG = "NfcBeam";
    private NfcAdapter mNfcAdapter;
    private TextView mDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisplay = new TextView(this);
        setContentView(mDisplay);

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            mDisplay.setText("NFC is not available on this device.");
        } else {
            // Register callback to set NDEF message.  Setting this makes
            // NFC data push active while the Activity is in the foreground.
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see if a Beam launched this Activity
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        mDisplay.setText(new String(msg.getRecords()[0].getPayload()));
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = String.format("Sending A Message From Android Recipes at %s",
                DateFormat.getTimeFormat(this).format(new Date()));
        NdefMessage msg = new NdefMessage(NdefRecord.createMime(
                "application/com.example.androidrecipes.beamtext", text.getBytes())
                /**
                 * The Android Application Record (AAR) is commented out. When a device
                 * receives a push with an AAR in it, the application specified in the AAR
                 * is guaranteed to run. The AAR overrides the tag dispatch system.
                 * You can add it back in to guarantee that this
                 * activity starts when receiving a beamed message. For now, this code
                 * uses the tag dispatch system.
                 */
                //,NdefRecord.createApplicationRecord("com.examples.nfcbeam")
        );
        return msg;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        //This callback happens on a binder thread, don't update
        // the UI directly from this method.
        Log.i(TAG, "Message Sent!");
    }
}
