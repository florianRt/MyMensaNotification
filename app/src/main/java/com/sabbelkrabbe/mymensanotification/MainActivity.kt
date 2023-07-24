package com.sabbelkrabbe.mymensanotification

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sabbelkrabbe.mymensanotification.databinding.ActivityMainBinding
import com.sabbelkrabbe.mymensanotification.receivers.AlarmReceiver
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var urlMainMensa: String
    private lateinit var urlMensa71: String
    private lateinit var selectedMensa: String

    private lateinit var binding: ActivityMainBinding
    lateinit var weekMenuMensa71: Array<Day?>
    lateinit var weekMenuMainMensa: Array<Day?>
    lateinit var day: DayOfWeek

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        urlMensa71 = getString(R.string.menu_website_mensa_71)
        urlMainMensa = getString(R.string.menu_website_main_mensa)

        val prefs =
            this.getSharedPreferences("com.sabbelkrabbe.mymensanotification", Context.MODE_PRIVATE)
//        prefs.edit().putInt("test", 12345).apply()
        updateMensa()
        Log.d(TAG, "onCreate: " + prefs.getInt("test", 0))

        initCards()
        initButtons()
    }

    private fun initButtons() {
        day = LocalDate.now().dayOfWeek
        binding.txtDay.text = DayConverter().getDay(resources, day)

        binding.btnNextDay.setOnClickListener {
            updateMensa()
            if (day < DayOfWeek.FRIDAY) {
                day = day.plus(1)
                if (selectedMensa == "Mensa 71")
                    setFoodCards(weekMenuMensa71[day.value - 1]!!)
                else
                    setFoodCards(weekMenuMainMensa[day.value - 1]!!)
                binding.txtDay.text = DayConverter().getDay(resources, day)
            }

            if (selectedMensa == "Zentralmensa" && day == DayOfWeek.FRIDAY) {
                day = day.plus(1)
                setFoodCards(weekMenuMainMensa[day.value - 1]!!)
                binding.txtDay.text = DayConverter().getDay(resources, day)
            }
        }

        binding.btnPrevDay.setOnClickListener {
            updateMensa()
            if (day > DayOfWeek.MONDAY) {
                day = day.minus(1)
                if (selectedMensa == "Mensa 71")
                    setFoodCards(weekMenuMensa71[day.value - 1]!!)
                else
                    setFoodCards(weekMenuMainMensa[day.value - 1]!!)
//                setFoodCards(weekMenuMensa71[day.value - 1]!!)
                binding.txtDay.text = DayConverter().getDay(resources, day)
            }
        }
    }

    private fun initCards() {

        val queue = Volley.newRequestQueue(this)

        val stringRequests: Array<StringRequest?> = arrayOfNulls(2)

        // Request a string response from the provided URL.
        if (checkForInternetConnection()) {
            stringRequests[0] = StringRequest(
                Request.Method.GET, urlMensa71,
                { response ->
                    weekMenuMensa71 = Menu().getWeek(response, 5)

                    val dayOfWeek = LocalDate.now().dayOfWeek

                    if (dayOfWeek < DayOfWeek.SATURDAY && selectedMensa == "Mensa 71") {
                        setFoodCards(weekMenuMensa71[dayOfWeek.value - 1]!!)
                    }
                },
                { Log.d(TAG, "makeNotification: Failed to send request") })

            stringRequests[1] = StringRequest(
                Request.Method.GET, urlMainMensa,
                { response ->
                    weekMenuMainMensa = Menu().getWeek(response, 6)

                    val dayOfWeek = LocalDate.now().dayOfWeek

                    if (dayOfWeek < DayOfWeek.SUNDAY && selectedMensa == "Main Mensa" || dayOfWeek == DayOfWeek.SATURDAY) {
                        supportActionBar?.title = "Zentralmensa"
                        setFoodCards(weekMenuMainMensa[dayOfWeek.value - 1]!!)
                    }
                },
                { Log.d(TAG, "makeNotification: Failed to send request") })
            // Add the request to the RequestQueue.

            for (request in stringRequests) {
                queue.add(request)
            }
        }
    }

    private fun setFoodCards(day: Day) {
        binding.foodLoading.visibility = View.INVISIBLE

        val titles = arrayOf(binding.essen1Title, binding.essen2Title, binding.essen3Title)
        val names = arrayOf(binding.essen1Name, binding.essen2Name, binding.essen3Name)
        val prices = arrayOf(binding.essen1Price, binding.essen2Price, binding.essen3Price)
        val hints = arrayOf(binding.essen1Hints, binding.essen2Hints, binding.essen3Hints)
        val cards = arrayOf(binding.essen1, binding.essen2, binding.essen3)

        for (i in day.foods!!.indices) {
            @Suppress("SENSELESS_COMPARISON")
            if (day.foods!![i] == null) {
                cards[i].visibility = View.GONE
                continue
            }
            val food = day.foods!![i]
            titles[i].text = buildString {
                append("Essen ")
                append(i + 1)
                append(getTextToDiet(food.diet))
            }
            names[i].text = food.name
            prices[i].text = buildString { append(food.price) }
            var hintList = ""
            for (hint in food.hints) {
                hintList += " ${hint.name}"
            }
            hints[i].text = hintList
        }

    }

    private fun getTextToDiet(diet: Int): String {
        return when (diet) {
            Food().VEGETARIAN -> " (fleischlos)"
            Food().VEGAN -> " (vegan)"
            else -> ""
        }
    }


    @Suppress("DEPRECATION")
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
                AlarmReceiver().setNotificationAlarm(
                    this,
                    now.get(Calendar.DAY_OF_WEEK),
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE) + 1,
                    0,
                    false
                )

                val prefs =
                    this.getSharedPreferences(
                        "com.sabbelkrabbe.mymensanotification",
                        Context.MODE_PRIVATE
                    )
                selectedMensa = prefs.getString("Mensa", "Mensa 71")!!

                if (selectedMensa == "Mensa 71")
                    AlarmReceiver().makeNotification(this, 0)
                else
                    AlarmReceiver().makeNotification(this, 1)
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

    override fun onRestart() {
        super.onRestart()
        updateMensa()

        day = LocalDate.now().dayOfWeek
        binding.txtDay.text = DayConverter().getDay(resources, day)

        if (day < DayOfWeek.FRIDAY) {
            if (selectedMensa == "Mensa 71")
                setFoodCards(weekMenuMensa71[day.value - 1]!!)
            else
                setFoodCards(weekMenuMainMensa[day.value - 1]!!)
            binding.txtDay.text = DayConverter().getDay(resources, day)
        }

        if (day == DayOfWeek.SATURDAY) {
            setFoodCards(weekMenuMainMensa[day.value - 1]!!)
            binding.txtDay.text = DayConverter().getDay(resources, day)
            supportActionBar?.title = "Zentralmensa"
        }
    }

    private fun updateMensa() {
        val prefs =
            this.getSharedPreferences("com.sabbelkrabbe.mymensanotification", Context.MODE_PRIVATE)
        selectedMensa = prefs.getString("Mensa", "Mensa 71")!!

        supportActionBar?.title = selectedMensa
    }
}
