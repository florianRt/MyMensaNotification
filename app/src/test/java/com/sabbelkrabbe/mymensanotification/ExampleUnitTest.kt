package com.sabbelkrabbe.mymensanotification

import android.content.res.Resources
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun getMenu() {
        assertEquals(4, 2 + 2)
        every {
            resources.getString(R.string.menu_website)
        } returns WEBSITE

        assert(Menu().getWeek(WEBSITE).size == 5);
    }

    companion object {
        private const val WEBSITE = "https://www.mensa-erlangen.de/mensa-erlangen/wochenkarte/"
    }

    @MockK
    private lateinit var resources: Resources

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }
}