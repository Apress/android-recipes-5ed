package com.examples.universalapp;

import android.app.Activity;
import android.os.Bundle;

public class UniversalActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}