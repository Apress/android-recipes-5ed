package com.examples.animateproperty;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class FlipperPauseActivity extends Activity {

    boolean mIsHeads;
    ObjectAnimator mFlipper;
    Bitmap mHeadsImage, mTailsImage;
    ImageView mFlipImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mHeadsImage = BitmapFactory.decodeResource(getResources(), R.drawable.heads);
        mTailsImage = BitmapFactory.decodeResource(getResources(), R.drawable.tails);

        mFlipImage = (ImageView) findViewById(R.id.flip_image);
        mFlipImage.setImageBitmap(mHeadsImage);
        mIsHeads = true;

        mFlipper = ObjectAnimator.ofFloat(mFlipImage, "rotationY", 0f, 360f);
        mFlipper.setDuration(1500);
        mFlipper.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedFraction() >= 0.25f && mIsHeads) {
                    mFlipImage.setImageBitmap(mTailsImage);
                    mIsHeads = false;
                }
                if (animation.getAnimatedFraction() >= 0.75f && !mIsHeads) {
                    mFlipImage.setImageBitmap(mHeadsImage);
                    mIsHeads = true;
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mFlipper.isPaused()) {
                mFlipper.resume();
            } else {
                mFlipper.start();
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mFlipper.pause();
            return true;
        }
        return super.onTouchEvent(event);
    }
}