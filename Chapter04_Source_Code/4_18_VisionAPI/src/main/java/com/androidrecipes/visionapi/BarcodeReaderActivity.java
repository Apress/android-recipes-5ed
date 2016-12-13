package com.androidrecipes.visionapi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarcodeReaderActivity extends AppCompatActivity {

    private static final String TAG = "BarcodeReader";
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private GoogleApiClient mApiClient;
    private BarcodeDetector mQrCodeDetector;
    private CameraSource mCameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);

        //Verify play services is active and up to date
        int resultCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);
        switch (resultCode) {
            case ConnectionResult.SUCCESS:
                Log.d(TAG, "Google Play Services is ready to go!");
                break;
            default:
                showPlayServicesError(resultCode);
                return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupQrCodeScanning();
    }

private void setupQrCodeScanning() {
    // Check for Camera permission!
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE_PERMISSIONS);
        return;
    }

    mQrCodeDetector = new BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build();
    Detector.Processor<Barcode> barcodeProcessor = new MyQRCodeProcessor();
    mQrCodeDetector.setProcessor(barcodeProcessor);

    mCameraSource = new CameraSource.Builder(this, mQrCodeDetector)
            .setAutoFocusEnabled(true)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .build();

    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
    surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
        @SuppressWarnings("MissingPermission")
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                mCameraSource.start(surfaceHolder);
            } catch (IOException e) {
                Log.e(TAG, "Failed to start camera preview.", e);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mCameraSource.stop();
            mQrCodeDetector.release();
        }
    });
}

    /**
     * When Play Services is missing or at the wrong version, the client
     * library will assist with a dialog to help the user update.
     */
    private void showPlayServicesError(int errorCode) {
        GoogleApiAvailability.getInstance()
                .showErrorDialogFragment(this, errorCode, 10,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission " + permission
                            + " is required for this application to work.");
                    finish();
                    return;
                }
            }
        }
    }

private class MyQRCodeProcessor implements Detector.Processor<Barcode> {
    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        SparseArray<Barcode> detectedItems = detections.getDetectedItems();
        int size = detectedItems.size();
        for (int i = 0; i < size; i++) {
            final Barcode barcode = detectedItems.get(i);
            if (barcode != null) {
                Log.d(TAG, "Found QR code: " + barcode.rawValue);
                if (barcode.format == Barcode.URL) {
                    Log.d(TAG, "Found URL: " + barcode.url.url);

                    Runnable runnable = new Runnable() {
                        public void run() {
                            ((TextView) findViewById(R.id.qr_code_result)).setText(barcode.url.url);
                        }
                    };
                    runOnUiThread(runnable);
                }
            }
        }
    }
}
}
