package com.androidrecipes.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class SimpleAppWidget extends AppWidgetProvider {

    /*
     * This method is called to update the widgets created by this provider.
     * Normally, this will get called:
     * 1. Initially when the widget is created
     * 2. When the updatePeriodMillis defined in the AppWidgetProviderInfo expires
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the background service to update the widget
        context.startService(new Intent(context, RandomService.class));
    }
}
