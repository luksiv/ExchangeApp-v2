package com.paysera.currencyconverter.currencyconversion.api

import com.google.gson.annotations.SerializedName
import com.paysera.currencyconverter.currencyconversion.services.EVPApiResponse
import io.reactivex.Single
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal

interface ICurrencyConversionApiClient {

    fun calculate(fromMoney: Money, toCurrency: String): Single<EVPApiResponse>

}