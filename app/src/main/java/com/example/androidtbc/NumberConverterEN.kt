package com.example.androidtbc

class NumberConverterEN {
    fun numberToTextEN(n: Long): String {
        if (n == 0L) return "Zero"

        val firstTo19 = listOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        )

        val tenTo90 = listOf(
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        )

        val tmb = listOf("", "Thousand", "Million", "Billion")

        var number = n
        var result = ""
        var group = 0

        while (number > 0) {
            if (number % 1000 != 0L) {
                var value = (number % 1000).toInt()
                var smth = ""

                if (value >= 100) {
                    smth += "${firstTo19[value / 100]} Hundred "
                    value %= 100
                }

                if (value >= 20) {
                    smth += "${tenTo90[value / 10]} "
                    value %= 10
                }

                if (value > 0) {
                    smth += "${firstTo19[value]} "
                }

                smth += "${tmb[group]} "

                result = "$smth$result"
            }
            number /= 1000
            group++
        }

        return result.trim()
    }



}
