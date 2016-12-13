package com.androidrecipes.backgroundblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BackgroundOverlayView extends ImageView {

    private Paint mPaint;
    private Bitmap mOverlayImage;
    private int mClipOffset;

    /*
     * Customization of ImageView to allow us to draw a
     * composite of two images, but still leverage all
     * the image scaling features of the framework.
     */
    public BackgroundOverlayView(Context context) {
        super(context);
        init();
    }

    public BackgroundOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackgroundOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /*
     * Set the normal and blurred image copies in our view
     */
    public void setImagePair(Bitmap base, Bitmap overlay) {
        mOverlayImage = overlay;
        
        /* Apply the normal image to the base ImageView, which
         * will allow it to apply our ScaleType for us and provide
         * a Matrix we can use to draw both images scaled accordingly
         * later on. This will also invalidate the view to trigger
         * a new draw.
         */
        setImageBitmap(base);
    }

    /*
     * Adjust the vertical point where the normal and blurred
     * copy should switch. 
     */
    public void setOverlayOffset(int overlayOffset) {
        mClipOffset = overlayOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Draw base image first, clipped to the top section
        // We clip the base image to avoid unnecessary overdraw in
        // the bottom section of the view.
        canvas.save();
        canvas.clipRect(getLeft(), getTop(), getRight(), mClipOffset);
        super.onDraw(canvas);
        canvas.restore();

        //Obtain the matrix used to scale the base image, and apply it
        // to the blurred overlay image so the two match up
        final Matrix matrix = getImageMatrix();
        canvas.save();
        canvas.clipRect(getLeft(), mClipOffset, getRight(), getBottom());
        canvas.drawBitmap(mOverlayImage, matrix, mPaint);
        canvas.restore();
    }
}
