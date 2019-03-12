package com.paysera.currencyconverter.currencyconversion.api

import com.paysera.currencyconverter.currencyconversion.services.EVPApiResponse
import com.paysera.currencyconverter.currencyconversion.services.EVPApiService
import io.reactivex.Single
import org.joda.money.Money
import javax.inject.Inject

class CurrencyConversionApiClient @Inject internal constructor(
    private val mEVPApiService: EVPApiService
) : ICurrencyConversionApiClient {
    override fun calculate(fromMoney: Money, toCurrency: String): Single<EVPApiResponse> {
        return mEVPApiService.getExchangeValue(fromMoney.amount, fromMoney.currencyUnit.currencyCode, toCurrency)
    }
}
