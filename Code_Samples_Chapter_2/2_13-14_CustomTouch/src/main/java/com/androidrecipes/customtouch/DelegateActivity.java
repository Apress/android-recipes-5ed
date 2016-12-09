package com.androidrecipes.customtouch;

import android.app.Activity;
import android.os.Bundle;

public class DelegateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TouchDelegateLayout layout = new TouchDelegateLayout(this);

        setContentView(layout);
    }
}
