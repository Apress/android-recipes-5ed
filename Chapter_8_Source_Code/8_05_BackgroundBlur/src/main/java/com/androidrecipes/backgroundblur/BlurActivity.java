package com.androidrecipes.backgroundblur;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class BlurActivity extends Activity implements
        AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener {

    private static final String[] ITEMS = {
            "Item One", "Item Two", "Item Three", "Item Four", "Item Five",
            "Item Six", "Item Seven", "Item Eight", "Item Nine", "Item Ten",
            "Item Eleven", "Item Twelve", "Item Thirteen", "Item Fourteen", "Item Fifteen"};

    private BackgroundOverlayView mSlideBackground;
    private ImageView mFadeBackground;
    private ListView mListView;
    private View mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

        mSlideBackground = (BackgroundOverlayView) findViewById(R.id.background_slide);
        mFadeBackground = (ImageView) findViewById(R.id.background_fade);
        mListView = (ListView) findViewById(R.id.list);

        //Apply a clear header view to shift the start position of the list elements down
        mHeader = new HeaderView(this);
        mListView.addHeaderView(mHeader, null, false);
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ITEMS));

        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);

        initializeImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blur, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_slide:
                mSlideBackground.setVisibility(View.VISIBLE);
                mFadeBackground.setVisibility(View.GONE);
                return true;
            case R.id.menu_fade:
                mSlideBackground.setVisibility(View.GONE);
                mFadeBackground.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * The heart of our transparency tricks.  We obtain a normal copy
     * and a pre-blurred copy of the background image.
     */
    private void initializeImage() {
        Bitmap inBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        Bitmap outBitmap = inBitmap.copy(inBitmap.getConfig(), true);

        //Create the RenderScript context
        final RenderScript rs = RenderScript.create(this);
        //Create allocations for input and output data
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        //Run a blur at the maximum supported radius (25f)
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(25f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(outBitmap);

        //Tear down the RenderScript context
        rs.destroy();

        //Apply the two copies to our custom drawable for fading
        OverlayFadeDrawable drawable = new OverlayFadeDrawable(
                new BitmapDrawable(getResources(), inBitmap),
                new BitmapDrawable(getResources(), outBitmap));
        mFadeBackground.setImageDrawable(drawable);

        //Apply the two copies to our custom ImageView for sliding
        mSlideBackground.setImagePair(inBitmap, outBitmap);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //On a click event, animated scroll the list back to the top
        mListView.smoothScrollToPosition(0);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        //Make sure views have been measured first
        if (mHeader.getHeight() <= 0) return;

        //Adjust sliding effect clip point based on scroll position
        int topOffset;
        if (firstVisibleItem == 0) {
            //Header is still visible
            topOffset = mHeader.getTop() + mHeader.getHeight();
        } else {
            //Header has been detached, at this point we should be all the way up
            topOffset = 0;
        }
        mSlideBackground.setOverlayOffset(topOffset);

        //Adjust fading effect based on scroll position
        // Blur completely by 85% of the header is scrolled off
        float percent = Math.abs(mHeader.getTop()) / (mHeader.getHeight() * 0.85f);
        int level = Math.min((int) (percent * 10000), 10000);
        mFadeBackground.setImageLevel(level);
    }

    @Override
    public void onScrollStateChanged(AbsListView view,
                                     int scrollState) {
    }
}
