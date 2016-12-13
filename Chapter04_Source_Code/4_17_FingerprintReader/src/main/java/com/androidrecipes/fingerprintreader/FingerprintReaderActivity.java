package com.androidrecipes.fingerprintreader;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class FingerprintReaderActivity extends AppCompatActivity {

    private static final int PERM_REQUEST_CODE = 101;
    private static final String KEY_NAME = "AndroidRecipes";
    private static final String TAG = "FingerprintReader";
    private static final String PREFS = "prefs";
    private FingerprintManager mFingerprintManager;
    private CancellationSignal mCancellationSignal;
    private Handler mAuthHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.USE_FINGERPRINT},
                    PERM_REQUEST_CODE);

            return;
        }

        if (!mFingerprintManager.hasEnrolledFingerprints()) {
            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

mCancellationSignal = new CancellationSignal();
HandlerThread handlerThread = new HandlerThread("AuthThread");
handlerThread.start();
mAuthHandler = new Handler(handlerThread.getLooper());
mFingerprintManager.authenticate(null, mCancellationSignal, 0,
    new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Log.d(TAG, "onAuthenticationError: " + errString);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            Log.d(TAG, "onAuthenticationHelp: " + helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Log.d(TAG, "onAuthenticationSucceeded");
            showResult("Authentication succeeded!");
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Log.d(TAG, "onAuthenticationFailed");
            showResult("Authentication failed!");
        }
    },
    mAuthHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCancellationSignal.cancel();
        mAuthHandler.getLooper().quit();
    }

    private void showResult(final String message) {
        Runnable runnable = new Runnable() {
            public void run() {
                ((TextView) findViewById(R.id.auth_result)).setText(message);
            }
        };
        runOnUiThread(runnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERM_REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission " + permission
                                    + " is required for this application to work.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }
        }
    }
}
