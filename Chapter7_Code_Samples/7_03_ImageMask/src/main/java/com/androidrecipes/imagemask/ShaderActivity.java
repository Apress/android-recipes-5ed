package com.androidrecipes.imagemask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class ShaderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoundedCornerImageView iv = new RoundedCornerImageView(this);
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.dog);

        iv.setImage(source);
        setContentView(iv);
    }
}
