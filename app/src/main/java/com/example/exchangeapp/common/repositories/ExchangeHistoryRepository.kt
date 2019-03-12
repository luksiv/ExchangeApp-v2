package com.example.exchangeapp.common.repositories

import com.example.exchangeapp.currencyconversion.entities.Exchange
import io.realm.Realm
import io.realm.RealmResults

class ExchangeHistoryRepository(realm: Realm) : EntityRepository<Exchange>(realm) {

    fun getAllHistory(): RealmResults<Exchange> = queryBuilder().findAll()

    fun getExchangeCount(): Int = getAllHistory().size

    override fun getPersistentType(): Class<Exchange> = Exchange::class.java
}