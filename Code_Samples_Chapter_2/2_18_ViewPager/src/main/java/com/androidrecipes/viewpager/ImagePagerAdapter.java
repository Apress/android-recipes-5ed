package com.androidrecipes.viewpager;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImagePagerAdapter extends PagerAdapter {
    private static final int[] IMAGES = {
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_add,
            android.R.drawable.ic_menu_delete,
            android.R.drawable.ic_menu_share,
            android.R.drawable.ic_menu_edit
    };
    private static final int[] COLORS = {
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.GRAY,
            Color.MAGENTA
    };
    private Context mContext;

    public ImagePagerAdapter(Context context) {
        super();
        mContext = context;
    }

    /*
     * Provide the total number of pages
     */
    @Override
    public int getCount() {
        return 5;
    }

    /*
     * Override this method if you want to show more than one page
     * at a time inside the ViewPager's content bounds.
     */
    @Override
    public float getPageWidth(int position) {
        return 0.333f;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Create a new ImageView and add it to the supplied container
        ImageView iv = new ImageView(mContext);
        // Set the content for this position
        iv.setImageResource(IMAGES[position]);
        iv.setBackgroundColor(COLORS[position]);
        // You MUST add the view here, the framework will no do that for you
        container.addView(iv);
        //Return this view also as the key object for this position
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //Remove the view from the container here
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // Validate that the object returned from instantiateItem() is associated
        // with the view added to the container in that location.  Our example uses
        // the same object in both places.
        return (view == object);
    }

}
