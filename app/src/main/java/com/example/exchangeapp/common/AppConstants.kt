package com.example.exchangeapp.common

import org.joda.money.CurrencyUnit

object AppConstants {
    val currencies = listOf(
        CurrencyUnit.of("EUR"),
        CurrencyUnit.of("USD"),
        CurrencyUnit.of("JPY")
    )
}