package com.paysera.currencyconverter.currencyconversion.services

import com.google.gson.annotations.SerializedName
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal

data class EVPApiResponse(
        @SerializedName("amount")
        val amount: String,
        @SerializedName("currency")
        val currency: String
) {
    fun getAmountMoney() = Money.of(CurrencyUnit.of(currency), BigDecimal(amount))
}
