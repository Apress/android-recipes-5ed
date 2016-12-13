package com.androidrecipes.sharedocuments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class DocumentActivity extends Activity {

    private static final int REQUEST_DOCUMENT = 1;
    private static final int REQUEST_DOCUMENT_TREE = 2;

    private TextView mTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        mImageView = (ImageView) findViewById(R.id.image);
        mTextView = (TextView) findViewById(R.id.text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getData() == null) return;
        final Uri result = data.getData();

        if (requestCode == REQUEST_DOCUMENT) {
            //Uri is a reference to the selected image document
            try {
                InputStream in = getContentResolver().openInputStream(result);
                Bitmap image = BitmapFactory.decodeStream(in);

                mImageView.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_DOCUMENT_TREE) {
            //Construct a Uri we can use to query the selected directory contents
            String subDocumentId = DocumentsContract.getTreeDocumentId(result);
            Uri subTree = DocumentsContract.buildChildDocumentsUriUsingTree(result, subDocumentId);

            //Query the directory and list its contents
            Cursor cursor = getContentResolver().query(subTree, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    mTextView.setText("");
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Contents of Directory:\n");
                    while (cursor.moveToNext()) {
                        sb.append(cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)));
                        sb.append("\n");
                    }
                    mTextView.setText(sb.toString());
                }

                cursor.close();
            } else {
                mTextView.setText("");
            }
        }
    }

    public void onDocumentSelect(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_DOCUMENT);
    }

    public void onDirectorySelect(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_DOCUMENT_TREE);
    }
}
