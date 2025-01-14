package com.example.androidtbc

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val cardsList = mutableListOf<Card>()

    init {
        cardsList.addAll(
            listOf(
                Card(
                    cardNumber = "1234567891011234",
                    name = "rati",
                    validThru = "05/26",
                    type = CardType.VISA,
                    cvv = "123"
                ),
                Card(
                    cardNumber = "1234567891019192",
                    name = "OKEII",
                    validThru = "05/26",
                    type = CardType.MASTERCARD,
                    cvv = "532"
                ),
                Card(
                    cardNumber = "1234567891019192",
                    name = "OKEII",
                    validThru = "05/26",
                    type = CardType.MASTERCARD,
                    cvv = "532"
                ),
                Card(
                    cardNumber = "1234567891019192",
                    name = "OKEII",
                    validThru = "05/26",
                    type = CardType.VISA,
                    cvv = "111"
                ),
                Card(
                    cardNumber = "1234567891019192",
                    name = "OKEII",
                    validThru = "05/26",
                    type = CardType.MASTERCARD,
                    cvv = "646"
                ),
                Card(
                    cardNumber = "1234567891019192",
                    name = "OKEII",
                    validThru = "05/26",
                    type = CardType.VISA,
                    cvv = "124"
                ),
            )
        )
    }

    fun getCards(): List<Card> = cardsList.toList()

    fun deleteCard(position: Int) {
        cardsList.removeAt(position)
    }

    fun addCard(card: Card) {
        cardsList.add(0, card)
    }
}