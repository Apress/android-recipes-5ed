package com.androidrecipes.soundpool;

import android.app.Activity;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class SoundPoolActivity extends Activity implements View.OnClickListener {

    private AudioManager mAudioManager;
    private SoundPool mSoundPool;
    private SparseIntArray mSoundMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Get the AudioManager system service
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //Set up pool to only play one sound at a time over the
        // standard speaker output.
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        findViewById(R.id.button_beep1).setOnClickListener(this);
        findViewById(R.id.button_beep2).setOnClickListener(this);
        findViewById(R.id.button_beep3).setOnClickListener(this);

        //Load each sound and save their streamId into a map
        mSoundMap = new SparseIntArray();
        AssetManager manager = getAssets();
        try {
            int streamId;
            streamId = mSoundPool.load(manager.openFd("Beep1.ogg"), 1);
            mSoundMap.put(R.id.button_beep1, streamId);

            streamId = mSoundPool.load(manager.openFd("Beep2.ogg"), 1);
            mSoundMap.put(R.id.button_beep2, streamId);

            streamId = mSoundPool.load(manager.openFd("Beep3.ogg"), 1);
            mSoundMap.put(R.id.button_beep3, streamId);
        } catch (IOException e) {
            Toast.makeText(this, "Error Loading Sound Effects", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSoundPool.release();
        mSoundPool = null;
    }

    @Override
    public void onClick(View v) {
        //Retrieve the appropriate sound ID
        int streamId = mSoundMap.get(v.getId());
        if (streamId > 0) {
            float streamVolumeCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float streamVolumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = streamVolumeCurrent / streamVolumeMax;

            mSoundPool.play(streamId, volume, volume, 1, 0, 1.0f);
        }
    }
}
