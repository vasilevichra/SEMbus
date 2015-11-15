package com.mediasmithy.sembus;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {


    public static PendingIntent buildButtonPendingIntent(Context context) {
        ++WidgetIntentReceiver.clickCountDesc;
        ++WidgetIntentReceiver.clickCountTitle;

        // initiate widget update request
        Intent intent = new Intent();
        intent.setAction(WidgetUtils.WIDGET_UPDATE_ACTION);
//        Activity.findViewById(R.id.next_location);
//        Drawable.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static CharSequence getDesc(Context context) {
        return context.getResources().getStringArray(R.array.bus_schedule)[0];
    }

    private static CharSequence getTitle(Context context) {
        return context.getResources().getStringArray(R.array.bus_stops)[0];
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        remoteViews.setOnClickPendingIntent(R.id.schedule, pendingIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // initializing widget layout
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // register for button event
        remoteViews.setOnClickPendingIntent(R.id.schedule, buildButtonPendingIntent(context));
        remoteViews.setOnClickPendingIntent(R.id.location, buildButtonPendingIntent(context));
        remoteViews.setOnClickPendingIntent(R.id.next_location, buildButtonPendingIntent(context));

        // updating view with initial data
        remoteViews.setTextViewText(R.id.location, getTitle(context));
        remoteViews.setTextViewText(R.id.schedule, getDesc(context));

        // request for widget update
        pushWidgetUpdate(context, remoteViews);
    }
}
