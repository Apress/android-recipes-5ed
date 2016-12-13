package com.androidrecipes.vectors;

import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class VectorActivity extends AppCompatActivity {

    private AnimatedVectorDrawableCompat mAnimatedDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);

        //Set the converted SVG vector as a static image
        ImageView imageView = (ImageView) findViewById(R.id.image_static);
        VectorDrawableCompat vectorDrawableCompat
                = VectorDrawableCompat.create(getResources(), R.drawable.svg_converted, getTheme());
        imageView.setImageDrawable(vectorDrawableCompat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //Create the vector path morph animation
            imageView = (ImageView) findViewById(R.id.image_animated);

            mAnimatedDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animated_check);
            imageView.setImageDrawable(mAnimatedDrawable);
            findViewById(R.id.animate_button).setEnabled(true);
        } else {
            findViewById(R.id.animate_button).setEnabled(false);
        }
    }

    public void onMorphClick(View v) {
        mAnimatedDrawable.start();
    }
}
