package com.sabbelkrabbe.mymensanotification

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Implementation of App Widget functionality.
 */
class MenuOverview : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    val mensaChoices = context.resources.getStringArray(R.array.mensa_array)

    val prefs = context.getSharedPreferences("com.sabbelkrabbe.mymensanotification",
        Context.MODE_PRIVATE)

    val selectedMensa = prefs.getString("Mensa", "Mensa 71")
    var selection = selectedMensa?.let { HelperMethods.getIndex(mensaChoices, it) }
    if(selection == null){
        selection = 0
    }

    val views = RemoteViews(context.packageName, R.layout.menu_overview2)

    val url = context.resources.getStringArray(R.array.mensa_urls)[selection]

    var weekMenu: Array<Day?>
    var weekMenuCentralMensa: Array<Day?>
    val queue = Volley.newRequestQueue(context)

    val stringRequest = StringRequest(
        Request.Method.GET, url,
        { response ->
            val dayCount = if(selectedMensa == "Zentralmensa") 6 else 5
            weekMenu = Menu().getWeek(response, dayCount)

            val dayOfWeek = LocalDate.now().dayOfWeek

            if (dayOfWeek < DayOfWeek.SATURDAY || selectedMensa == "Zentralmensa") {
                updateMenu(context,appWidgetManager,appWidgetId, views, weekMenu[dayOfWeek.value - 1]!!)
            }
        },
        { Log.d(ContentValues.TAG, "makeNotification: Failed to send request") })

    if(selectedMensa == "Zentralmensa"){
        val stringRequest2 = StringRequest(
            Request.Method.GET, context.resources.getStringArray(R.array.mensa_urls)[1],
            { response ->
                weekMenuCentralMensa = Menu().getWeek(response, 6)

                val dayOfWeek = LocalDate.now().dayOfWeek

                if (dayOfWeek == DayOfWeek.SATURDAY) {
                    updateMenu(context,appWidgetManager,appWidgetId, views, weekMenuCentralMensa[dayOfWeek.value - 1]!!)
                }
            },
            { Log.d(ContentValues.TAG, "makeNotification: Failed to send request") })
        queue.add(stringRequest2)
    }

    queue.add(stringRequest)


    views.setViewVisibility(R.id.widget_loading, View.VISIBLE)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun updateMenu(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    views: RemoteViews,
    menu: Day
){
    val ids = arrayOf(R.id.food1, R.id.food2, R.id.food3)
    for (i in menu.foods!!.indices) {
        views.setTextViewText(ids[i], menu.foods!![i].name)
    }

    views.setViewVisibility(R.id.widget_loading, View.INVISIBLE)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}