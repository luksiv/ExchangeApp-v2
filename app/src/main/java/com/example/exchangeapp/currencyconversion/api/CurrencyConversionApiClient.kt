package com.paysera.currencyconverter.currencyconversion.api

import com.paysera.currencyconverter.currencyconversion.services.EVPApiService
import org.joda.money.Money
import javax.inject.Inject

class CurrencyConversionApiClient @Inject internal constructor(
    private val mEVPApiService: EVPApiService
) : ICurrencyConversionApiClient {
    override fun calculate(fromAmount: Money, toCurrency: String) = mEVPApiService.getExchangeValue(fromAmount.amount, fromAmount.currencyUnit.currencyCode, toCurrency)
}
