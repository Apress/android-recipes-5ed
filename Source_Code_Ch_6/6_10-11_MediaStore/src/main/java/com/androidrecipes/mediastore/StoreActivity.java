package com.androidrecipes.mediastore;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class StoreActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_CAPTURE = 100;
    private static final int REQUEST_DOCUMENT = 101;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save);

        Button images = (Button) findViewById(R.id.imageButton);
        images.setOnClickListener(this);
        Button videos = (Button) findViewById(R.id.videoButton);
        videos.setOnClickListener(this);
        //We can only create new documents above API Level 19
        Button text = (Button) findViewById(R.id.textButton);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            text.setOnClickListener(this);
        } else {
            text.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "All Done!", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_DOCUMENT && resultCode == Activity.RESULT_OK) {
            //Once the user has selected where to save the new document,
            // we can write the contents into it
            Uri document = data.getData();
            writeDocument(document);
        }
    }

    private void writeDocument(Uri document) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(document, "w");
            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
            //Construct some content for our file
            StringBuilder sb = new StringBuilder();
            sb.append("Android Recipes Log File:");
            sb.append("\n");
            sb.append("Last Written at: ");
            sb.append(DateFormat.getLongDateFormat(this).format(new Date()));

            out.write(sb.toString().getBytes());

            // Let the document provider know you're done by closing the stream.
            out.flush();
            out.close();
            // Close our file handle
            pfd.close();
        } catch (FileNotFoundException e) {
            Log.w("AndroidRecipes", e);
        } catch (IOException e) {
            Log.w("AndroidRecipes", e);
        }
    }

    @Override
    public void onClick(View v) {
        ContentValues values;
        Intent intent;
        Uri storeLocation;
        final long nowMillis = System.currentTimeMillis();

        switch (v.getId()) {
            case R.id.imageButton:
                //Create any metadata for image
                values = new ContentValues(5);
                values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, nowMillis);
                values.put(MediaStore.Images.ImageColumns.DATE_ADDED, nowMillis / 1000);
                values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, nowMillis / 1000);
                values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "Android Recipes Image Sample");
                values.put(MediaStore.Images.ImageColumns.TITLE, "Android Recipes Image Sample");

                //Insert metadata and retrieve Uri location for file
                storeLocation = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                //Start capture with new location as destination
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, storeLocation);
                startActivityForResult(intent, REQUEST_CAPTURE);
                return;
            case R.id.videoButton:
                //Create any metadata for video
                values = new ContentValues(7);
                values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, nowMillis);
                values.put(MediaStore.Video.VideoColumns.DATE_ADDED, nowMillis / 1000);
                values.put(MediaStore.Video.VideoColumns.DATE_MODIFIED, nowMillis / 1000);
                values.put(MediaStore.Video.VideoColumns.DISPLAY_NAME, "Android Recipes Video Sample");
                values.put(MediaStore.Video.VideoColumns.TITLE, "Android Recipes Video Sample");
                values.put(MediaStore.Video.VideoColumns.ARTIST, "Yours Truly");
                values.put(MediaStore.Video.VideoColumns.DESCRIPTION, "Sample Video Clip");

                //Insert metadata and retrieve Uri location for file
                storeLocation = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                //Start capture with new location as destination
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, storeLocation);
                startActivityForResult(intent, REQUEST_CAPTURE);
                return;
            case R.id.textButton:
                //Create a new document
                intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                //This is a text document
                intent.setType("text/plain");
                //Optional title to pre-set on document
                intent.putExtra(Intent.EXTRA_TITLE, "Android Recipes");
                startActivityForResult(intent, REQUEST_DOCUMENT);
            default:
                return;
        }
    }
}
