package com.mediasmithy.sembus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

import static com.mediasmithy.sembus.Provider.pushWidgetUpdate
import static com.mediasmithy.sembus.Utils.*

class IntentReceiver extends BroadcastReceiver {
    public static int clickCountDesc = 0
    public static int clickCountTitle = 0
    def desc = []
    def title = []

    @Override
    void onReceive(Context context, Intent intent) {
        if (intent.getAction() == WIDGET_UPDATE_ACTION) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout)

            // updating view
//            remoteViews.setTextViewText(R.id.location, getTitles(context))
            remoteViews.setTextViewText(R.id.schedule, getNextBusFromSchedule(context))

            pushWidgetUpdate context.getApplicationContext(), remoteViews
        }
    }


    def getDescs(Context context) {
        // some static jokes from xml
        desc = context.getResources().getStringArray(R.array.bus_schedule)
        if (clickCountDesc >= desc.length) {
            clickCountDesc = 0;
        }
        return desc[clickCountDesc];
    }

    def getTitles(Context context) {
        title = context.getResources().getStringArray(R.array.bus_stops)
        if (clickCountTitle >= title.length) {
            clickCountTitle = 0
        }
        return title[clickCountTitle]
    }

    private String getNextBusFromSchedule(Context context) {
        final Date CURRENT_TIME = new Date()
        Log.d(APP_TAG, "Current phone time: " + CURRENT_TIME)

        for (String time : context.getResources().getStringArray(R.array.bus_schedule_ladozhskaya)) {
            final SimpleDateFormat WITHOUT_TIME = new SimpleDateFormat("yyyyMMdd")
//            WITHOUT_TIME.setTimeZone(TimeZone.getTimeZone("GMT"))

            try {
                final Date DATE_WITHOUT_TIME = WITHOUT_TIME.parse(WITHOUT_TIME.format(new Date()))
                TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"))
                final Date TIME_WITHOUT_DATE = TIME_FORMAT.parse(time)

                Date iterator = new Date(DATE_WITHOUT_TIME.getTime() + TIME_WITHOUT_DATE.getTime())
                if (iterator.before(CURRENT_TIME)) {
                    Log.d(APP_TAG, "Too early. Skipping time in schedule: " + time + " | " + iterator)
                    continue;
                }
                Log.d(APP_TAG, "Next bus time: " + time + " | " + iterator)
                long minutesDiff = TimeUnit.MILLISECONDS.toMinutes(iterator.getTime() - new Date().getTime())

                return time + " (" + (minutesDiff <= 60 ? minutesDiff + 1 : "∞") + ")"
            } catch (ParseException e) {
                e.printStackTrace()
            }

        }
        return "X"
    }
}
