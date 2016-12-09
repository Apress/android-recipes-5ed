package com.androidrecipes.optionsmenu;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class OptionsActivity extends AppCompatActivity implements
        PopupMenu.OnMenuItemClickListener,
        CompoundButton.OnCheckedChangeListener {

    private MenuItem mOptionsItem;
    private CheckBox mFirstOption, mSecondOption;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Use this callback to create the menu and do any
        // initial setup necessary
        getMenuInflater().inflate(R.menu.options, menu);

        //Find and initialize our action item
        mOptionsItem = menu.findItem(R.id.menu_add);
        MenuItemCompat.setOnActionExpandListener(mOptionsItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //Must return true to have item expand
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mFirstOption.setChecked(false);
                mSecondOption.setChecked(false);
                //Must return true to have item collapse
                return true;
            }
        });

        mFirstOption = (CheckBox) MenuItemCompat.getActionView(mOptionsItem).findViewById(R.id.option_first);
        mFirstOption.setOnCheckedChangeListener(this);
        mSecondOption = (CheckBox) MenuItemCompat.getActionView(mOptionsItem).findViewById(R.id.option_second);
        mSecondOption.setOnCheckedChangeListener(this);

        return true;
    }
    
    /* CheckBox Callback Methods */

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mFirstOption.isChecked() && mSecondOption.isChecked()) {
            MenuItemCompat.collapseActionView(mOptionsItem);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Use this callback to do setup that needs to happen
        // each time the menu opens
        return super.onPrepareOptionsMenu(menu);
    }

    //Callback from the PopupMenu click
    public boolean onMenuItemClick(MenuItem item) {
        menuItemSelected(item);
        return true;
    }

    //Callback from a standard options menu click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuItemSelected(item);
        return true;
    }

    //Private helper so each unique callback can trigger the same actions
    private void menuItemSelected(MenuItem item) {
        //Get the selected option by id
        switch (item.getItemId()) {
            case R.id.menu_add:
                //Do add action
                break;
            case R.id.menu_remove:
                //Do remove action
                break;
            case R.id.menu_edit:
                //Do edit action
                break;
            case R.id.menu_settings:
                //Do settings action
                break;
            default:
                break;
        }
    }
}
