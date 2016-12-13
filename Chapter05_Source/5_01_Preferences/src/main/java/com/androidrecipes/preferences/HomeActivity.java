package com.androidrecipes.preferences;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends Activity implements View.OnClickListener {

    Button settingsButton;
    TextView displayText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Load the preference defaults
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        displayText = (TextView) findViewById(R.id.display);
        settingsButton = (Button) findViewById(R.id.settings);
        settingsButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Display the current settings
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        StringBuilder builder = new StringBuilder();
        builder.append("User Name: " + settings.getString("namePref", "") + "\n");
        if (!settings.getBoolean("morePref", false)) {
            builder.append("More Settings is DISABLED");
        } else {
            builder.append("More Settings is ENABLED\n");
            builder.append("Favorite Color is " + settings.getString("colorPref", "") + "\n");
            builder.append(settings.getBoolean("gpsPref", false) ? "GPS is ENABLED\n" : "GPS is DISABLED\n");
            builder.append(settings.getBoolean("networkPref", false) ? "Network is ENABLED\n" : "Network is DISABLED\n");
        }
        displayText.setText(builder.toString());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}