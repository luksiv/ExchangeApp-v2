package com.example.exchangeapp.currencyconversion.helpers

import android.util.Log
import com.example.exchangeapp.common.services.BalanceManager
import com.example.exchangeapp.currencyconversion.exceptions.BalanceInsufficientException
import com.paysera.currencyconverter.currencyconversion.api.CurrencyConversionApiClient
import com.paysera.currencyconverter.currencyconversion.entities.Exchange
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyConversionHelper @Inject internal constructor(
    private val balanceManager: BalanceManager,
    private val currencyConversionApiClient: CurrencyConversionApiClient
) {

    companion object {
        val freeExchanges = 10
        val feeProcentage = 0.007 // 0.7%
    }

    fun performExchange(from: Money, to: CurrencyUnit): Completable {
        return Completable.fromAction {
            if (balanceManager.isBalanceSufficient(from)) {
                Log.v("Exchange", "From: $from")
                val fee = calculateExchangeFee(from)
                Log.v("Exchange", "Fee: $fee")
                val adjustedFrom = from.minus(fee)
                Log.v("Exchange", "Adjusted from: $adjustedFrom")
                val exchangeValue = getExchangeValueTo(adjustedFrom, to)
                Log.v("Exchange", "Exchange value: $exchangeValue")
                balanceManager.exchange(from, exchangeValue)
                registerExchange(from, exchangeValue, fee)
            } else {
                Log.e("Exchange", "Request denied! Not enough money!")
                throw BalanceInsufficientException()
            }
        }

    }

    private fun registerExchange(from: Money, to: Money, fee: Money) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val id = it.where(Exchange::class.java).max("transactionId")?.toInt() ?: -1
            val exchange = it.createObject(Exchange::class.java, id + 1)
            exchange.let {
                it.amount = from.amount.toPlainString()
                it.currencyFrom = from.currencyUnit.currencyCode
                it.amountTo = to.amount.toPlainString()
                it.currencyTo = to.currencyUnit.currencyCode
                it.feeAmount = fee.amount.toPlainString()
            }
        }
    }

    fun getExchangeValueTo(from: Money, to: CurrencyUnit): Money =
        currencyConversionApiClient.calculate(from, to.currencyCode)
            .subscribeOn(Schedulers.io())
            .blockingGet()
            .getAmountMoney()

    private fun calculateExchangeFee(money: Money): Money = when (isExchangeFree()) {
        true -> Money.of(money.currencyUnit, BigDecimal("0"))
        false -> money.multipliedBy(feeProcentage, RoundingMode.HALF_DOWN)
    }

    private fun isExchangeFree(): Boolean =
        Realm.getDefaultInstance().where(Exchange::class.java).count() < freeExchanges


}