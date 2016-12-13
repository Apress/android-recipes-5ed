package com.androidrecipes.imagefilters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.widget.ImageView;

public class ImageFiltersActivity extends Activity {

    private enum ConvolutionFilter {
        SHARPEN, LIGHTEN, DARKEN, EDGE_DETECT, EMBOSS
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create the source data, and a destination for the filtered results
        Bitmap inBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dog);
        Bitmap outBitmap = inBitmap.copy(inBitmap.getConfig(), true);
        //Show the normal image
        setImageInView(outBitmap.copy(outBitmap.getConfig(), false), R.id.image_normal);
        //Create the RenderScript context
        final RenderScript rs = RenderScript.create(this);
        //Create allocations for input and output data
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());

        //Run blur script
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(4f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(outBitmap);
        setImageInView(outBitmap.copy(outBitmap.getConfig(), false), R.id.image_blurred);

        //Run greyscale script
        final ScriptIntrinsicColorMatrix scriptColor = ScriptIntrinsicColorMatrix.create(rs, Element.U8_4(rs));
        scriptColor.setGreyscale();
        scriptColor.forEach(input, output);
        output.copyTo(outBitmap);
        setImageInView(outBitmap.copy(outBitmap.getConfig(), false), R.id.image_greyscale);

        //Run sharpen script
        ScriptIntrinsicConvolve3x3 scriptC = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        scriptC.setCoefficients(getCoefficients(ConvolutionFilter.SHARPEN));
        scriptC.setInput(input);
        scriptC.forEach(output);
        output.copyTo(outBitmap);
        setImageInView(outBitmap.copy(outBitmap.getConfig(), false), R.id.image_sharpen);

        //Run edge detect script
        scriptC = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        scriptC.setCoefficients(getCoefficients(ConvolutionFilter.EDGE_DETECT));
        scriptC.setInput(input);
        scriptC.forEach(output);
        output.copyTo(outBitmap);
        setImageInView(outBitmap.copy(outBitmap.getConfig(), false), R.id.image_edge);

        //Run emboss script
        scriptC = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        scriptC.setCoefficients(getCoefficients(ConvolutionFilter.EMBOSS));
        scriptC.setInput(input);
        scriptC.forEach(output);
        output.copyTo(outBitmap);
        setImageInView(outBitmap.copy(outBitmap.getConfig(), false), R.id.image_emboss);

        //Tear down the RenderScript context
        rs.destroy();
    }

    private void setImageInView(Bitmap bm, int viewId) {
        ImageView normalImage = (ImageView) findViewById(viewId);
        normalImage.setImageBitmap(bm);
    }

    /*
     * Helper to obtain matrix coefficients for each type of
     * convolution image filter.
     */
    private float[] getCoefficients(ConvolutionFilter filter) {
        switch (filter) {
            case SHARPEN:
                return new float[]{
                        0f, -1f, 0f,
                        -1f, 5f, -1f,
                        0f, -1f, 0f
                };
            case LIGHTEN:
                return new float[]{
                        0f, 0f, 0f,
                        0f, 1.5f, 0f,
                        0f, 0f, 0f
                };
            case DARKEN:
                return new float[]{
                        0f, 0f, 0f,
                        0f, 0.5f, 0f,
                        0f, 0f, 0f
                };
            case EDGE_DETECT:
                return new float[]{
                        0f, 1f, 0f,
                        1f, -4f, 1f,
                        0f, 1f, 0f
                };
            case EMBOSS:
                return new float[]{
                        -2f, -1f, 0f,
                        -1f, 1f, 1f,
                        0f, 1f, 2f
                };
            default:
                return null;
        }
    }
}
