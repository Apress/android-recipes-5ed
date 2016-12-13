package com.androidrecipes.imageprocessing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class ImageProcessingActivity extends Activity implements
        View.OnClickListener {

    private ImageView mImage;
    private SeekBar mAmplitude, mDampening, mFrequency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImage = (ImageView) findViewById(R.id.image);
        mAmplitude = (SeekBar) findViewById(R.id.control_amplitude);
        mDampening = (SeekBar) findViewById(R.id.control_dampening);
        mFrequency = (SeekBar) findViewById(R.id.control_frequency);

        /*
         * Settings Ranges:
         * A = 0.01 - 1.0
         * D = 0.0001 - 0.01
         * F = 0.01 - 0.5
         */

        mAmplitude.setProgress(40);
        mDampening.setProgress(20);

        mFrequency.setProgress(10);
        mFrequency.setMax(50);

        mImage.setImageResource(R.drawable.background);

        findViewById(R.id.button_enhance).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        drawRipples(mImage, R.drawable.background);
    }

    private void drawRipples(ImageView iv, int imID) {
        Bitmap bmIn = BitmapFactory.decodeResource(
                getResources(), imID);
        Bitmap bmOut = Bitmap.createBitmap(bmIn.getWidth(),
                bmIn.getHeight(), bmIn.getConfig());

        //Initialize the RenderScript context
        RenderScript rs = RenderScript.create(this);
        //Create data allocations
        Allocation allocIn = Allocation.createFromBitmap(rs, bmIn,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        Allocation allocOut = Allocation.createTyped(rs,
                allocIn.getType());
        //Obtain script instance and initial parameters
        ScriptC_ripple script = new ScriptC_ripple(rs,
                getResources(), R.raw.ripple);

        //Set up ripple control values
        script.set_centerX(bmIn.getWidth() / 2);
        script.set_centerY(bmIn.getHeight() / 2);
        script.set_minRadius(0f);
        //Grab user controls from the UI
        float amplitude = Math.max(0.01f, mAmplitude.getProgress() / 100f);
        script.set_scalar(amplitude);
        float dampening = Math.max(0.0001f, mDampening.getProgress() / 10000f);
        script.set_damper(dampening);
        float frequency = Math.max(0.01f, mFrequency.getProgress() / 100f);
        script.set_frequency(frequency);

        //Run the script
        script.forEach_root(allocIn, allocOut);

        allocOut.copyTo(bmOut);
        iv.setImageBitmap(bmOut);
        //Tear down the RenderScript context
        rs.destroy();
    }

}
