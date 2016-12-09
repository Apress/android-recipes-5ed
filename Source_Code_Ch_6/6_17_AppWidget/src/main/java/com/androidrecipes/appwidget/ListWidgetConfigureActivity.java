package com.androidrecipes.appwidget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ListWidgetConfigureActivity extends Activity {

    private int mAppWidgetId;
    private RadioGroup mModeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure);

        mModeGroup = (RadioGroup) findViewById(R.id.group_mode);

        mAppWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        setResult(RESULT_CANCELED);
    }

    public void onAddClick(View v) {
        SharedPreferences.Editor prefs = getSharedPreferences(String.valueOf(mAppWidgetId), MODE_PRIVATE).edit();
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_widget_layout);
        switch (mModeGroup.getCheckedRadioButtonId()) {
            case R.id.mode_image:
                prefs.putString(ListWidgetService.KEY_MODE, ListWidgetService.MODE_IMAGE).commit();
                views.setTextViewText(R.id.text_title, "Image Collection");
                break;
            case R.id.mode_video:
                prefs.putString(ListWidgetService.KEY_MODE, ListWidgetService.MODE_VIDEO).commit();
                views.setTextViewText(R.id.text_title, "Video Collection");
                break;
            default:
                Toast.makeText(this, "Please Select a Media Type.", Toast.LENGTH_SHORT).show();
                return;
        }

        Intent intent = new Intent(this, ListWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        //Attach the adapter to populate the data for the list in
        //the form of an Intent that points to our RemoveViewsService
        views.setRemoteAdapter(mAppWidgetId, R.id.list, intent);
        //Set the empty view for the list
        views.setEmptyView(R.id.list, R.id.list_empty);

        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);
        views.setPendingIntentTemplate(R.id.list, pendingIntent);

        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(mAppWidgetId, views);

        Intent data = new Intent();
        data.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, data);
        finish();
    }
}
