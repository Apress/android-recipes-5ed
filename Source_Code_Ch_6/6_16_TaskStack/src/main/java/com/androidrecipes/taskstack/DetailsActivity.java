package com.androidrecipes.taskstack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

@SuppressLint("NewApi")
public class DetailsActivity extends Activity {
    //Custom Action String for external Activity launches
    public static final String ACTION_NEW_ARRIVAL =
            "com.examples.taskstack.ACTION_NEW_ARRIVAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Enable ActionBar home button with up arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView text = new TextView(this);
        text.setGravity(Gravity.CENTER);
        String item = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        text.setText(item);

        setContentView(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Create an intent for the parent Activity
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                //Check if we need to create the entire stack
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    //This stack doesn't exist yet, so it must be synthesized
                    TaskStackBuilder.create(this)
                            .addParentStack(this)
                            .startActivities();
                } else {
                    //Stack exists, so just navigate up
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
