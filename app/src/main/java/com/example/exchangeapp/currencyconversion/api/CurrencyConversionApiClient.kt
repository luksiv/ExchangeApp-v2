package com.example.exchangeapp.currencyconversion.api

import io.reactivex.Single
import org.joda.money.Money
import javax.inject.Inject

interface ICurrencyConversionApiClient {
    fun calculate(fromMoney: Money, toCurrency: String): Single<EVPApiResponse>
}

class CurrencyConversionApiClient @Inject internal constructor(
    private val evpApiService: EVPApiService
) : ICurrencyConversionApiClient {
    override fun calculate(fromMoney: Money, toCurrency: String): Single<EVPApiResponse> {
        return evpApiService.getExchangeValue(fromMoney.amount, fromMoney.currencyUnit.currencyCode, toCurrency)
    }
}
