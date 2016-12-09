package com.androidrecipes.taskstacklaunch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {
    //Custom Action String for external Activity launches
    public static final String ACTION_NEW_ARRIVAL =
            "com.examples.taskstack.ACTION_NEW_ARRIVAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Attach the button listeners
        findViewById(R.id.button_nephew).setOnClickListener(this);
        findViewById(R.id.button_niece).setOnClickListener(this);
        findViewById(R.id.button_twins).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String newArrival;
        switch (v.getId()) {
            case R.id.button_nephew:
                newArrival = "Baby Nephew";
                break;
            case R.id.button_niece:
                newArrival = "Baby Niece";
                break;
            case R.id.button_twins:
                newArrival = "Twin Nieces!";
                break;
            default:
                return;
        }

        Intent intent = new Intent(ACTION_NEW_ARRIVAL);
        intent.putExtra(Intent.EXTRA_TEXT, newArrival);
        startActivity(intent);
    }
}
