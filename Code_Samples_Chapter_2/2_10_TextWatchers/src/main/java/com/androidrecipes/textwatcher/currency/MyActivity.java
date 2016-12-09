package com.androidrecipes.textwatcher.currency;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class MyActivity extends Activity {

    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        text = new EditText(this);
        text.addTextChangedListener(new CurrencyTextWatcher());

        setContentView(text);
    }

}
