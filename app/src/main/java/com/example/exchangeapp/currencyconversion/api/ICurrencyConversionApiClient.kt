package com.example.exchangeapp.currencyconversion.api

import io.reactivex.Single
import org.joda.money.Money

interface ICurrencyConversionApiClient {
    fun calculate(fromMoney: Money, toCurrency: String): Single<EVPApiResponse>
}