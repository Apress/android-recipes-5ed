package com.androidrecipes.sticky;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ReportActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logEvent("CREATE");
    }

    @Override
    public void onStart() {
        super.onStart();
        logEvent("START");
    }

    @Override
    public void onResume() {
        super.onResume();
        logEvent("RESUME");
    }

    @Override
    public void onPause() {
        super.onPause();
        logWarning("PAUSE");
    }

    @Override
    public void onStop() {
        super.onStop();
        logWarning("STOP");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logWarning("DESTROY");
    }

    private void logEvent(String event) {
        Intent intent = new Intent(this, OperationsManager.class);
        intent.setAction(OperationsManager.ACTION_EVENT);
        intent.putExtra(OperationsManager.EXTRA_NAME, event);

        startService(intent);
    }

    private void logWarning(String event) {
        Intent intent = new Intent(this, OperationsManager.class);
        intent.setAction(OperationsManager.ACTION_WARNING);
        intent.putExtra(OperationsManager.EXTRA_NAME, event);

        startService(intent);
    }
}