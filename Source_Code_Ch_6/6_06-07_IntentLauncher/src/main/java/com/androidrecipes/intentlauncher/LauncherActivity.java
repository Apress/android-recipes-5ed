package com.androidrecipes.intentlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class LauncherActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // //Create a Uri for files on external storage
        // File externalFile = new
        // File(Environment.getExternalStorageDirectory(), filename);
        // Uri external = Uri.fromFile(externalFile);
        // viewPdf(external);

        // //Create a Uri for files on internal storage
        // File internalFile = getFileStreamPath(filename);
        // Uri internal = Uri.fromFile(internalFile);
        // viewPdf(internal);

        // Share an update with friends
        shareContent("Check this out!");
    }

    private void shareContent(String update) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, update);
        startActivity(Intent.createChooser(intent, "Share..."));
    }

    private void viewPdf(Uri file) {
        Intent intent;
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(file, "application/pdf");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // No application to view, ask to download one
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Application Found");
            builder.setMessage("We could not find an application on your device to view PDFs.  Would you like to download one from Android Market?");
            builder.setPositiveButton("Yes, Please",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent
                                    .setData(Uri
                                            .parse("market://details?id=com.adobe.reader"));
                            startActivity(marketIntent);
                        }
                    });
            builder.setNegativeButton("No, Thanks", null);
            builder.create().show();
        }
    }
}