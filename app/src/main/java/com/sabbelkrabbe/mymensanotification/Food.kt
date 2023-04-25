package com.sabbelkrabbe.mymensanotification

class Food {
    val CARNISTIC = 0
    val VEGETARIAN = 1
    val VEGAN = 2

    var name: String? = null
    var price: String? = null
    lateinit var hints: Array<Hint>
    var diet = CARNISTIC


    class Hint(val name: String, val description: String)
}