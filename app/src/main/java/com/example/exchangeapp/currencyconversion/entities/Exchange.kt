package com.example.exchangeapp.currencyconversion.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

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
) : RealmObject()