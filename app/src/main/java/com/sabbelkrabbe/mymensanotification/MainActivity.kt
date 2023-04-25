package com.sabbelkrabbe.mymensanotification

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.window.layout.WindowMetricsCalculator
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sabbelkrabbe.mymensanotification.databinding.ActivityMainBinding
import com.sabbelkrabbe.mymensanotification.receivers.AlarmReceiver
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*


class MainActivity : AppCompatActivity() {

    private val url =
        "https://www.studierendenwerk-kassel.de/speiseplan/mensa-71-wilhelmshoeher-allee/"


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val prefs = this.getSharedPreferences("com.sabbelkrabbe.mymensanotification", Context.MODE_PRIVATE)
//        prefs.edit().putInt("test", 12345).apply()
        Log.d(TAG, "onCreate: " + prefs.getInt("test", 0))

        initCards()
    }

    private fun initCards() {

        val queue = Volley.newRequestQueue(this)
        // Request a string response from the provided URL.
        if (checkForInternetConnection()) {
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val week = Menu().getWeek(response)!!
                    val dayOfWeek = LocalDate.now().dayOfWeek

                    if (dayOfWeek < DayOfWeek.SATURDAY) {
                        setFoodCards(week[dayOfWeek.value - 1]!!)
                    }
                },
                { Log.d(TAG, "makeNotification: Failed to send request") })
            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }
    }

    private fun setFoodCards(day: Day) {
        val display: Display = windowManager.defaultDisplay

        val width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: androidx.window.layout.WindowMetrics =
                WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
            windowMetrics.bounds.width()
        } else {
            display.width
        }

        if (day.foods!!.isNotEmpty()) {
            val food = day.foods!![0]
            binding.essen1Title.text = buildString {
                append("Essen ")
                append(1)
                append(getTextToDiet(food.diet))
            }
            binding.essen1Name.text = food.name
            binding.essen1Price.text = buildString { append(food.price) }
            var hintList = ""
            for (hint in food.hints) {
                hintList += " ${hint.name}"
            }
            binding.essen1Hints.text = hintList
            binding.essen1.layoutParams.width = width - dpToPx(20)
        } else {
            binding.essen1.visibility = View.GONE
        }
        if (day.foods!!.size > 1) {
            val food = day.foods!![1]
            binding.essen2.visibility = View.VISIBLE
            binding.essen2Title.text = buildString {
                append("Essen ")
                append(2)
                append(getTextToDiet(food.diet))
            }
            binding.essen2Name.text = food.name
            binding.essen2Price.text = buildString {
                append(food.price)
            }
            var hintList = ""
            for (hint in food.hints) {
                hintList += " ${hint.name}"
            }
            binding.essen2Hints.text = hintList
            binding.essen2.layoutParams.width = width - dpToPx(20)
        } else {
            binding.essen2.visibility = View.GONE
        }

        if (day.foods!!.size > 2) {
            val food = day.foods!![2]
            binding.essen3.visibility = View.VISIBLE
            binding.essen3Title.text = buildString {
                append("Essen ")
                append(3)
                append(getTextToDiet(food.diet))
            }
            binding.essen3Name.text = food.name
            binding.essen3Price.text = buildString {
                append(food.price)
                append("€")
            }
            var hintList = ""
            for (hint in food.hints) {
                hintList += " ${hint.name}"
            }
            binding.essen3Hints.text = hintList
            binding.essen3.layoutParams.width = width - dpToPx(20)
        } else {
            binding.essen3.visibility = View.GONE
        }
    }

    private fun dpToPx(i: Int): Int {
        return (i * resources.displayMetrics.density).toInt()
    }

    private fun getTextToDiet(diet: Int): String {
        return when (diet) {
            Food().VEGETARIAN -> " (fleischlos)"
            Food().VEGAN -> " (vegan)"
            else -> ""
        }
    }


    private fun checkForInternetConnection(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if (!isConnected) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
        return isConnected
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_createNotification -> {
                val now = Calendar.getInstance()
                AlarmReceiver().setNotificationAlarm(this, now.get(Calendar.DAY_OF_WEEK),now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE) + 1,0,false)
                AlarmReceiver().makeNotification(this)
                true
            }
            R.id.btn_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
