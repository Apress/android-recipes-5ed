package com.androidrecipes.playback;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.MediaController;

public class PlayerActivity extends Activity implements
        MediaController.MediaPlayerControl, MediaPlayer.OnBufferingUpdateListener {

    MediaController mController;
    MediaPlayer mPlayer;
    ImageView coverImage;

    int bufferPercent = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        coverImage = (ImageView) findViewById(R.id.coverImage);

        mController = new MediaController(this);
        mController.setAnchorView(findViewById(R.id.root));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayer = new MediaPlayer();
        //Set the audio data source
        try {
            mPlayer.setDataSource(this, Uri.parse("http://www.jingle.org/levysfurnishers.mp3"));
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Set an image for the album cover
        coverImage.setImageResource(R.drawable.ic_launcher);

        mController.setMediaPlayer(this);
        mController.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mController.show();
        return super.onTouchEvent(event);
    }

    //MediaPlayerControl Methods
    @Override
    public int getBufferPercentage() {
        return bufferPercent;
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void seekTo(int pos) {
        mPlayer.seekTo(pos);
    }

    @Override
    public void start() {
        mPlayer.start();
    }

    //BufferUpdateListener Methods
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferPercent = percent;
    }

    //Android 2.0+ Target Callbacks
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    //Android 4.3+ Target Callbacks
    @Override
    public int getAudioSessionId() {
        return mPlayer.getAudioSessionId();
    }
}