package com.sabbelkrabbe.mymensanotification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.sabbelkrabbe.mymensanotification.databinding.ActivitySettingsBinding
import com.sabbelkrabbe.mymensanotification.notification.channelID
import com.sabbelkrabbe.mymensanotification.notification.messageExtra
import com.sabbelkrabbe.mymensanotification.notification.notificationID
import com.sabbelkrabbe.mymensanotification.notification.titleExtra
import com.sabbelkrabbe.mymensanotification.receivers.AlarmReceiver
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        val prefs = this.getSharedPreferences("com.sabbelkrabbe.mymensanotification",
            Context.MODE_PRIVATE)

        if (DateFormat.is24HourFormat(this)) {
            binding.alarmTimePicker.setIs24HourView(true)
            binding.alarmTimePicker.hour = prefs.getInt("hour", 12)
            binding.alarmTimePicker.minute = prefs.getInt("minute", 0)
        }


        binding.chBMonday.isChecked = prefs.getBoolean(DayOfWeek.MONDAY.name , false)
        binding.chBTuesday.isChecked = prefs.getBoolean(DayOfWeek.TUESDAY.name , false)
        binding.chBWednesday.isChecked = prefs.getBoolean(DayOfWeek.WEDNESDAY.name , false)
        binding.chBThursday.isChecked = prefs.getBoolean(DayOfWeek.THURSDAY.name , false)
        binding.chBFriday.isChecked = prefs.getBoolean(DayOfWeek.FRIDAY.name , false)
        binding.chBSaturday.isChecked = prefs.getBoolean(DayOfWeek.SATURDAY.name , false)
        binding.chBSunday.isChecked = prefs.getBoolean(DayOfWeek.SUNDAY.name , false)

        binding.btnConfirm.setOnClickListener {
            val prefs = this.getSharedPreferences("com.sabbelkrabbe.mymensanotification",
                Context.MODE_PRIVATE).also {
                it.edit().putBoolean(DayOfWeek.MONDAY.name, binding.chBMonday.isChecked)
                    .putBoolean(DayOfWeek.TUESDAY.name, binding.chBTuesday.isChecked)
                    .putBoolean(DayOfWeek.WEDNESDAY.name, binding.chBWednesday.isChecked)
                    .putBoolean(DayOfWeek.THURSDAY.name, binding.chBThursday.isChecked)
                    .putBoolean(DayOfWeek.FRIDAY.name, binding.chBFriday.isChecked)
                    .putBoolean(DayOfWeek.SATURDAY.name, binding.chBSaturday.isChecked)
                    .putBoolean(DayOfWeek.SUNDAY.name, binding.chBSunday.isChecked)
                    .putInt("hour", binding.alarmTimePicker.hour)
                    .putInt("minute", binding.alarmTimePicker.minute)
                    .apply()
            }

            prefs.edit().putInt("hour", binding.alarmTimePicker.hour).apply()
            prefs.edit().putInt("minute", binding.alarmTimePicker.minute).apply()


            scheduleNotification()

/*            AlarmReceiver().setNotificationAlarm(this,
//                1,
//                binding.alarmTimePicker.hour,
//                binding.alarmTimePicker.minute,
//                0,
//                true)
////            AlarmReceiver().setNotificationAlarm(this,
////                LocalDate.now().dayOfWeek + 1,
////                binding.alarmTimePicker.hour,
////                binding.alarmTimePicker.minute,
////                1,
////                true)*/

            finish()
        }
    }

    private fun scheduleNotification()
    {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "Menu Notification"
        val message = "This is a test notification"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
//        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }

    private fun getTime(): Long
    {
        val minute = binding.alarmTimePicker.minute
        val hour = binding.alarmTimePicker.hour
        val day = LocalDate.now().dayOfMonth
        val month = LocalDate.now().monthValue
        val year = LocalDate.now().year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}