class NumberConverter {
    fun numberToText(number: Long): String {
        val numbers = mutableMapOf(
            1L to "ერთი",
            2L to "ორი",
            3L to "სამი",
            4L to "ოთხი",
            5L to "ხუთი",
            6L to "ექვსი",
            7L to "შვიდი",
            8L to "რვა",
            9L to "ცხრა",
            10L to "ათი",
            11L to "თერთმეტი",
            12L to "თორმეტი",
            13L to "ცამეტი",
            14L to "თოთხმეტი",
            15L to "თხუთმეტი",
            16L to "თექვსმეტი",
            17L to "ჩვიდმეტი",
            18L to "თვრამეტი",
            19L to "ცხრამეტი",
            20L to "ოცი",
            30L to "ოცდაათი",
            40L to "ორმოცი",
            50L to "ორმოცდაათი",
            60L to "სამოცი",
            70L to "სამოცდაათი",
            80L to "ოთხმოცი",
            90L to "ოთხმოცდაათი",
            100L to "ასი",
            200L to "ორასი",
            300L to "სამასი",
            400L to "ოთხასი",
            500L to "ხუთასი",
            600L to "ექვსასი",
            700L to "შვიდასი",
            800L to "რვაასი",
            900L to "ცხრაასი",
            1000L to "ათასი",
            1000000L to "მილიონი",
            1000000000L to "მილიარდი",
            1000000000000L to "ტრილიონი"
        )

        val shortenHundreds = mapOf(
            100L to "ას",
            200L to "ორას",
            300L to "სამას",
            400L to "ოთხას",
            500L to "ხუთას",
            600L to "ექვსას",
            700L to "შვიდას",
            800L to "რვაას",
            900L to "ცხრაას"
        )

        //თუ input რიცხვი უშუალოდ პირდაპირ მოიძებნება ჩვენს map-ებში, დააბრუნოს. მარტივად
        if (numbers.containsKey(number)) {
            return numbers[number]!!
        }

        if (shortenHundreds.containsKey(number)) {
            return shortenHundreds[number]!!
        }


        return when {
            //ტრილიონები
            number in 1000000000000L..999999999999999L -> {
                val trillions = number / 1000000000000L
                val remainder = number % 1000000000000L

                val trillionText = numberToText(trillions) + " ტრილიონ"

                if (remainder == 0L) {
                    trillionText
                } else {
                    trillionText + " " + numberToText(remainder)
                }
            }

            //მილიარდები
            number in 1000000000L..999999999999L -> {
                val billions = number / 1000000000L
                val remainder = number % 1000000000L

                val billionText = numberToText(billions) + " მილიარდ"

                if (remainder == 0L) {
                    billionText
                } else {
                    billionText + " " + numberToText(remainder)
                }
            }

            //მილიონები
            number in 1000000L..999999999L -> {
                val millions = number / 1000000L
                val remainder = number % 1000000L

                val millionText = numberToText(millions) + " მილიონ"

                if (remainder == 0L) {
                    millionText
                } else {
                    millionText + " " + numberToText(remainder)
                }
            }
            //ათასეულები
            number in 1000L..999999L -> {
                val thousands = number / 1000L
                val remainder = number % 1000L

                val thousandText = if (thousands == 1L) {
                    "ათას"
                } else {
                    numberToText(thousands) + " ათას"
                }

                if (remainder == 0L) {
                    thousandText
                } else {
                    thousandText + " " + numberToText(remainder)
                }
            }

            number in 21L..29L -> {
                when (number % 10L) {
                    1L -> "ოცდაერთი"
                    2L -> "ოცდაორი"
                    3L -> "ოცდასამი"
                    4L -> "ოცდაოთხი"
                    5L -> "ოცდახუთი"
                    6L -> "ოცდაექვსი"
                    7L -> "ოცდაშვიდი"
                    8L -> "ოცდარვა"
                    9L -> "ოცდაცხრა"
                    else -> "ოცი"
                }
            }

            number in 31L..39L -> {
                when (number % 10L) {
                    1L -> "ოცდათერთმეტი"
                    2L -> "ოცდათორმეტი"
                    3L -> "ოცდაცამეტი"
                    4L -> "ოცდათოთხმეტი"
                    5L -> "ოცდათხუთმეტი"
                    6L -> "ოცდათექვსმეტი"
                    7L -> "ოცდაჩვიდმეტი"
                    8L -> "ოცდათვრამეტი"
                    9L -> "ოცდაცხრამეტი"
                    else -> "ოცდაათი"
                }
            }

            number in 41L..49L -> {
                when (number % 10L) {
                    1L -> "ორმოცდაერთი"
                    2L -> "ორმოცდაორი"
                    3L -> "ორმოცდასამი"
                    4L -> "ორმოცდაოთხი"
                    5L -> "ორმოცდახუთი"
                    6L -> "ორმოცდაექვსი"
                    7L -> "ორმოცდაშვიდი"
                    8L -> "ორმოცდარვა"
                    9L -> "ორმოცდაცხრა"
                    else -> "ორმოცი"
                }
            }

            number in 51L..59L -> {
                when (number % 10L) {
                    1L -> "ორმოცდათერთმეტი"
                    2L -> "ორმოცდათორმეტი"
                    3L -> "ორმოცდაცამეტი"
                    4L -> "ორმოცდათოთხმეტი"
                    5L -> "ორმოცდათხუთმეტი"
                    6L -> "ორმოცდათექვსმეტი"
                    7L -> "ორმოცდაჩვიდმეტი"
                    8L -> "ორმოცდათვრამეტი"
                    9L -> "ორმოცდაცხრამეტი"
                    else -> "ორმოცდაათი"
                }
            }

            number in 61L..69L -> {
                when (number % 10L) {
                    1L -> "სამოცდაერთი"
                    2L -> "სამოცდაორი"
                    3L -> "სამოცდასამი"
                    4L -> "სამოცდაოთხი"
                    5L -> "სამოცდახუთი"
                    6L -> "სამოცდაექვსი"
                    7L -> "სამოცდაშვიდი"
                    8L -> "სამოცდარვა"
                    9L -> "სამოცდაცხრა"
                    else -> "სამოცი"
                }
            }

            number in 71L..79L -> {
                when (number % 10L) {
                    1L -> "სამოცდათერთმეტი"
                    2L -> "სამოცდათორმეტი"
                    3L -> "სამოცდაცამეტი"
                    4L -> "სამოცდათოთხმეტი"
                    5L -> "სამოცდათხუთმეტი"
                    6L -> "სამოცდათექვსმეტი"
                    7L -> "სამოცდაჩვიდმეტი"
                    8L -> "სამოცდათვრამეტი"
                    9L -> "სამოცდაცხრამეტი"
                    else -> "სამოცდაათი"
                }
            }

            number in 81L..89L -> {
                when (number % 10L) {
                    1L -> "ოთხმოცდაერთი"
                    2L -> "ოთხმოცდაორი"
                    3L -> "ოთხმოცდასამი"
                    4L -> "ოთხმოცდაოთხი"
                    5L -> "ოთხმოცდახუთი"
                    6L -> "ოთხმოცდაექვსი"
                    7L -> "ოთხმოცდაშვიდი"
                    8L -> "ოთხმოცდარვა"
                    9L -> "ოთხმოცდაცხრა"
                    else -> "ოთხმოცი"
                }
            }

            number in 91L..99L -> {
                when (number % 10L) {
                    1L -> "ოთხმოცდათერთმეტი"
                    2L -> "ოთხმოცდათორმეტი"
                    3L -> "ოთხმოცდაცამეტი"
                    4L -> "ოთხმოცდათოთხმეტი"
                    5L -> "ოთხმოცდათხუთმეტი"
                    6L -> "ოთხმოცდათექვსმეტი"
                    7L -> "ოთხმოცდაჩვიდმეტი"
                    8L -> "ოთხმოცდათვრამეტი"
                    9L -> "ოთხმოცდაცხრამეტი"
                    else -> "ოთხმოცდაათი"
                }
            }

            number in 101L..199L -> {
                val hundred = 100L
                shortenHundreds[hundred]!! + " " + numberToText(number % 100L)
            }

            number in 200L..999L -> {
                val hundreds = (number / 100L) * 100L
                val remainder = number % 100L

                if (remainder == 0L) {
                    numbers[hundreds]!!
                } else {
                    shortenHundreds[hundreds]!! + " " + numberToText(remainder)
                }
            }

            number == 0L -> "ნული"
            number < 0 -> "გთხოვთ შეიყვანოთ დადებითი რიცხვი"
            else -> "არასწორი რიცხვი"
        }
    }
}