package com.paysera.currencyconverter.currencyconversion.api

import com.paysera.currencyconverter.currencyconversion.services.EVPApiResponse
import io.reactivex.Single
import org.joda.money.Money

interface ICurrencyConversionApiClient {
    fun calculate(fromMoney: Money, toCurrency: String): Single<EVPApiResponse>
}