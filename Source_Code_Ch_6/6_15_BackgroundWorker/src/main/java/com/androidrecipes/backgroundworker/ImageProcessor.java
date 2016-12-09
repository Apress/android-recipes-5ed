package com.androidrecipes.backgroundworker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class ImageProcessor extends HandlerThread implements Handler.Callback {
    public static final int MSG_SCALE = 100;
    public static final int MSG_CROP = 101;

    private Context mContext;
    private Handler mReceiver, mCallback;

    public ImageProcessor(Context context) {
        this(context, null);
    }

    public ImageProcessor(Context context, Handler callback) {
        super("AndroidRecipesWorker");
        mCallback = callback;
        mContext = context.getApplicationContext();
    }

    @Override
    protected void onLooperPrepared() {
        mReceiver = new Handler(getLooper(), this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        Bitmap source, result;
        //Retrieve arguments from the incoming message
        int scale = msg.arg1;
        switch (msg.what) {
            case MSG_SCALE:
                source = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.ic_launcher);
                //Create a new, scaled up image
                result = Bitmap.createScaledBitmap(source,
                        source.getWidth() * scale, source.getHeight() * scale, true);
                break;
            case MSG_CROP:
                source = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.ic_launcher);
                int newWidth = source.getWidth() / scale;
                //Create a new, horizontally cropped image
                result = Bitmap.createBitmap(source,
                        (source.getWidth() - newWidth) / 2, 0,
                        newWidth, source.getHeight());
                break;
            default:
                throw new IllegalArgumentException("Unknown Worker Request");
        }

        // Return the image to the main thread
        if (mCallback != null) {
            mCallback.sendMessage(Message.obtain(null, 0, result));
        }
        return true;
    }

    //Add/Remove a callback handler
    public void setCallback(Handler callback) {
        mCallback = callback;
    }
    
    /* Methods to Queue Work */

    // Scale the icon to the specified value
    public void scaleIcon(int scale) {
        Message msg = Message.obtain(null, MSG_SCALE, scale, 0, null);
        mReceiver.sendMessage(msg);
    }

    //Crop the icon in the center and scale the result to the specified value
    public void cropIcon(int scale) {
        Message msg = Message.obtain(null, MSG_CROP, scale, 0, null);
        mReceiver.sendMessage(msg);
    }
}
