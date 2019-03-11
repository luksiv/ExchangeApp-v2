package com.paysera.currencyconverter.currencyconversion.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal

open class Account(
        @PrimaryKey
        var accountId: Int = 0,
        @Required
        var name: String = "",
        @Required
        var currency: String = "",
        @Required
        var balance: String = ""
) : RealmObject() {
        fun getBalance() = Money.of(CurrencyUnit.of(currency), BigDecimal(balance))
        fun setBalance(money: Money) {
            balance = money.amount.toPlainString()
        }
}

