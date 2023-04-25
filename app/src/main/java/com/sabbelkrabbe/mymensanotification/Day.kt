package com.sabbelkrabbe.mymensanotification

class Day {
    var name: String? = null
    var foods: Array<Food>? = null

    fun getFood(i: Int): Food? {
        return if (foods != null && i < foods!!.size) {
            foods!![i]
        } else {
            null
        }
    }

    fun getFoodString():String {
        var foodString = ""

        if(foods != null) {
            for (food in foods!!) {
                foodString += food.name + " "
                if (food.hints.isNotEmpty()) {
                    foodString += "("
                    for (hint in food.hints) {
                        foodString += hint.name + ", "
                    }
                    foodString = foodString.substring(0, foodString.length - 2)
                    foodString += ") "
                }
                foodString += "\n" + food.price + "€ \n"
            }
            foodString = foodString.substring(0, foodString.length - 1)
        }
        return foodString
    }
}