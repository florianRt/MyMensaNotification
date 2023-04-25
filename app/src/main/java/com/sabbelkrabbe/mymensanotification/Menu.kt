package com.sabbelkrabbe.mymensanotification

class Menu {
     fun getWeek(htmlPage: String): Array<Day?>? {

         var week = arrayOfNulls<Day>(5)
         if(htmlPage.contains("Montag")) {
             val monday = htmlPage.substring(
                 htmlPage.indexOf("name=\"Montag\">"),
                 htmlPage.indexOf("name=\"Dienstag\">")
             )
             val tuesday = htmlPage.substring(
                 htmlPage.indexOf("name=\"Dienstag\">"),
                 htmlPage.indexOf("name=\"Mittwoch\">")
             )
             val wednesday = htmlPage.substring(
                 htmlPage.indexOf("name=\"Mittwoch\">"),
                 htmlPage.indexOf("name=\"Donnerstag\">")
             )
             val thursday = htmlPage.substring(
                 htmlPage.indexOf("name=\"Donnerstag\">"),
                 htmlPage.indexOf("name=\"Freitag\">")
             )
             val friday = htmlPage.substring(
                 htmlPage.indexOf("name=\"Freitag\">"),
                 htmlPage.indexOf("<!--Speise_end-->")
             )
             week[0] = getDay(monday)
             week[1] = getDay(tuesday)
             week[2] = getDay(wednesday)
             week[3] = getDay(thursday)
             week[4] = getDay(friday)
             week = cleanWeek(week)
         }

        return week
    }

    private fun cleanWeek(week: Array<Day?>): Array<Day?> {
        val days = ArrayList<Day>()
        for (day in week) {
            if (day != null) {
                days.add(day)
            }
        }
        for (day in week) {
            val foods = ArrayList<Food>()
            for (food in day!!.foods!!) {
                if (food != null) {
                    foods.add(food)
                }
            }
            day.foods = foods.toTypedArray()
        }
        for (day in week) {
            for (food in day!!.foods!!) {
                val hints = ArrayList<Food.Hint>()
                for (hint in food.hints) {
                    hints.add(hint)
                }
                food.hints = hints.toTypedArray()
            }
        }
        return days.toTypedArray()
    }

    private fun getDay(dayString: String): Day? {
        val foodList = ArrayList<Food>()
        val day = Day()
        day.name = dayString.substring(dayString.indexOf("name=\"") + 6, dayString.indexOf("\">"))
        val foodString1: String = if (dayString.contains("Essen 2")) {
            dayString.substring(dayString.indexOf("Essen 1"), dayString.indexOf("Essen 2"))
        } else {
            dayString
        }
        if (dayString.contains("Essen 1") && !foodString1.contains("Salate")) {
            foodList.add(getFoodByString(foodString1))
        }
        val foodString2: String = if (dayString.contains("Essen 3")) {
            dayString.substring(dayString.indexOf("Essen 2"), dayString.indexOf("Essen 3"))
        } else {
            dayString.substring(dayString.indexOf("Essen 2"))
        }
        if (foodString2.contains("Essen 2") && !foodString1.contains("Salate")) {
            foodList.add(getFoodByString(foodString2))
        }
        if (dayString.contains("Essen 3") && !foodString1.contains("Salate")) {
            foodList.add(getFoodByString(dayString.substring(dayString.indexOf("Essen 3"))))
        }
        day.foods = foodList.toTypedArray()
        return day
    }

    private fun getFoodByString(foodString: String): Food {
        val food1 = Food()
        food1.name = foodString.substring(
            foodString.indexOf("<strong>") + 8,
            foodString.indexOf("</strong>")
        ).trim { it <= ' ' }
        if (foodString.contains("class=\"preise\">")) {
            food1.price = foodString.substring(
                foodString.indexOf("class=\"preise\">") + 15,
                foodString.indexOf("€</p>") + 1
            ).replace("\n","").replace("\t","").trim { it <= ' ' }
        } else {
            food1.price = "Kein Preis verfügbar"
        }
        food1.hints = getHints(foodString.trim { it <= ' ' })
        if (foodString.contains("vegan")) {
            food1.diet = food1.VEGAN
        } else if (foodString.contains("fleischlos")) {
            food1.diet = food1.VEGETARIAN
        } else {
            food1.diet = food1.CARNISTIC
        }
        return food1
    }

    private fun getHints(hintText: String): Array<Food.Hint> {
        var hintText = hintText
        hintText = hintText.substring(hintText.indexOf("title=\""), hintText.indexOf("\"><strong>"))
        println(hintText)
        val hints = ArrayList<Food.Hint>()
        if (hintText.contains("Kn=Knoblauch")) {
            hints.add(Food.Hint("Kn", "Knoblauch"))
        }
        if (hintText.contains("30=glutenhaltiges Getreide")) {
            hints.add(Food.Hint("30", "glutenhaltiges Getreide"))
        }
        if (hintText.contains("30a")) {
            var weeds = ""
            if (hintText.contains("Weizen")) {
                weeds += "Weizen, "
            }
            if (hintText.contains("Gerste")) {
                weeds += "Gerste, "
            }
            if (hintText.contains("Roggen")) {
                weeds += "Roggen, "
            }
            if (hintText.contains("Dinkel")) {
                weeds += "Dinkel, "
            }
            if (hintText.contains("Kamut")) {
                weeds += "Kamut, "
            }
            if (hintText.contains("Khorsan-Weizen")) {
                weeds += "Khorsan-Weizen, "
            }
            hints.add(Food.Hint("30a", weeds))
        }
        if (hintText.contains("S=Schweinefleisch bzw. Schweinefleisch-Anteile")) {
            hints.add(Food.Hint("S", "Schweinefleisch bzw. Schweinefleisch-Anteile"))
        }
        if (hintText.contains("3=mit Antioxidationsmittel")) {
            hints.add(Food.Hint("3", "mit Antioxidationsmittel"))
        }
        if (hintText.contains("32=Eier")) {
            hints.add(Food.Hint("32", "Eier"))
        }
        if (hintText.contains("36=Milch/Milchzucker (Laktose)")) {
            hints.add(Food.Hint("36", "Milch/Milchzucker (Laktose)"))
        }
        if (hintText.contains("35=Soja")) {
            hints.add(Food.Hint("35", "Soja"))
        }
        if (hintText.contains("38=Sellerie")) {
            hints.add(Food.Hint("38", "Sellerie"))
        }
        if (hintText.contains("39=Senf")) {
            hints.add(Food.Hint("39", "Senf"))
        }
        if (hintText.contains("1=mit Farbstoff")) {
            hints.add(Food.Hint("1", "mit Farbstoff"))
        }
        if (hintText.contains("40=Sesam")) {
            hints.add(Food.Hint("40", "Sesam"))
        }
        return hints.toTypedArray()
    }
}
