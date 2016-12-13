package com.example.metadataretriever;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MetadataActivity extends Activity {
    private static final int PICK_VIDEO = 100;

    private ImageView mFrameView;
    private TextView mMetadataView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mFrameView = (ImageView) findViewById(R.id.image_frame);
        mMetadataView = (TextView) findViewById(R.id.text_metadata);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_VIDEO && resultCode == RESULT_OK && data != null) {
            Uri video = data.getData();
            MetadataTask task = new MetadataTask(this, mFrameView, mMetadataView);
            task.execute(video);
        }
    }

    public void onSelectClick(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO);
    }

    public static class MetadataTask extends AsyncTask<Uri, Void, Bundle> {
        private Context mContext;
        private ImageView mFrame;
        private TextView mMetadata;
        private ProgressDialog mProgress;

        public MetadataTask(Context context, ImageView frame, TextView metadata) {
            mContext = context;
            mFrame = frame;
            mMetadata = metadata;
        }

        @Override
        protected void onPreExecute() {
            mProgress = ProgressDialog.show(mContext, "", "Analyzing Video File...", true);
        }

        @Override
        protected Bundle doInBackground(Uri... params) {
            Uri video = params[0];
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mContext, video);

            Bitmap frame = retriever.getFrameAtTime();

            String date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

            Bundle result = new Bundle();
            result.putParcelable("frame", frame);
            result.putString("date", date);
            result.putString("duration", duration);
            result.putString("width", width);
            result.putString("height", height);

            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }

            Bitmap frame = result.getParcelable("frame");
            mFrame.setImageBitmap(frame);
            String metadata = String.format("Video Date: %s\nVideo Duration: %s\nVideo Size: %s x %s",
                    result.getString("date"),
                    result.getString("duration"),
                    result.getString("width"),
                    result.getString("height"));
            mMetadata.setText(metadata);
        }
    }

}
