package com.androidrecipes.viewpager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class PagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPager pager = new ViewPager(this);
        pager.setAdapter(new ImagePagerAdapter(this));

        setContentView(pager);
    }
}
