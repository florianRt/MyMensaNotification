package com.sabbelkrabbe.mymensanotification.receivers

import android.content.BroadcastReceiver
import android.content.Intent
import com.sabbelkrabbe.mymensanotification.receivers.AlarmReceiver
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.Context
import java.util.*

class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
//            // on device boot complete, reset the alarm
//            val alarmIntent = Intent(context, AlarmReceiver::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
//            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            val calendar = Calendar.getInstance()
//            calendar.timeInMillis = System.currentTimeMillis()
//            calendar[Calendar.HOUR_OF_DAY] = 7
//            calendar[Calendar.MINUTE] = 0
//            calendar[Calendar.SECOND] = 1
//            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
//                AlarmManager.INTERVAL_DAY, pendingIntent)
//        }
    }
}