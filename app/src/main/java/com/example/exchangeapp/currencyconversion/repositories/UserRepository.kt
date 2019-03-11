package com.example.exchangeapp.currencyconversion.repositories

import android.util.Log
import com.paysera.currencyconverter.currencyconversion.entities.Account
import com.paysera.currencyconverter.currencyconversion.entities.User
import io.realm.Realm
import org.joda.money.CurrencyUnit
import javax.inject.Inject

class UserRepository @Inject internal constructor(private val mRealm: Realm) {



    fun setupUser() {
        Log.v("Realm", "Realm: setupUser called")
        mRealm.executeTransaction {
            val userCount = it.where(User::class.java).count()
            if (userCount == 0L) {
                it.deleteAll()
                val user = it.createObject(User::class.java, 0)
                val accountEur = it.createObject(Account::class.java, 0)
                accountEur.name = "EUR ACCOUNT"
                accountEur.currency = CurrencyUnit.EUR.currencyCode
                accountEur.balance = "1000.00"
                user.accounts.add(accountEur)
                val accountUsd = it.createObject(Account::class.java, 1)
                accountUsd.name = "USD ACCOUNT"
                accountUsd.currency = CurrencyUnit.USD.currencyCode
                accountUsd.balance = "0"
                user.accounts.add(accountUsd)
                val accountJpy = it.createObject(Account::class.java, 2)
                accountJpy.name = "JPY ACCOUNT"
                accountJpy.currency = CurrencyUnit.JPY.currencyCode
                accountJpy.balance = "0"
                user.accounts.add(accountJpy)
                Log.v("Realm", "Realm: added user")
            }
        }
    }

    fun getUser(): User {
        print("Realm: getting user")
        return mRealm.where(User::class.java).equalTo("userId", 0).findFirst()
    }

}