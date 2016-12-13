package com.androidrecipes.drawablexml;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListBackgroundActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater()
                            .inflate(android.R.layout.simple_list_item_1, parent, false);
                }
                TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
                tv.setBackgroundResource(R.drawable.backgradient);

                return tv;
            }

            @Override
            public int getCount() {
                return 15;
            }
        });
    }
}
