package com.example.exchangeapp.common.repositories

import com.example.exchangeapp.currencyconversion.entities.Exchange
import io.realm.Realm
import io.realm.RealmResults
import org.joda.money.CurrencyUnit

class ExchangeHistoryRepository(realm: Realm) : EntityRepository<Exchange>(realm) {
    fun getAllHistory(): RealmResults<Exchange> = queryBuilder().findAll()

    fun getExchangeCount(): Int = getAllHistory().size

    fun getAllFromCurrencyHistory(currency: CurrencyUnit): RealmResults<Exchange> =
        queryBuilder().equalTo("currencyFrom", currency.currencyCode).findAll()

    override fun getPersistentType(): Class<Exchange> = Exchange::class.java
}