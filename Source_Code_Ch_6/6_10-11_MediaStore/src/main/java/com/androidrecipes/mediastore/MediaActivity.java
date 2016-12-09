package com.androidrecipes.mediastore;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MediaActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_AUDIO = 1;
    private static final int REQUEST_VIDEO = 2;
    private static final int REQUEST_IMAGE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button images = (Button) findViewById(R.id.imageButton);
        images.setOnClickListener(this);
        Button videos = (Button) findViewById(R.id.videoButton);
        videos.setOnClickListener(this);
        Button audio = (Button) findViewById(R.id.audioButton);
        audio.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            //Uri to user selection returned in the Intent
            Uri selectedContent = data.getData();

            if (requestCode == REQUEST_IMAGE) {
                //Pass an InputStream to BitmapFactory
            }
            if (requestCode == REQUEST_VIDEO) {
                //Pass the Uri or a FileDescriptor to MediaPlayer
            }
            if (requestCode == REQUEST_AUDIO) {
                //Pass the Uri or a FileDescriptor to MediaPlayer
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        //Use the proper Intent action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        //Only return files to which we can open a stream
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //Set correct MIME type and launch
        switch (v.getId()) {
            case R.id.imageButton:
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
                return;
            case R.id.videoButton:
                intent.setType("video/*");
                startActivityForResult(intent, REQUEST_VIDEO);
                return;
            case R.id.audioButton:
                intent.setType("audio/*");
                startActivityForResult(intent, REQUEST_AUDIO);
                return;
            default:
                return;
        }
    }
}