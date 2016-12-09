package com.androidrecipes.rotation;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

public class ManualRotationActivity extends Activity {

    //References to view elements
    private EditText mEditText;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Calling super is required
        super.onCreate(savedInstanceState);
        //Load view resources
        loadView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //Calling super is required
        super.onConfigurationChanged(newConfig);

        //Only reload the view under the new configuration
        // if the box is checked.
        if (mCheckBox.isChecked()) {
            final Bundle uiState = new Bundle();
            //Store important UI state
            saveState(uiState);
            //Reload the view
            loadView();
            //Restore UI state
            restoreState(uiState);
        }
    }

    //Implement any code to persist the UI state
    private void saveState(Bundle state) {
        state.putBoolean("checkbox", mCheckBox.isChecked());
        state.putString("text", mEditText.getText().toString());
    }

    //Restore any elements you saved before reloading
    private void restoreState(Bundle state) {
        mCheckBox.setChecked(state.getBoolean("checkbox"));
        mEditText.setText(state.getString("text"));
    }

    //Set the content view and obtain view references
    private void loadView() {
        setContentView(R.layout.activity_manual);

        //We have to reset our view references anytime a new layout is set
        mCheckBox = (CheckBox) findViewById(R.id.override);
        mEditText = (EditText) findViewById(R.id.text);
    }
}
