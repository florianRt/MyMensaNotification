package com.sabbelkrabbe.mymensanotification

import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.internal.ContextUtils.getActivity

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.time.DayOfWeek

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.sabbelkrabbe.mymensanotification", appContext.packageName)

        val intent = Intent(appContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)



        onView(withId(R.id.btn_settings)).perform(click())

        val pref = appContext.getSharedPreferences(
            "com.sabbelkrabbe.mymensanotification",
            Context.MODE_PRIVATE
        )

        if (pref.getBoolean(DayOfWeek.MONDAY.name, false)) {
            onView(withId(R.id.chB_monday)).perform(click())
        }
        if (pref.getBoolean(DayOfWeek.TUESDAY.name, false)) {
            onView(withId(R.id.chB_tuesday)).perform(click())
        }
        if (pref.getBoolean(DayOfWeek.WEDNESDAY.name, false)) {
            onView(withId(R.id.chB_wednesday)).perform(click())
        }
        if (pref.getBoolean(DayOfWeek.THURSDAY.name, false)) {
            onView(withId(R.id.chB_thursday)).perform(click())
        }
        if (pref.getBoolean(DayOfWeek.FRIDAY.name, false)) {
            onView(withId(R.id.chB_friday)).perform(click())
        }
        if (pref.getBoolean(DayOfWeek.SATURDAY.name, false)) {
            onView(withId(R.id.chB_saturday)).perform(click())
        }
        if (pref.getBoolean(DayOfWeek.SUNDAY.name, false)) {
            onView(withId(R.id.chB_sunday)).perform(click())
        }
        
        onView(withId(R.id.chB_monday)).perform(click())
        onView(withId(R.id.chB_tuesday)).perform(click())
        onView(withId(R.id.chB_wednesday)).perform(click())
        onView(withId(R.id.chB_thursday)).perform(click())
        onView(withId(R.id.chB_friday)).perform(click())
        onView(withId(R.id.chB_saturday)).perform(click())
        onView(withId(R.id.chB_sunday)).perform(click())

        onView(withText("Bestätigen")).perform(click())

        onView(withText("MensaMenu")).check(matches(isDisplayed()))


        assert(pref.getBoolean(DayOfWeek.MONDAY.name, false))
        assert(pref.getBoolean(DayOfWeek.TUESDAY.name, false))
        assert(pref.getBoolean(DayOfWeek.WEDNESDAY.name, false))
        assert(pref.getBoolean(DayOfWeek.THURSDAY.name, false))
        assert(pref.getBoolean(DayOfWeek.FRIDAY.name, false))
        assert(pref.getBoolean(DayOfWeek.SATURDAY.name, false))
        assert(pref.getBoolean(DayOfWeek.SUNDAY.name, false))
    }
}