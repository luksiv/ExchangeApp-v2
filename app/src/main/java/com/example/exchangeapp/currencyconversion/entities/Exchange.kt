package com.paysera.currencyconverter.currencyconversion.entities

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal

open class Exchange(
    @PrimaryKey
        var transactionId: Int = 0,
    @Required
        var currencyFrom: String = "",
    @Required
        var amount: String = "",
    @Required
        var currencyTo: String = "",
    @Required
        var amountTo: String = "",
    @Required
        var feeAmount: String = ""
) : RealmObject() {

}