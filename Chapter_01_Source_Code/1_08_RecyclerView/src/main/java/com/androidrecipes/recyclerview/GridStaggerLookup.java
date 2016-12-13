package com.androidrecipes.recyclerview;

import android.support.v7.widget.GridLayoutManager;

public class GridStaggerLookup extends GridLayoutManager.SpanSizeLookup {

    @Override
    public int getSpanSize(int position) {
        return (position % 3 == 0 ? 2 : 1);
    }
}
