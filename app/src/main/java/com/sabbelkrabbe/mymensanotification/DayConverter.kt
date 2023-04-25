package com.sabbelkrabbe.mymensanotification

import android.content.res.Resources
import java.time.DayOfWeek

class DayConverter {
    fun getDay(resources: Resources,day: DayOfWeek): String{
        return when (day) {
            DayOfWeek.MONDAY -> resources.getString(R.string.monday_name)
            DayOfWeek.TUESDAY -> resources.getString(R.string.tuesday_name)
            DayOfWeek.WEDNESDAY -> resources.getString(R.string.wednesday_name)
            DayOfWeek.THURSDAY -> resources.getString(R.string.thursday_name)
            DayOfWeek.FRIDAY -> resources.getString(R.string.friday_name)
            DayOfWeek.SATURDAY -> resources.getString(R.string.saturday_name)
            DayOfWeek.SUNDAY -> resources.getString(R.string.sunday_name)
        }
    }
}