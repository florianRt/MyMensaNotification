package com.sabbelkrabbe.mymensanotification.receivers

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sabbelkrabbe.mymensanotification.Day
import com.sabbelkrabbe.mymensanotification.Menu
import com.sabbelkrabbe.mymensanotification.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.util.Calendar

// TODO: fix notifications not showing up

class AlarmReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "10"
    private val url = arrayOf(
        "https://www.studierendenwerk-kassel.de/speiseplaene/mensa-71-wilhelmshoeher-allee",
        "https://www.studierendenwerk-kassel.de/speiseplaene/zentralmensa-arnold-bode-strasse"
    )

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences(
            "com.sabbelkrabbe.mymensanotification",
            Context.MODE_PRIVATE
        )
        prefs.edit().putInt("test", 12345).apply()

        Log.d(TAG, "onReceive: Alarm received")
//        Toast.makeText(context, "Alarm received", Toast.LENGTH_LONG).show()

        val selectedMensa = prefs.getString("Mensa", "Mensa 71")!!

        if(selectedMensa == "Mensa 71") {
            makeNotification(context, 0)
        } else {
            makeNotification(context, 1)
        }
//        makeNotification(context)
    }

    fun makeNotification(context: Context, mensa: Int) {
        createNotificationChannel(context)



        val queue = Volley.newRequestQueue(context)
        // Request a string response from the provided URL.
        if (checkForInternetConnection(context)) {
            val stringRequest = StringRequest(
                Request.Method.GET, url[mensa],
                { response ->
                    val week = Menu().getWeek(response, 5 + mensa)
                    val dayOfWeek = LocalDate.now().dayOfWeek

                    Log.d(TAG, "makeNotification: Download successful + $week + $dayOfWeek + $mensa")

                    if (dayOfWeek < DayOfWeek.SATURDAY + mensa.toLong()) {
                        showNotification(week!![dayOfWeek.value - 1]!!, context)
                    }
                },
                { Log.d(VolleyLog.TAG, "makeNotification: Failed to send request") })
            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }
    }

    @Suppress("DEPRECATION")
    private fun checkForInternetConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if (!isConnected) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
        return isConnected
    }

    private fun showNotification(day: Day, context: Context) {


        /*val food1 = Food()
        food1.name = "Knödel mit Sauerkraut"
        food1.price = "2,50€"
        food1.diet = Food().CARNISTIC
        food1.hints = arrayOf(Food.Hint("Kn", "Knoblauch"), Food.Hint("S", "Schwein"))
        val food2 = Food()
        food2.name = "Snackige Salatcreme ohne Fleisch"
        food2.price = "3,20€"
        food2.diet = Food().VEGETARIAN
        food2.hints = arrayOf(Food.Hint("Kn", "Knoblauch"), Food.Hint("Gl", "Gluten"))
        val day = Day()
        day.foods = arrayOf(food1, food2)*/

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_menu)
            .setContentTitle("Mensa Menü")
            .setContentText(day.name)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(day.getFoodString())
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(context, "Please enable notifications", Toast.LENGTH_SHORT).show()
                return
            }

            notify(15687164, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "Menü Notification"//getString(R.string.channel_name)
        val descriptionText =
            "Benachrichtigung zum heutigen Menü"//getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    fun setNotificationAlarm(
        context: Context,
        day: Int,
        hour: Int,
        minute: Int,
        requestCode: Int,
        reset: Boolean,
    ) {
        Log.d(TAG, "setNotificationAlarm: Setting alarm")
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (false && reset) {
            val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

            alarmMgr.cancel(alarmIntent)
        }

        val prefs = context.getSharedPreferences(
            "com.sabbelkrabbe.mymensanotification",
            Context.MODE_PRIVATE
        )


        if (true || prefs.getBoolean(getDayOfWeek(day), true)) {
            val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

            LocalDate.now() + Period.ofDays(requestCode)

            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()

            Log.d(TAG, "setNotificationAlarm: $hour $minute")
            cal[Calendar.HOUR_OF_DAY] = hour
            cal[Calendar.MINUTE] = minute
            cal[Calendar.SECOND] = 0

            alarmMgr.setExact(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                alarmIntent
            )
        }
    }

    private fun getDayOfWeek(day: Int): String {
        return when (day) {
            1 -> "monday"
            2 -> "tuesday"
            3 -> "wednesday"
            4 -> "thursday"
            5 -> "friday"
            6 -> "saturday"
            7 -> "sunday"
            else -> "monday"
        }
    }

}