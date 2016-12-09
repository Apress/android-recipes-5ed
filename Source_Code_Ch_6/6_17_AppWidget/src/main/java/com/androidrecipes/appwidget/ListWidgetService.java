package com.androidrecipes.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.File;

public class ListWidgetService extends RemoteViewsService {

    public static final String KEY_MODE = "mode";
    public static final String MODE_IMAGE = "image";
    public static final String MODE_VIDEO = "video";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this, intent);
    }

    private class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private int mAppWidgetId;

        private Cursor mDataCursor;

        public ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context.getApplicationContext();
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            //Load preferences to get settings user set while adding the widget
            SharedPreferences prefs = mContext.getSharedPreferences(String.valueOf(mAppWidgetId), MODE_PRIVATE);
            //Get the user's config setting, defaulting to image mode
            String mode = prefs.getString(KEY_MODE, MODE_IMAGE);
            //Set the media type to query based on the user configuration setting
            if (MODE_VIDEO.equals(mode)) {
                //Query for video items in the MediaStore
                String[] projection = {MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.DATE_TAKEN,
                        MediaStore.Video.Media.DATA};
                mDataCursor = MediaStore.Images.Media.query(getContentResolver(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection);
            } else {
                //Query for image items in the MediaStore
                String[] projection = {MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.DATA};
                mDataCursor = MediaStore.Images.Media.query(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection);
            }
        }

        /*
         * This method gets called after onCreate(), but also if an external call
         * to AppWidgetManager.notifyAppWidgetViewDataChanged() indicates that the
         * data for a widget should be refreshed.
         */
        @Override
        public void onDataSetChanged() {
            //Refresh the Cursor data
            mDataCursor.requery();
        }

        @Override
        public void onDestroy() {
            //Close the cursor when we no longer need it.
            mDataCursor.close();
            mDataCursor = null;
        }

        @Override
        public int getCount() {
            return mDataCursor.getCount();
        }

        /*
         * If your data comes from the network or otherwise may take ahile to load,
         * you can return a loading view here.  This view will be shown while getViewAt()
         * is blocked until it returns
         */
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        /*
         * Return a view for each item in the collection.  You can safely perform long
         * operations in this method.  The loading view will be displayed until this
         * method returns.
         */
        @Override
        public RemoteViews getViewAt(int position) {
            mDataCursor.moveToPosition(position);

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_widget_item);
            views.setTextViewText(R.id.line1, mDataCursor.getString(0));
            views.setTextViewText(R.id.line2, DateFormat.format("MM/dd/yyyy", mDataCursor.getLong(1)));

            SharedPreferences prefs = mContext.getSharedPreferences(String.valueOf(mAppWidgetId), MODE_PRIVATE);
            String mode = prefs.getString(KEY_MODE, MODE_IMAGE);
            String type;
            if (MODE_VIDEO.equals(mode)) {
                type = "video/*";
            } else {
                type = "image/*";
            }

            Uri data = Uri.fromFile(new File(mDataCursor.getString(2)));

            Intent intent = new Intent();
            intent.setDataAndType(data, type);
            views.setOnClickFillInIntent(R.id.list_widget_item, intent);

            return views;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
