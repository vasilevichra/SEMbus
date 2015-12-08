package com.mediasmithy.sembus
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import groovy.time.TimeCategory

import static com.mediasmithy.sembus.Provider.pushWidgetUpdate
import static com.mediasmithy.sembus.Utils.WIDGET_UPDATE_ACTION as update
import static java.util.Calendar.*

class IntentReceiver extends BroadcastReceiver {

    public static int clickCountDesc = 0
    public static int clickCountTitle = 0

    def format = "HH:mm"
    def offsetInMilliseconds = TimeZone.getTimeZone("Europe/Moscow").rawOffset

    @Override
    void onReceive(Context context, Intent intent) {
        if (intent.getAction() == update) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout)

            // updating view
            remoteViews.setTextViewText(R.id.location, getTitle(context))
            remoteViews.setTextViewText(R.id.schedule, getNextBus(context))

            pushWidgetUpdate context.getApplicationContext(), remoteViews
        }
    }

    boolean isBusComing(String[] schedule) {

        def today = new Date().clearTime()
        def now = new Date()

        if (now[DAY_OF_WEEK] == SATURDAY || now[DAY_OF_WEEK] == SUNDAY) return false

        def firstStopTime = new Date(today.time + Date.parse(format, schedule.first()).time + offsetInMilliseconds)
        def firstStopTimeMinusHour = use(TimeCategory) {
            firstStopTime - 1.hour
        }

        def lastStopTime = new Date(today.time + Date.parse(format, schedule.last()).time + offsetInMilliseconds)

        return firstStopTimeMinusHour.before(now) && lastStopTime.after(now)
    }

    String getTitle(Context context) {

        def title = context.getResources().getStringArray(R.array.bus_stops as int)

        if (isBusComing(context.getResources().getStringArray(R.array.bus_schedule_ladozhskaya as int))) {
            return title[0]
        }

        if (isBusComing(context.getResources().getStringArray(R.array.bus_schedule_ankor as int))) {
            return title[1]
        }

        return context.getResources().getString(R.string.title_sleep)
    }

    String getNextBus(Context context) {

        def today = new Date().clearTime()
        def now = new Date()

        def activeSchedule = [

                R.array.bus_schedule_ladozhskaya,
                R.array.bus_schedule_ankor

        ].find {

            isBusComing(context.getResources().getStringArray(it as int))

        }

        if (!activeSchedule) return "(ー。ー)zZ"

        def nextStop = context.getResources().getStringArray(activeSchedule as int).find {

            new Date(today.time + Date.parse(format, it).time + offsetInMilliseconds).after(now)

        }

        def nextStopDate = new Date(today.time + Date.parse(format, nextStop).time + offsetInMilliseconds)
        def diff = TimeCategory.minus(nextStopDate, now)

        return "${nextStop} | ${diff.minutes + 1}"
    }
}