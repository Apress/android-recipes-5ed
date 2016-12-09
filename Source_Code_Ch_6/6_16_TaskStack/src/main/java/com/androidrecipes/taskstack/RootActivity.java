package com.androidrecipes.taskstack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class RootActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button listButton = new Button(this);
        listButton.setText("Show Family Members");
        listButton.setOnClickListener(this);

        setContentView(listButton,
                new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void onClick(View v) {
        //Launch the next Activity
        Intent intent = new Intent(this, ItemsListActivity.class);
        startActivity(intent);
    }
}
