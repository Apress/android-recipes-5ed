package com.androidrecipes.smsprovider;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

public class SmsActivity extends Activity implements OnItemClickListener, LoaderCallbacks<List<MessageItem>> {

    private MessagesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView list = new ListView(this);
        mAdapter = new MessagesAdapter(this);
        list.setAdapter(mAdapter);

        final Intent intent = getIntent();

        if (!intent.hasExtra("threadId")) {
            //Items are clickable if we are not showing a conversation
            list.setOnItemClickListener(this);
        }
        //Load the messages data
        getLoaderManager().initLoader(0, getIntent().getExtras(), this);

        setContentView(list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final MessageItem item = mAdapter.getItem(position);
        long threadId = item.thread_id;

        //Launch a new instance to show this conversation
        Intent intent = new Intent(this, SmsActivity.class);
        intent.putExtra("threadId", threadId);
        startActivity(intent);
    }

    @Override
    public Loader<List<MessageItem>> onCreateLoader(int id, Bundle args) {
        if (args != null && args.containsKey("threadId")) {
            return new ConversationLoader(this, args.getLong("threadId"));
        } else {
            return new ConversationLoader(this);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<MessageItem>> loader, List<MessageItem> data) {
        mAdapter.clear();
        mAdapter.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<MessageItem>> loader) {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }

    private static class MessagesAdapter extends ArrayAdapter<MessageItem> {

        int cacheSize = 4 * 1024 * 1024; // 4MiB
        private LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        public MessagesAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
            }

            MessageItem item = getItem(position);

            TextView text1 = (TextView) convertView.findViewById(R.id.text1);
            TextView text2 = (TextView) convertView.findViewById(R.id.text2);
            ImageView image = (ImageView) convertView.findViewById(R.id.image);

            text1.setText(item.address);
            text2.setText(item.body);
            //Set text style based on incoming/outgoing status
            Typeface tf = item.incoming ?
                    Typeface.defaultFromStyle(Typeface.ITALIC) : Typeface.DEFAULT;
            text2.setTypeface(tf);
            image.setImageBitmap(getAttachment(item));

            return convertView;
        }

        private Bitmap getAttachment(MessageItem item) {
            if (item.attachment == null) return null;

            final Uri imageUri = item.attachment;
            //Pull image thumbnail from cache if we have it
            Bitmap cached = bitmapCache.get(imageUri.toString());
            if (cached != null) {
                return cached;
            }

            //Decode the asset from the provider if we don't have it in cache
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                int cellHeight = getContext().getResources().getDimensionPixelSize(R.dimen.message_height);
                InputStream is = getContext().getContentResolver().openInputStream(imageUri);
                BitmapFactory.decodeStream(is, null, options);

                options.inJustDecodeBounds = false;
                options.inSampleSize = options.outHeight / cellHeight;
                is = getContext().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);

                bitmapCache.put(imageUri.toString(), bitmap);
                return bitmap;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
