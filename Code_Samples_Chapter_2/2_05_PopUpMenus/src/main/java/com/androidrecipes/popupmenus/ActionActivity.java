package com.androidrecipes.popupmenus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ActionActivity extends AppCompatActivity implements AbsListView.MultiChoiceModeListener {

    private static final String[] ITEMS = {
            "Mom", "Dad", "Brother", "Sister", "Uncle", "Aunt",
            "Cousin", "Grandfather", "Grandmother"};

    private ListView mList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Register a button for context events
        mList = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item, R.id.text, ITEMS);
        mList.setAdapter(adapter);
        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mList.setMultiChoiceModeListener(this);

        setContentView(mList, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        //You can do extra work here update the menu if the
        // ActionMode is ever invalidated
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //This is called when the action mode has ben exited
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.contextmenu, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        SparseBooleanArray items = mList.getCheckedItemPositions();
        //Switch on the item's ID to find the action the user selected
        switch (item.getItemId()) {
            case R.id.menu_delete:
                //Perform delete actions
                break;
            case R.id.menu_edit:
                //Perform edit actions
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position,
                                          long id, boolean checked) {
        int count = mList.getCheckedItemCount();
        mode.setTitle(String.format("%d Selected", count));
    }
}
