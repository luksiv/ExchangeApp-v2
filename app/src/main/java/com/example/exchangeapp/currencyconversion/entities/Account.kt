package com.paysera.currencyconverter.currencyconversion.entities

import android.util.Log
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

    val currencyUnit: CurrencyUnit
        get() {
            return CurrencyUnit.of(currency)
        }

    fun getBalance() = Money.of(CurrencyUnit.of(currency), BigDecimal(balance))
    fun setBalance(money: Money) {
        balance = money.amount.toPlainString()
    }

    fun getAppliedFeesSum(): Money {
        var appliedFees = Money.of(currencyUnit, BigDecimal("0"))
        Realm.getDefaultInstance().executeTransaction {
            it.where(Exchange::class.java).equalTo("currencyFrom", currencyUnit.currencyCode)
                .findAll()
                .filter { it.feeAmount != "0.00" }
                .map {
                    Log.v("AppliedFeesInside", it.feeAmount)
                    appliedFees = appliedFees.plus(BigDecimal(it.feeAmount))
                }
        }
        return appliedFees
    }
}

