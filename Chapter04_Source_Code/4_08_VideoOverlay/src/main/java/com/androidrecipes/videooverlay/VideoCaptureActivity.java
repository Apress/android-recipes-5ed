package com.androidrecipes.videooverlay;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class VideoCaptureActivity extends Activity implements SurfaceHolder.Callback {

    private Camera mCamera;
    private MediaRecorder mRecorder;

    private SurfaceView mPreview;
    private Button mRecordButton;

    private boolean mRecording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mRecordButton = (Button) findViewById(R.id.button_record);
        mRecordButton.setText("Start Recording");

        mPreview = (SurfaceView) findViewById(R.id.surface_video);
        mPreview.getHolder().addCallback(this);

        mCamera = Camera.open();
        //Rotate the preview display to match portrait
        mCamera.setDisplayOrientation(90);
        mRecorder = new MediaRecorder();
    }

    @Override
    protected void onDestroy() {
        mCamera.release();
        mCamera = null;
        super.onDestroy();
    }

    public void onRecordClick(View v) {
        updateRecordingState();
    }

    /*
     * Initialize the camera and recorder.
     * The order of these methods are important because MediaRecorder is a fairly
     * strict state machine that moves through states as the methods are called.
     */
    private void initializeRecorder() throws IllegalStateException, IOException {
        //Unlock the camera to let MediaRecorder use it
        mCamera.unlock();
        mRecorder.setCamera(mCamera);
        //Update the source settings
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //Update the output settings
        File recordOutput = new File(Environment.getExternalStorageDirectory(), "recorded_video.mp4");
        if (recordOutput.exists()) {
            recordOutput.delete();
        }
        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mRecorder.setProfile(cpHigh);
        mRecorder.setOutputFile(recordOutput.getAbsolutePath());
        //Attach the surface to the recorder to allow preview while recording
        mRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        //Optionally, set limit values on recording
        mRecorder.setMaxDuration(50000); // 50 seconds
        mRecorder.setMaxFileSize(5000000); // Approximately 5 megabytes

        mRecorder.prepare();
    }

    private void updateRecordingState() {
        if (mRecording) {
            mRecording = false;
            //Reset the recorder state for the next recording
            mRecorder.stop();
            mRecorder.reset();
            //Take the camera back to let preview continue
            mCamera.lock();
            mRecordButton.setText("Start Recording");
        } else {
            try {
                //Reset the recorder for the next session
                initializeRecorder();
                //Start recording
                mRecording = true;
                mRecorder.start();
                mRecordButton.setText("Stop Recording");
            } catch (Exception e) {
                //Error occurred initializing recorder
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //When we get a surface, immediately start camera preview
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
