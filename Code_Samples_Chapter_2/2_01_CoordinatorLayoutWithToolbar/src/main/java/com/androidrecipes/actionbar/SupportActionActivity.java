package com.androidrecipes.actionbar;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SupportActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();

        //Display home with the "up" arrow indicator
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Set the title text
        actionBar.setTitle("Android Recipes");
        //Set the subtitle text
        actionBar.setSubtitle("ActionBar Recipes");

//        actionBar.setDisplayShowCustomEnabled(true);
//        ImageView imageView = new ImageView(this);
//        imageView.setImageResource(R.drawable.ic_launcher);
//        imageView.setScaleType(ImageView.ScaleType.CENTER);
//        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
//                ActionBar.LayoutParams.MATCH_PARENT,
//                ActionBar.LayoutParams.MATCH_PARENT);
//        actionBar.setCustomView(imageView, lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
