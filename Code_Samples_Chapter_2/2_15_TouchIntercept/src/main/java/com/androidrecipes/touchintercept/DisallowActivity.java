package com.androidrecipes.touchintercept;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DisallowActivity extends Activity implements
        ViewPager.OnPageChangeListener {
    private static final String[] ITEMS = {
            "Row One", "Row Two", "Row Three", "Row Four",
            "Row Five", "Row Six", "Row Seven", "Row Eight",
            "Row Nine", "Row Ten"
    };

    private ViewPager mViewPager;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a header view of horizontal swiping items
        mViewPager = new ViewPager(this);
        // As a ListView header, ViewPager must be given a fixed height
        mViewPager.setLayoutParams(new ListView.LayoutParams(
                ListView.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.header_height)));
        // Listen for paging state changes to disable parent touches
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setAdapter(new HeaderAdapter(this));

        // Create a vertical scrolling list
        mListView = new ListView(this);
        // Add the pager as the list header
        mListView.addHeaderView(mViewPager);
        // Add list items
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ITEMS));

        setContentView(mListView);
    }

    /* OnPageChangeListener Methods */

    @Override
    public void onPageScrolled(int position,
                               float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // While the ViewPager is scrolling, disable the ScrollView touch
        // intercept so it cannot take over and try to vertical scroll.
        // This flag must be set for each gesture you want to override.
        boolean isScrolling = state != ViewPager.SCROLL_STATE_IDLE;
        mListView.requestDisallowInterceptTouchEvent(isScrolling);
    }

    private static class HeaderAdapter extends PagerAdapter {
        private Context mContext;

        public HeaderAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object instantiateItem(ViewGroup container,
                                      int position) {
            // Create a new page view
            TextView tv = new TextView(mContext);
            tv.setText(String.format("Page %d", position + 1));
            tv.setBackgroundColor((position % 2 == 0) ? Color.RED
                    : Color.GREEN);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.BLACK);

            // Add as the view for this position, and return as the object for
            // this position
            container.addView(tv);
            return tv;
        }

        @Override
        public void destroyItem(ViewGroup container,
                                int position, Object object) {
            View page = (View) object;
            container.removeView(page);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
}
