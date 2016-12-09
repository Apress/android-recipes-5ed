package com.androidrecipes.restrictedprofiles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Paint mFingerPaint;
    private Path mPath;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //Set up the paint brush
        mFingerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFingerPaint.setStyle(Style.STROKE);
        mFingerPaint.setStrokeCap(Cap.ROUND);
        mFingerPaint.setStrokeJoin(Join.ROUND);
        //Default stroke width
        mFingerPaint.setStrokeWidth(8f);
    }

    public void setPaintColor(int color) {
        mFingerPaint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        mFingerPaint.setStrokeWidth(width);
    }

    public void setCanvasColor(int color) {
        setBackgroundColor(color);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPath = new Path();
                //Start at the touch down
                mPath.moveTo(event.getX(), event.getY());
                //Re-draw
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //Add all touch points between events
                for (int i = 0; i < event.getHistorySize(); i++) {
                    mPath.lineTo(event.getHistoricalX(i),
                            event.getHistoricalY(i));
                }
                //Re-draw
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Draw the background
        super.onDraw(canvas);
        //Draw the paint stroke
        if (mPath != null) {
            canvas.drawPath(mPath, mFingerPaint);
        }
    }
}
