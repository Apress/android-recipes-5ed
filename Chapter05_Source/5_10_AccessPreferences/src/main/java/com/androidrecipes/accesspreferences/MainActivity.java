package com.androidrecipes.accesspreferences;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnCheckedChangeListener {

    public static final String SETTINGS_ACTION = "com.examples.sharepreferences.ACTION_SETTINGS";
    public static final Uri SETTINGS_CONTENT_URI =
            Uri.parse("content://com.examples.sharepreferences.settingsprovider/settings");

    public static class SettingsColumns {
        public static final String _ID = Settings.NameValueTable._ID;
        public static final String NAME = Settings.NameValueTable.NAME;
        public static final String VALUE = Settings.NameValueTable.VALUE;
    }

    TextView mEnabled, mName, mSelection;
    CheckBox mToggle;

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            updatePreferences();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mEnabled = (TextView) findViewById(R.id.value_enabled);
        mName = (TextView) findViewById(R.id.value_name);
        mSelection = (TextView) findViewById(R.id.value_selection);
        mToggle = (CheckBox) findViewById(R.id.checkbox_enable);
        mToggle.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Get the latest provider data
        updatePreferences();
        //Register an observer for changes that will
        // happen while we are active
        getContentResolver().registerContentObserver(SETTINGS_CONTENT_URI, false, mObserver);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ContentValues cv = new ContentValues(2);
        cv.put(SettingsColumns.NAME, "preferenceEnabled");
        cv.put(SettingsColumns.VALUE, isChecked);

        //Update the provider, which will trigger our observer
        getContentResolver().update(SETTINGS_CONTENT_URI, cv, null, null);
    }

    public void onSettingsClick(View v) {
        try {
            Intent intent = new Intent(SETTINGS_ACTION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,
                    "You do not have the Android Recipes Settings App installed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePreferences() {
        Cursor c = getContentResolver().query(SETTINGS_CONTENT_URI,
                new String[]{SettingsColumns.NAME, SettingsColumns.VALUE},
                null, null, null);
        if (c == null) {
            return;
        }

        while (c.moveToNext()) {
            String key = c.getString(0);
            if ("preferenceEnabled".equals(key)) {
                mEnabled.setText(String.format("Enabled Setting = %s", c.getString(1)));
                mToggle.setChecked(Boolean.parseBoolean(c.getString(1)));
            } else if ("preferenceName".equals(key)) {
                mName.setText(String.format("User Name Setting = %s", c.getString(1)));
            } else if ("preferenceSelection".equals(key)) {
                mSelection.setText(String.format("Selection Setting = %s", c.getString(1)));
            }
        }

        c.close();
    }

}
