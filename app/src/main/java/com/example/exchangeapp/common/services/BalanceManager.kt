package com.example.exchangeapp.common.services

import com.paysera.currencyconverter.currencyconversion.entities.Account
import io.realm.Realm
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import javax.inject.Singleton

@Singleton
class BalanceManager {


    fun isCurrencyAvailable(currencyUnit: CurrencyUnit): Boolean =
        Realm.getDefaultInstance().where(Account::class.java).equalTo("currency", currencyUnit.currencyCode).isValid

    fun isBalanceSufficient(moneyRequired: Money): Boolean {
        val realm = Realm.getDefaultInstance()
        if (isCurrencyAvailable(moneyRequired.currencyUnit)) {
            val account = realm.where(Account::class.java).equalTo("currency", moneyRequired.currencyUnit.currencyCode)
                .findFirst()
            return account.getBalance().minus(moneyRequired).isPositiveOrZero
        }
        return false
    }

    fun exchange(from: Money, to: Money) {
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                val accountFrom = it.where(Account::class.java).equalTo("currency", from.currencyUnit.currencyCode).findFirst()
                val accountTo = it.where(Account::class.java).equalTo("currency", to.currencyUnit.currencyCode).findFirst()
                accountFrom.setBalance(accountFrom.getBalance().minus(from))
                accountTo.setBalance(accountTo.getBalance().plus(to))
            }
        }
    }

}