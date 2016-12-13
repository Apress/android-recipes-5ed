package com.examples.animateviewproperty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AnimateActivity extends Activity implements View.OnClickListener {

    private View mViewToAnimate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button button = (Button) findViewById(R.id.toggleButton);
        button.setOnClickListener(this);

        mViewToAnimate = findViewById(R.id.theView);
    }

    @Override
    public void onClick(View v) {
        if (mViewToAnimate.getAlpha() > 0f) {
            //If the view is visible already, slide it out to the right
            mViewToAnimate.animate().alpha(0f).translationX(500f);
        } else {
            //If the view is hidden, do a fade-in in-place
            //Property Animations actually modify the view, so
            // we have to reset the view's location first
            mViewToAnimate.setTranslationX(0f);
            mViewToAnimate.animate().alpha(1f);
        }
    }
}
