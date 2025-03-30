package com.example.androidtbc.presentation.model

import android.os.Parcelable
import com.example.androidtbc.domain.model.Account
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountUI(
    val id: Int,
    val accountName: String,
    val accountNumber: String,
    val valuteType: CurrencyTypeUI,
    val cardType: CardTypeUI,
    val balance: Double,
    val cardLogo: String?,
    val maskedNumber: String
) : Parcelable {
    companion object {
        fun fromDomain(account: Account): AccountUI {
            val maskedNumber = if (account.accountNumber.length > 4) {
                "**** ${account.accountNumber.takeLast(4)}"
            } else {
                account.accountNumber
            }

            val roundedBalance = (Math.round(account.balance * 100) / 100.0)

            return AccountUI(
                id = account.id,
                accountName = account.accountName,
                accountNumber = account.accountNumber,
                valuteType = CurrencyTypeUI.fromDomain(account.valuteType),
                cardType = CardTypeUI.fromDomain(account.cardType),
                balance = roundedBalance,
                cardLogo = account.cardLogo,
                maskedNumber = maskedNumber
            )
        }
    }
}
