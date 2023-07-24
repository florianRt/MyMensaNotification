package com.sabbelkrabbe.mymensanotification.receivers

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.sabbelkrabbe.mymensanotification.MenuOverview


class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            updateWidget(context)
        }
    }

    private fun updateWidget(context: Context) {
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
            ComponentName(
                context.applicationContext,
                MenuOverview::class.java
            )
        )
        val myWidget = MenuOverview()
        myWidget.onUpdate(
            context.applicationContext,
            AppWidgetManager.getInstance(context.applicationContext),
            ids
        )
    }
}