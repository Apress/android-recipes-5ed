package com.androidrecipes.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListPagerAdapter extends FragmentPagerAdapter {

    private static final int ITEMS_PER_PAGE = 3;

    private List<String> mItems;

    public ListPagerAdapter(FragmentManager manager, List<String> items) {
        super(manager);
        mItems = items;
    }

    /*
     * This method will only get called the first time a Fragment is needed for this position.
     */
    @Override
    public Fragment getItem(int position) {
        int start = position * ITEMS_PER_PAGE;
        return ArrayListFragment.newInstance(getPageList(position), start);
    }

    @Override
    public int getCount() {
        //Get whole number
        int pages = mItems.size() / ITEMS_PER_PAGE;
        // Add one more page for any remaining values if list size is not divisible by page size
        int excess = mItems.size() % ITEMS_PER_PAGE;
        if (excess > 0) {
            pages++;
        }

        return pages;
    }

    /*
     * This will get called after getItem() for new Fragments, but also when Fragments
     * beyond the off-screen page limit are added back; we need to make sure to update the
     * list for these elements.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ArrayListFragment fragment = (ArrayListFragment) super.instantiateItem(container, position);
        fragment.updateListItems(getPageList(position));
        return fragment;
    }

    /*
     * Called by the framework when notifyDataSetChanged() is called, we must decide how
     * each Fragment has changed for the new data set.  We also return POSITION_NONE if
     * a Fragment at a particular position is no longer needed so the adapter can
     * remove it.
     */
    @Override
    public int getItemPosition(Object object) {
        ArrayListFragment fragment = (ArrayListFragment) object;
        int position = fragment.getBaseIndex() / ITEMS_PER_PAGE;
        if (position >= getCount()) {
            //This page no longer needed
            return POSITION_NONE;
        } else {
            //Refresh fragment data display
            fragment.updateListItems(getPageList(position));

            return position;
        }
    }

    /*
     * Helper method to obtain the piece of the overall list that should be
     * applied to a given Fragment
     */
    private List<String> getPageList(int position) {
        int start = position * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, mItems.size());
        List<String> itemPage = mItems.subList(start, end);

        return itemPage;
    }

    /*
     * Internal custom Fragment that displays a list section inside
     * of a ListView, and provides external methods for updating the list
     */
    public static class ArrayListFragment extends Fragment {
        private ArrayList<String> mItems;
        private ArrayAdapter<String> mAdapter;
        private int mBaseIndex;

        public ArrayListFragment() {
            super();
            mItems = new ArrayList<String>();
        }

        //Fragments are created by convention using a Factory pattern
        static ArrayListFragment newInstance(List<String> page, int baseIndex) {
            ArrayListFragment fragment = new ArrayListFragment();
            fragment.updateListItems(page);
            fragment.setBaseIndex(baseIndex);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //Make a new adapter for the list items
            mAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, mItems);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //Construct and return a Listview with our adapter attached
            ListView list = new ListView(getActivity());
            list.setAdapter(mAdapter);
            return list;
        }

        //Retrieve the index in the global list where this page starts
        public int getBaseIndex() {
            return mBaseIndex;
        }

        //Save the index in the global list where this page starts
        public void setBaseIndex(int index) {
            mBaseIndex = index;
        }

        public void updateListItems(List<String> items) {
            mItems.clear();
            for (String piece : items) {
                mItems.add(piece);
            }

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
