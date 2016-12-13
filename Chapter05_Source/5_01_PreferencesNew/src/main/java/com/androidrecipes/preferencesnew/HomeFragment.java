package com.androidrecipes.preferencesnew;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment implements View.OnClickListener {

    Button settingsButton;
    TextView displayText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View content = inflater.inflate(R.layout.main, container, false);

        //Load the preference defaults
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);

        displayText = (TextView) content.findViewById(R.id.display);
        settingsButton = (Button) content.findViewById(R.id.settings);
        settingsButton.setOnClickListener(this);

        return content;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Display the current settings
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, new SettingsFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
}