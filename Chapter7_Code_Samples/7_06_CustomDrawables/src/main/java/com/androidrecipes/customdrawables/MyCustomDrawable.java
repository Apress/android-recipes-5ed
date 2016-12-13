package com.androidrecipes.customdrawables;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MyCustomDrawable extends Drawable {
    private final Paint mPaint;

    public MyCustomDrawable() {
        // Default values for our Paint object
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setAntiAlias(true);
        mPaint.setShadowLayer(4, 4, 4, Color.GRAY);
    }

    @Override
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser,
                        @NonNull AttributeSet attrs, Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        TypedArray typedArray = null;
        try {
            typedArray = r.obtainAttributes(attrs, new int[]{android.R.attr.color});
            int color = typedArray.getColor(0, Color.BLACK);
            mPaint.setColor(color);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float radius = (width > height ? height * 0.4f : width * 0.4f);
        canvas.drawCircle(width / 2f, height / 2f, radius, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
