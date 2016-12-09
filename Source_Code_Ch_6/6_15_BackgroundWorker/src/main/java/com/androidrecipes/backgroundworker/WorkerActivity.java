package com.androidrecipes.backgroundworker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

public class WorkerActivity extends Activity implements Handler.Callback {

    private ImageProcessor mWorker;
    private Handler mResponseHandler;

    private ImageView mResultView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mResultView = (ImageView) findViewById(R.id.image_result);
        //Handler to map background callbacks to this Activity
        mResponseHandler = new Handler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Start a new worker
        mWorker = new ImageProcessor(this, mResponseHandler);
        mWorker.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Terminate the worker
        mWorker.setCallback(null);
        mWorker.quit();
        mWorker = null;
    }

    /*
     * Callback method for background results.
     * This is called on the UI thread.
     */
    @Override
    public boolean handleMessage(Message msg) {
        Bitmap result = (Bitmap) msg.obj;
        mResultView.setImageBitmap(result);
        return true;
    }
    
    /* Action Methods to Post Background Work */

    public void onScaleClick(View v) {
        for (int i = 1; i < 10; i++) {
            mWorker.scaleIcon(i);
        }
    }

    public void onCropClick(View v) {
        for (int i = 1; i < 10; i++) {
            mWorker.cropIcon(i);
        }
    }
}
