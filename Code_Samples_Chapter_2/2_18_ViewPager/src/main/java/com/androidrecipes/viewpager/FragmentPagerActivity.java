package com.androidrecipes.viewpager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

public class FragmentPagerActivity extends AppCompatActivity {

    private ArrayList<String> mListItems;
    private ListPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Create the initial data set
        mListItems = new ArrayList<String>();
        mListItems.add("Mom");
        mListItems.add("Dad");
        mListItems.add("Sister");
        mListItems.add("Brother");
        mListItems.add("Cousin");
        mListItems.add("Niece");
        mListItems.add("Nephew");
        //Attach the data to the pager
        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new ListPagerAdapter(getSupportFragmentManager(), mListItems);

        pager.setAdapter(mAdapter);
    }

    public void onAddClick(View v) {
        //Add a new unique item to the end of the list
        mListItems.add("Crazy Uncle " + System.currentTimeMillis());
        mAdapter.notifyDataSetChanged();
    }

    public void onRemoveClick(View v) {
        //Remove an item from the head of the list
        if (!mListItems.isEmpty()) {
            mListItems.remove(0);
        }
        mAdapter.notifyDataSetChanged();
    }
}
