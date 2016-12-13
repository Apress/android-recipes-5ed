package com.androidrecipes.ndkjni;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoText = (TextView) findViewById(R.id.text_info);

        getInfo();
    }

    private void getInfo() {
        String text = String.format("%s CPU with %d core(s)",
                NativeLib.getCpuFamily(), NativeLib.getCpuCount());
        mInfoText.setText(text);
    }
}
