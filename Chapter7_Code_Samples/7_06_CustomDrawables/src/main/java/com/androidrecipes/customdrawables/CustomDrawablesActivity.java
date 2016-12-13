package com.androidrecipes.customdrawables;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.widget.ImageView;

public class CustomDrawablesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            ((ImageView) findViewById(R.id.image)).setImageDrawable(new MyCustomDrawable());
        }
    }
}
