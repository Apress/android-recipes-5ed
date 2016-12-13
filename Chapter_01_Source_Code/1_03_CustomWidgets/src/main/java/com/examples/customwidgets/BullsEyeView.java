package com.examples.customwidgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class BullsEyeView extends View {

    private Paint mPaint;

    private Point mCenter;
    private float mRadius;

    /*
     * Java Constructor
     */
    public BullsEyeView(Context context) {
        this(context, null);
    }

    /*
     * XML Constructor
     */
    public BullsEyeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /*
     * XML Constructor with Style
     * Do any initialization of your view in this constructor
     */
    public BullsEyeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //Create a paintbrush to draw with
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //We want to draw our circles filled in
        mPaint.setStyle(Style.FILL);
        //Create the center point for our circle
        mCenter = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        //Determine the ideal size of your content, unconstrained
        int contentWidth = 200;
        int contentHeight = 200;

        width = getMeasurement(widthMeasureSpec, contentWidth);
        height = getMeasurement(heightMeasureSpec, contentHeight);
        //MUST call this method with the measured values!
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
            //If there was a change, reset the parameters
            mCenter.x = w / 2;
            mCenter.y = h / 2;
            mRadius = Math.min(mCenter.x, mCenter.y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Draw a series of concentric circles,
        // smallest to largest, alternating colors
        mPaint.setColor(Color.RED);
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius, mPaint);

        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius * 0.8f, mPaint);

        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius * 0.6F, mPaint);

        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius * 0.4F, mPaint);

        mPaint.setColor(Color.RED);
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius * 0.2F, mPaint);
    }
}
