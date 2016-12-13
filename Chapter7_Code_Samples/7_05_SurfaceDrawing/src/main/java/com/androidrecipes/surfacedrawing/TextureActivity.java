package com.androidrecipes.surfacedrawing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureActivity extends Activity implements
        View.OnClickListener, View.OnTouchListener, TextureView.SurfaceTextureListener {

    private TextureView mSurface;
    private DrawingThread mThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texture);
        //Attach listener to button
        findViewById(R.id.button_transform).setOnClickListener(this);

        //Set up the surface with a touch listener and callback
        mSurface = (TextureView) findViewById(R.id.surface);
        mSurface.setOnTouchListener(this);
        mSurface.setSurfaceTextureListener(this);
    }

    @Override
    public void onClick(View v) {
        //Rotate the entire drawing view
        mSurface.animate()
                .rotation(mSurface.getRotation() < 180.f ? 180.f : 0.f);
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mThread.addItem((int) event.getX(), (int) event.getY());
        }
        return true;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                          int height) {
        mThread = new DrawingThread(new Surface(surface),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        mThread.updateSize(width, height);
        mThread.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {
        mThread.updateSize(width, height);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mThread.quit();
        mThread = null;

        //Return true to allow the framework to release the surface
        return true;
    }

    private static class DrawingThread extends HandlerThread implements Handler.Callback {
        private static final int MSG_ADD = 100;
        private static final int MSG_MOVE = 101;
        private static final int MSG_CLEAR = 102;

        private int mDrawingWidth, mDrawingHeight;
        private boolean mRunning = false;

        private Surface mDrawingSurface;
        private Rect mSurfaceRect;
        private Paint mPaint;

        private Handler mReceiver;
        private Bitmap mIcon;
        private ArrayList<DrawingItem> mLocations;

        private class DrawingItem {
            //Current location marker
            int x, y;
            //Direction markers for motion
            boolean horizontal, vertical;

            public DrawingItem(int x, int y, boolean horizontal, boolean vertical) {
                this.x = x;
                this.y = y;
                this.horizontal = horizontal;
                this.vertical = vertical;
            }
        }

        public DrawingThread(Surface surface, Bitmap icon) {
            super("DrawingThread");
            mDrawingSurface = surface;
            mSurfaceRect = new Rect();
            mLocations = new ArrayList<DrawingItem>();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mIcon = icon;
        }

        @Override
        protected void onLooperPrepared() {
            mReceiver = new Handler(getLooper(), this);
            //Start the rendering
            mRunning = true;
            mReceiver.sendEmptyMessage(MSG_MOVE);
        }

        @Override
        public boolean quit() {
            // Clear all messages before dying
            mRunning = false;
            mReceiver.removeCallbacksAndMessages(null);

            return super.quit();
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ADD:
                    //Create a new item at the touch location, with a randomized start direction
                    DrawingItem newItem = new DrawingItem(msg.arg1, msg.arg2,
                            Math.round(Math.random()) == 0,
                            Math.round(Math.random()) == 0);
                    mLocations.add(newItem);
                    break;
                case MSG_CLEAR:
                    //Remove all objects
                    mLocations.clear();
                    break;
                case MSG_MOVE:
                    //Do nothing if we are canceled
                    if (!mRunning) return true;

                    //Render a frame
                    try {
                        Canvas c = mDrawingSurface.lockCanvas(mSurfaceRect);

                        //Clear canvas first
                        c.drawColor(Color.BLACK);
                        //Draw each item
                        for (DrawingItem item : mLocations) {
                            //Update location
                            item.x += (item.horizontal ? 3 : -3);
                            if (item.x >= (mDrawingWidth - mIcon.getWidth()))
                                item.horizontal = false;
                            if (item.x <= 0) item.horizontal = true;
                            item.y += (item.vertical ? 3 : -3);
                            if (item.y >= (mDrawingHeight - mIcon.getHeight()))
                                item.vertical = false;
                            if (item.y <= 0) item.vertical = true;

                            c.drawBitmap(mIcon, item.x, item.y, mPaint);
                        }

                        mDrawingSurface.unlockCanvasAndPost(c);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            //Post the next frame
            if (mRunning) {
                mReceiver.sendEmptyMessage(MSG_MOVE);
            }

            return true;
        }

        public void updateSize(int width, int height) {
            mDrawingWidth = width;
            mDrawingHeight = height;
            mSurfaceRect.set(0, 0, mDrawingWidth, mDrawingHeight);
        }

        public void addItem(int x, int y) {
            //Pass the location into the Handler using Message arguments
            Message msg = Message.obtain(mReceiver, MSG_ADD, x, y);
            mReceiver.sendMessage(msg);
        }

        public void clearItems() {
            mReceiver.sendEmptyMessage(MSG_CLEAR);
        }
    }
}
