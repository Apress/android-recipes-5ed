package com.androidrecipes.textwatcher;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MyActivity extends Activity implements TextWatcher {

    EditText text;
    int textCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Create an EditText widget and add the watcher
        text = new EditText(this);
        text.addTextChangedListener(this);

        setContentView(text);
    }

    /* TextWatcher Implementation Methods */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int end) {
        textCount = text.getText().length();
        setTitle(String.valueOf(textCount));
    }

    public void afterTextChanged(Editable s) {
    }

}
