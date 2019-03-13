package com.example.exchangeapp.currencyconversion.helpers

import android.util.Log
import com.example.exchangeapp.common.repositories.ExchangeHistoryRepository
import com.example.exchangeapp.common.services.BalanceManager
import com.example.exchangeapp.currencyconversion.api.CurrencyConversionApiClient
import com.example.exchangeapp.currencyconversion.entities.Exchange
import com.example.exchangeapp.currencyconversion.exceptions.BalanceInsufficientException
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyConversionHelper @Inject internal constructor(
    private val balanceManager: BalanceManager,
    private val currencyConversionApiClient: CurrencyConversionApiClient
) {

    companion object {
        const val freeExchanges = 10
        const val feePercentage = 0.007 // 0.7%
    }

    fun getExchangeValueTo(from: Money, to: CurrencyUnit): Money {
        return currencyConversionApiClient.calculate(from, to.currencyCode)
            .subscribeOn(Schedulers.io())
            .blockingGet()
            .getAmountMoney()
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
        realm.close()
    }

    private fun calculateExchangeFee(money: Money): Money = when (isExchangeFree()) {
        true -> Money.of(money.currencyUnit, 0.toBigDecimal())
        false -> money.multipliedBy(feePercentage, RoundingMode.HALF_DOWN)
    }

    private fun isExchangeFree(): Boolean {
        return ExchangeHistoryRepository(Realm.getDefaultInstance()).getExchangeCount() < freeExchanges
    }
}