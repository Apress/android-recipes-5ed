package com.androidrecipes.matchmaker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class PlayerActivity extends Activity {

    public static final String ACTION_PLAY = "com.examples.myplayer.PLAY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Inspect the Intent that launched us
        Intent incoming = getIntent();
        //Get the video URI from the data field
        Uri videoUri = incoming.getData();
        //Get the optional title extra, if it exists
        String title;
        if (incoming.hasExtra(Intent.EXTRA_TITLE)) {
            title = incoming.getStringExtra(Intent.EXTRA_TITLE);
        } else {
            title = "";
        }

        //Begin playing the video and displaying the title
        Log.i("PLAYER", "Launched");
    }
    
    /* Remainder of the Activity Code */
}