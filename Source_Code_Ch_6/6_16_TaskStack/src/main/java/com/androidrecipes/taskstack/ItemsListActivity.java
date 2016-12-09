package com.androidrecipes.taskstack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@SuppressLint("NewApi")
public class ItemsListActivity extends Activity implements OnItemClickListener {

    private static final String[] ITEMS = {"Mom", "Dad", "Sister", "Brother", "Cousin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Enable ActionBar home button with up arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //Create and display a list of family members
        ListView list = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ITEMS);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        setContentView(list);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //Launch the final Activity, passing in the selected item name
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, ITEMS[position]);
        startActivity(intent);
    }
}
