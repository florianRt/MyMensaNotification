package com.sabbelkrabbe.mymensanotification

class Menu {
     fun getWeek(htmlPage: String, size: Int): Array<Day?> {

         var week = arrayOfNulls<Day>(size)
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
             var friday = htmlPage.substring(
                 htmlPage.indexOf("name=\"Freitag\">"),
                 htmlPage.indexOf("<!--Speise_end-->")
             )

             week[0] = getDay(monday)
             week[1] = getDay(tuesday)
             week[2] = getDay(wednesday)
             week[3] = getDay(thursday)

             if(week.size > 5){
                 val saturday = htmlPage.substring(
                     htmlPage.indexOf("name=\"Samstag\">"),
                     htmlPage.indexOf("<!--Speise_end-->")
                 )
                 friday = htmlPage.substring(
                     htmlPage.indexOf("name=\"Freitag\">"),
                     htmlPage.indexOf("name=\"Samstag\">")
                 )
                 week[5] = getDay(saturday)
             }
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
                foods.add(food)
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

    private fun getDay(dayString: String): Day {
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
        val food = Food()
        food.name = foodString.substring(
            foodString.indexOf("<strong>") + 8,
            foodString.indexOf("</strong>")
        ).trim { it <= ' ' }
        if (foodString.contains("class=\"preise\">")) {
            food.price = foodString.substring(
                foodString.indexOf("class=\"preise\">") + 15,
                foodString.indexOf("€</p>") + 1
            ).replace("\n","").replace("\t","").trim { it <= ' ' }
        } else {
            food.price = "Kein Preis verfügbar"
        }
        food.hints = getHints(foodString.trim { it <= ' ' })
        try {
            val dietString = foodString.substring(0, foodString.indexOf("tab_") - 1)
            println(dietString)
            if (dietString.contains("vegan")) {
                food.diet = food.VEGAN
                food.hints = arrayOf(Food.Hint("V", "Vegan")) + food.hints
            } else if (dietString.contains("fleischlos")) {
                food.diet = food.VEGETARIAN
                food.hints = arrayOf(Food.Hint("F", "Fleischlos")) + food.hints
            } else {
                food.diet = food.CARNISTIC
            }
        }catch (e: Exception){
//            e.printStackTrace()
            println("Error while getting diet: " + e.message)
            println("FoodString: $foodString")
            food.diet = food.CARNISTIC
        }
        return food
    }

    private fun getHints(text: String): Array<Food.Hint> {
        println("\n")
        var hintText = text
        println(text)
        hintText = hintText.substring(hintText.indexOf("title=\""), hintText.indexOf("\"><strong>"))
        println(hintText)
        val hints = ArrayList<Food.Hint>()
        if (hintText.contains("Kn=Knoblauch")) {
            hints.add(Food.Hint("Kn", "Knoblauch"))
        }
        if (hintText.contains("S=Schweinefleisch bzw. Schweinefleisch-Anteile")) {
            hints.add(Food.Hint("S", "Schweinefleisch bzw. Schweinefleisch-Anteile"))
        }
        if (hintText.contains("1=mit Farbstoff")) {
            hints.add(Food.Hint("1", "mit Farbstoff"))
        }
        if(hintText.contains("2=mit Konservierungsstoff")){
            hints.add(Food.Hint("2", "mit Konservierungsstoff"))
        }
        if (hintText.contains("3=mit Antioxidationsmittel")) {
            hints.add(Food.Hint("3", "mit Antioxidationsmittel"))
        }
        if (hintText.contains("4=mit Geschmacksverstärker")) {
            hints.add(Food.Hint("4", "mit Geschmacksverstärker"))
        }
        if (hintText.contains("5=geschwefelt")) {
            hints.add(Food.Hint("5", "geschwefelt"))
        }
        if (hintText.contains("6=geschwärzt")) {
            hints.add(Food.Hint("6", "geschwärzt"))
        }
        if (hintText.contains("7=gewachst")) {
            hints.add(Food.Hint("7", "gewachst"))
        }
        if (hintText.contains("8=mit Phosphat")) {
            hints.add(Food.Hint("8", "mit Phosphat"))
        }
        if (hintText.contains("9=mit Süßungsmittel(n)")) {
            hints.add(Food.Hint("9", "mit Süßungsmittel(n)"))
        }
        if (hintText.contains("10=enthält eine Phenylalaninquelle")) {
            hints.add(Food.Hint("10", "enthält eine Phenylalaninquelle"))
        }
        if(hintText.contains("31=Krebstiere")){
            hints.add(Food.Hint("31", "Krebstiere"))
        }
        if (hintText.contains("30=glutenhaltiges Getreide")) {
            hints.add(Food.Hint("30", "glutenhaltiges Getreide"))
        }
        if (hintText.contains("32=Eier")) {
            hints.add(Food.Hint("32", "Eier"))
        }
        if (hintText.contains("33=Fisch")) {
            hints.add(Food.Hint("33", "Fisch"))
        }
        if (hintText.contains("34=Erdnüsse")) {
            hints.add(Food.Hint("34", "Erdnüsse"))
        }
        if (hintText.contains("35=Soja")) {
            hints.add(Food.Hint("35", "Soja"))
        }
        if (hintText.contains("36=Milch/Milchzucker (Laktose)")) {
            hints.add(Food.Hint("36", "Milch/Milchzucker (Laktose)"))
        }
        if(hintText.contains("37=Schalenfrüchte")){
            hints.add(Food.Hint("37", "Schalenfrüchte (Nüsse)"))
        }
        if (hintText.contains("38=Sellerie")) {
            hints.add(Food.Hint("38", "Sellerie"))
        }
        if (hintText.contains("39=Senf")) {
            hints.add(Food.Hint("39", "Senf"))
        }
        if (hintText.contains("40=Sesam")) {
            hints.add(Food.Hint("40", "Sesam"))
        }
        if (hintText.contains("41=Schwefeldioxid / Sulfite")) {
            hints.add(Food.Hint("41", "Schwefeldioxid/Sulfite"))
        }
        if (hintText.contains("42=Lupinen")) {
            hints.add(Food.Hint("42", "Lupinen"))
        }
        if (hintText.contains("43=Weichtiere")) {
            hints.add(Food.Hint("43", "Weichtiere"))
        }
        if (hintText.contains("9a=")) {
            hints.add(Food.Hint("9a", "mit einer Zuckerart und Süßungsmitteln"))
        }
        if (hintText.contains("30a")) {
            hints.add(Food.Hint("30a", "Weizen, Dinkel, Khorasan-Weizen"))
        }
        if(hintText.contains("30b")){
            hints.add(Food.Hint("30b", "Roggen"))
        }
        if(hintText.contains("30c")){
            hints.add(Food.Hint("30c", "Gerste"))
        }
        if(hintText.contains("30d")){
            hints.add(Food.Hint("30d", "Hafer"))
        }
        if(hintText.contains("37a=")){
            hints.add(Food.Hint("37a", "Mandeln"))
        }
        if(hintText.contains("37b=")){
            hints.add(Food.Hint("37b", "Haselnüsse"))
        }
        if(hintText.contains("37c=")){
            hints.add(Food.Hint("37c", "Walnüsse"))
        }
        if(hintText.contains("37d=")){
            hints.add(Food.Hint("37d", "Cashewnüsse/Kaschnüsse"))
        }
        if(hintText.contains("37e=")){
            hints.add(Food.Hint("37e", "Pecannüsse"))
        }
        if(hintText.contains("37f=")){
            hints.add(Food.Hint("37f", "Paranüsse"))
        }
        if(hintText.contains("37g=")){
            hints.add(Food.Hint("37g", "Pistazien"))
        }
        if(hintText.contains("37h=")){
            hints.add(Food.Hint("37h", "Macadamia- oder Queenslandnüsse"))
        }


        //print all hints
        for (hint in hints) {
            println(hint.name+ " " + hint.description)
        }
        return hints.toTypedArray()
    }
}
