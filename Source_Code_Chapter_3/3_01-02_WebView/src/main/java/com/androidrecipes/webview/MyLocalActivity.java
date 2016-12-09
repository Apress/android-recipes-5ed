package com.androidrecipes.webview;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;

public class MyLocalActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        WebView upperView = (WebView) findViewById(R.id.upperview);
        // Zoom feature must be enabled
        upperView.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //Android 3.0+ has pinch-zoom, don't need buttons
            upperView.getSettings().setDisplayZoomControls(false);
        }
        upperView.loadUrl("file:///android_asset/android.jpg");

        WebView lowerView = (WebView) findViewById(R.id.lowerview);
        String htmlString = "<h1>Header</h1><p>This is HTML text<br /><i>Formatted in italics</i></p>";
        lowerView.loadData(htmlString, "text/html", "utf-8");
    }

}
