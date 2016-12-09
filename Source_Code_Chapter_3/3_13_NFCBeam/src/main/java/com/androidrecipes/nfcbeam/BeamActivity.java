package com.androidrecipes.nfcbeam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateBeamUrisCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BeamActivity extends Activity implements CreateBeamUrisCallback, OnNdefPushCompleteCallback {
    private static final String TAG = "NfcBeam";
    private static final int PICK_IMAGE = 100;

    private NfcAdapter mNfcAdapter;
    private Uri mSelectedImage;

    private TextView mUriName;
    private ImageView mPreviewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mUriName = (TextView) findViewById(R.id.text_uri);
        mPreviewImage = (ImageView) findViewById(R.id.image_preview);

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            mUriName.setText("NFC is not available on this device.");
        } else {
            // Register callback to set NDEF message
            mNfcAdapter.setBeamPushUrisCallback(this, this);
            // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            mUriName.setText(data.getData().toString());
            mSelectedImage = data.getData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    void processIntent(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            mPreviewImage.setImageURI(data);
        } else {
            mUriName.setText("Received Invalid Image Uri");
        }
    }

    public void onSelectClick(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public Uri[] createBeamUris(NfcEvent event) {
        if (mSelectedImage == null) {
            return null;
        }
        return new Uri[]{mSelectedImage};
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Log.i(TAG, "Push Complete!");
    }
}
