package com.androidrecipes.tinting;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ColorFilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        applyIconFilters();
    }

    private void applyIconFilters() {
        ImageView iconView = (ImageView) findViewById(R.id.icon_marker);
        iconView.getDrawable().setColorFilter(0xFFAA00AA, PorterDuff.Mode.SRC_ATOP);

        iconView = (ImageView) findViewById(R.id.icon_gear);
        iconView.getDrawable().setColorFilter(0xFF00AA00, PorterDuff.Mode.SRC_ATOP);

        iconView = (ImageView) findViewById(R.id.icon_check);
        iconView.getDrawable().setColorFilter(0xFF0000AA, PorterDuff.Mode.SRC_ATOP);

        iconView = (ImageView) findViewById(R.id.icon_heart);
        iconView.getDrawable().setColorFilter(0xFFAA0000, PorterDuff.Mode.SRC_ATOP);
    }
}
