package com.mediasmithy.sembus

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import static android.app.PendingIntent.*
import static com.mediasmithy.sembus.Utils.WIDGET_UPDATE_ACTION

class Provider extends AppWidgetProvider {


    static buildButtonPendingIntent(Context context) {
        ++IntentReceiver.clickCountDesc
        ++IntentReceiver.clickCountTitle

        // initiate widget update request
        def intent = new Intent()
        intent.setAction WIDGET_UPDATE_ACTION
//        Activity.findViewById(R.id.next_location)
//        Drawable.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY)
        getBroadcast context, 0, intent, FLAG_UPDATE_CURRENT
    }

    static pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        def myWidget = new ComponentName(context, Provider)
        def manager = AppWidgetManager.getInstance(context)
        manager.updateAppWidget myWidget, remoteViews

        def pendingIntent = getActivity(context, 0, new Intent(), 0)
        remoteViews.setOnClickPendingIntent R.id.schedule, pendingIntent
    }

    @Override
    void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // initializing widget layout
        def remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout)

        // register for button event
        remoteViews.setOnClickPendingIntent R.id.schedule, buildButtonPendingIntent(context)
        remoteViews.setOnClickPendingIntent R.id.location, buildButtonPendingIntent(context)
//        remoteViews.setOnClickPendingIntent R.id.next_location, buildButtonPendingIntent(context)

        // updating view with initial data
        remoteViews.setTextViewText R.id.location, context.getResources().getString(R.string.app_name)
        remoteViews.setTextViewText R.id.schedule, context.getResources().getString(R.string.welcome)

        // request for widget update
        pushWidgetUpdate context, remoteViews
    }
}