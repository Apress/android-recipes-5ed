package com.androidrecipes.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

public class ListAppWidget extends AppWidgetProvider {

    /*
     * This method is called to update the widgets created by this provider.
     * Because we supplied a configuration Activity, this method will not get called
     * for the initial adding of the widget, but will still be called:
     * 1. When the updatePeriodMillis defined in the AppWidgetProviderInfo expires
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Update each widget created by this provider
        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent intent = new Intent(context, ListWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_widget_layout);
            //Set the title view based on the widget configuration
            SharedPreferences prefs = context.getSharedPreferences(String.valueOf(appWidgetIds[i]), Context.MODE_PRIVATE);
            String mode = prefs.getString(ListWidgetService.KEY_MODE, ListWidgetService.MODE_IMAGE);
            if (ListWidgetService.MODE_VIDEO.equals(mode)) {
                views.setTextViewText(R.id.text_title, "Video Collection");
            } else {
                views.setTextViewText(R.id.text_title, "Image Collection");
            }

            //Attach the adapter to populate the data for the list in
            //the form of an Intent that points to our RemoveViewsService
            views.setRemoteAdapter(appWidgetIds[i], R.id.list, intent);

            //Set the empty view for the list
            views.setEmptyView(R.id.list, R.id.list_empty);

            //Set the template Intent for item clicks that each item will fill-in
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, viewIntent, 0);
            views.setPendingIntentTemplate(R.id.list, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    /*
     * Called when the first widget is added to the provider
     */
    @Override
    public void onEnabled(Context context) {
        //Start the service to monitor the MediaStore
        context.startService(new Intent(context, MediaService.class));
    }

    /*
     * Called when all widgets have been removed from this provider
     */
    @Override
    public void onDisabled(Context context) {
        //Stop the service that is monitoring the MediaStore
        context.stopService(new Intent(context, MediaService.class));
    }

    /*
     * Called when one or more widgets attached to this provider are removed
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        //Remove the SharedPreferences we created for each widget removed
        for (int i = 0; i < appWidgetIds.length; i++) {
            context.getSharedPreferences(String.valueOf(appWidgetIds[i]), Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .commit();
        }

    }
}
