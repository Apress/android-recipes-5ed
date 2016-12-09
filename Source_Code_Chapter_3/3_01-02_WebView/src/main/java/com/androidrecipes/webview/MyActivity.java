package com.androidrecipes.webview;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class MyActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webview = new WebView(this);
        //Enable JavaScript support
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.android.com/");

        setContentView(webview);
    }
}

