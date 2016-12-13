package com.androidrecipes.playback;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {

    //Simple flag to play with RedirectTracerTask
    private static final boolean SHOULD_REDIRECT = false;

    VideoView videoView;
    MediaController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Wire components to the view
        videoView = new VideoView(this);
        controller = new MediaController(this);
        videoView.setMediaController(controller);

        Uri videoLocation = Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        if (SHOULD_REDIRECT) {
            //Follow redirects
            RedirectTracerTask task = new RedirectTracerTask(videoView);
            task.execute(videoLocation);
        } else {
            //Just play the video now
            videoView.setVideoURI(videoLocation);
            videoView.start();
        }


        setContentView(videoView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}
