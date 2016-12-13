package com.androidrecipes.imagemask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class RoundedCornerImageView extends View {

    private Bitmap mImage;
    private Paint mBitmapPaint;

    private RectF mBounds;
    private float mRadius = 25.0f;

    public RoundedCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundedCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedCornerImageView(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //Create image paint
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //Create rect for drawing bounds
        mBounds = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height, width;
        height = width = 0;
        //Requested size is the image content size
        int imageHeight, imageWidth;
        if (mImage == null) {
            imageHeight = imageWidth = 0;
        } else {
            imageHeight = mImage.getHeight();
            imageWidth = mImage.getWidth();
        }
        //Get the best measurement and set it on the view
        width = getMeasurement(widthMeasureSpec, imageWidth);
        height = getMeasurement(heightMeasureSpec, imageHeight);

        setMeasuredDimension(width, height);
    }

    /*
     * Helper method to measure width and height
     */
    private int getMeasurement(int measureSpec, int contentSize) {
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:
                return Math.min(specSize, contentSize);
            case MeasureSpec.UNSPECIFIED:
                return contentSize;
            case MeasureSpec.EXACTLY:
                return specSize;
            default:
                return 0;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            //We want to center the image, so we need to offset our
            //values whenever the view changes size
            int imageWidth, imageHeight;
            if (mImage == null) {
                imageWidth = imageHeight = 0;
            } else {
                imageWidth = mImage.getWidth();
                imageHeight = mImage.getHeight();
            }
            int left = (w - imageWidth) / 2;
            int top = (h - imageHeight) / 2;
            //Set the bounds to offset the rounded rectangle
            mBounds.set(left, top, left + imageWidth, top + imageHeight);
            //Offset the shader to draw the Bitmap inside the centered space
            // Without this, the bitmap still be a 0,0 in the view
            if (mBitmapPaint.getShader() != null) {
                Matrix m = new Matrix();
                m.setTranslate(left, top);
                mBitmapPaint.getShader().setLocalMatrix(m);
            }
        }
    }

    public void setImage(Bitmap bitmap) {
        if (mImage != bitmap) {
            mImage = bitmap;
            if (mImage != null) {
                BitmapShader shader = new BitmapShader(mImage, TileMode.CLAMP, TileMode.CLAMP);
                mBitmapPaint.setShader(shader);
            } else {
                mBitmapPaint.setShader(null);
            }
            requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Let the view draw backgrounds, etc.
        super.onDraw(canvas);
        //Draw the image with the calculated values
        if (mBitmapPaint != null) {
            canvas.drawRoundRect(mBounds, mRadius, mRadius, mBitmapPaint);
        }
    }
}
