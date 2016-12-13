package com.androidrecipes.playback;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.VideoView;

import java.net.HttpURLConnection;
import java.net.URL;

public class RedirectTracerTask extends AsyncTask<Uri, Void, Uri> {

    private VideoView mVideo;
    private Uri initialUri;

    public RedirectTracerTask(VideoView video) {
        super();
        mVideo = video;
    }

    @Override
    protected Uri doInBackground(Uri... params) {
        initialUri = params[0];
        String redirected = null;
        try {
            URL url = new URL(initialUri.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //Once connected, see where you ended up
            redirected = connection.getHeaderField("Location");

            return Uri.parse(redirected);
        } catch (Exception e) {
            //Couldn't find a valid redirect to follow
            return null;
        }
    }

    @Override
    protected void onPostExecute(Uri result) {
        if (result != null) {
            mVideo.setVideoURI(result);
        } else {
            mVideo.setVideoURI(initialUri);
        }
        mVideo.start();
    }

}
