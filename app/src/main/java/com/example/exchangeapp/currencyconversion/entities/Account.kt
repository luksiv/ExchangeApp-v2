package com.example.exchangeapp.currencyconversion.entities

import com.example.exchangeapp.common.repositories.ExchangeHistoryRepository
import io.realm.Realm
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

    private val currencyUnit: CurrencyUnit
        get() {
            return CurrencyUnit.of(currency)
        }

    fun getBalance(): Money = Money.of(CurrencyUnit.of(currency), BigDecimal(balance))
    fun setBalance(money: Money) {
        balance = money.amount.toPlainString()
    }

    fun getAppliedFeesSum(): Money {
        var appliedFees = Money.of(currencyUnit, 0.toBigDecimal())
        Realm.getDefaultInstance().let {
            ExchangeHistoryRepository(it).getAllFromCurrencyHistory(currencyUnit)
                .filter { exchange ->  exchange.feeAmount != "0.00" }
                .map { appliedFees = appliedFees.plus(BigDecimal(it.feeAmount)) }
        }
        return appliedFees
    }
}

