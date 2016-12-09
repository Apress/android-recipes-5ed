/*
 * Copyright (C) 2011 by Mark Doffman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE
 */
package com.androidrecipes.dragtouch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.DragShadowBuilder;

public class DrawableDragShadowBuilder extends DragShadowBuilder {
    private Drawable mDrawable;

    public DrawableDragShadowBuilder(View view, Drawable drawable) {
        super(view);
        // Set the drawable and apply a green filter to it
        mDrawable = drawable;
        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY));
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point touchPoint) {
        // Fill in the size
        shadowSize.x = mDrawable.getIntrinsicWidth();
        shadowSize.y = mDrawable.getIntrinsicHeight();
        // Fill in the location of the shadow relative to the touch.
        // Here we center the shadow under the finger.
        touchPoint.x = mDrawable.getIntrinsicWidth() / 2;
        touchPoint.y = mDrawable.getIntrinsicHeight() / 2;

        mDrawable.setBounds(new Rect(0, 0, shadowSize.x, shadowSize.y));
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        //Draw the shadow view onto the provided canvas
        mDrawable.draw(canvas);
    }
}
