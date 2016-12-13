package com.androidrecipes.audioin;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class RecordActivity extends Activity {

    private MediaRecorder recorder;
    private Button start, stop;
    File path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        start = (Button) findViewById(R.id.startButton);
        start.setOnClickListener(startListener);
        stop = (Button) findViewById(R.id.stopButton);
        stop.setOnClickListener(stopListener);

        recorder = new MediaRecorder();
        path = new File(Environment.getExternalStorageDirectory(), "myRecording.3gp");

        resetRecorder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recorder.release();
    }

    private void resetRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(path.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                recorder.start();

                start.setEnabled(false);
                stop.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener stopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            recorder.stop();
            resetRecorder();

            start.setEnabled(true);
            stop.setEnabled(false);
        }
    };
}