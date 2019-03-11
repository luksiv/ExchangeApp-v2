package com.paysera.currencyconverter.currencyconversion.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User(
        @PrimaryKey
        var userId: Int = 0,
        var accounts: RealmList<Account> = RealmList(),
        var exchangeHistory: RealmList<Exchange> = RealmList()

) : RealmObject() {

}