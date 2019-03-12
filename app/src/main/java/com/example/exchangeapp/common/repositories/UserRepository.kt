package com.example.exchangeapp.common.repositories

import android.util.Log
import com.example.exchangeapp.currencyconversion.entities.Account
import com.example.exchangeapp.currencyconversion.entities.User
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import org.joda.money.CurrencyUnit
import javax.inject.Inject

class UserRepository(realm: Realm) : EntityRepository<User>(realm) {

    override fun getPersistentType(): Class<User> = User::class.java

    fun setupUser() {
        Log.v("Realm", "Realm: setupUser called")
        realm.executeTransaction {
            if (it.where(User::class.java).findAll().isEmpty()) {
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

    fun getUser(): User = queryBuilder().equalTo("userId", 0).findFirst()

    fun getUserAccounts(): RealmList<Account> = getUser().accounts
}