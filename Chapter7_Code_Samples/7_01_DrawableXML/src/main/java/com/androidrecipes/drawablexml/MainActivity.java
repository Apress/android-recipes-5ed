package com.androidrecipes.drawablexml;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    private static final String[] ITEMS = {
            "Gradient Backgrounds", "Bitmap Patterns",
            "9-Patch Background", "Drawable Shapes"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ITEMS));
    }

    @Override
    protected void onListItemClick(ListView parent, View view, int position, long id) {
        Intent intent;

        switch (position) {
            case 0:
                intent = new Intent(this, ListBackgroundActivity.class);
                break;
            case 1:
                intent = new Intent(this, PatternActivity.class);
                break;
            case 2:
                intent = new Intent(this, PatchActivity.class);
                break;
            case 3:
                intent = new Intent(this, RoundedActivity.class);
                break;
            default:
                return;
        }

        startActivity(intent);
    }
}
