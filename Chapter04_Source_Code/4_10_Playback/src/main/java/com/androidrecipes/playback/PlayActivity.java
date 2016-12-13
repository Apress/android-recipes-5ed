package com.androidrecipes.playback;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlayActivity extends Activity implements MediaPlayer.OnCompletionListener {

    Button mPlay;
    MediaPlayer mPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlay = new Button(this);
        mPlay.setText("Play Sound");
        mPlay.setOnClickListener(playListener);

        setContentView(R.layout.main);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
        }
    }

    private View.OnClickListener playListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mPlayer == null) {
                try {
                    mPlayer = MediaPlayer.create(PlayActivity.this, R.raw.sound);
                    mPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayer.release();
        mPlayer = null;
    }

}