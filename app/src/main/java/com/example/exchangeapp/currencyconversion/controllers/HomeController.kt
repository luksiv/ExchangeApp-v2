package com.example.exchangeapp.currencyconversion.controllers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.example.exchangeapp.CurrencyConversionApplication
import com.example.exchangeapp.common.AppConstants
import com.example.exchangeapp.common.controllers.BaseController
import com.example.exchangeapp.currencyconversion.helpers.CurrencyConversionHelper
import com.example.exchangeapp.currencyconversion.exceptions.BalanceInsufficientException
import com.example.exchangeapp.currencyconversion.views.HomeView
import com.example.exchangeapp.currencyconversion.views.HomeViewDelegate
import com.paysera.currencyconverter.currencyconversion.entities.Account
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import javax.inject.Inject

class HomeController : BaseController(), HomeViewDelegate {

    @Inject
    lateinit var conversionManager: CurrencyConversionHelper

    private var contentView: HomeView? = null

    override fun inject() {
        (applicationContext as CurrencyConversionApplication).mAppComponent.inject(this)
    }

    override fun onCreateControllerView(inflater: LayoutInflater, container: ViewGroup): View =
        HomeView(inflater.context).also {
//                        Realm.getDefaultInstance().use {realm ->
//                realm.executeTransaction {
//                    it.deleteAll()
//                }
//                UserRepository(realm).setupUser()
//                realm.close()
//            }
            contentView = it
            it.homeViewDelegate = this
            val currencies = AppConstants.currencies.map { currencyUnit -> currencyUnit.currencyCode }.toList()
            val accountsResults = getAccountsList()
            it.configureViews(currencies, accountsResults)
        }

    override fun onHistoryClick() {
        router.pushController(
            RouterTransaction.with(HistoryController())
                .popChangeHandler(HorizontalChangeHandler())
                .pushChangeHandler(HorizontalChangeHandler())
        )
    }

    override fun onAmountChanged(fromMoney: Money, toCurrency: CurrencyUnit) {
        Log.v("Rx", "onAmountChanged called")
        Single.fromCallable { conversionManager.getExchangeValueTo(fromMoney, toCurrency) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { toMoney ->
                    Log.v("onAmountChangedExchange", "$fromMoney -> $toMoney")
                    contentView?.updateExchangeValue(toMoney)
                },
                { err ->
                    Log.e("onAmountChangedEx", "$err")
                }
            )
    }

    override fun onExchangeSubmit(fromMoney: Money, toCurrency: CurrencyUnit) {
        conversionManager.performExchange(fromMoney, toCurrency)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.v("Exchange", "Conversion success")
                    contentView?.showConversionResult(true, "Conversion successful")
                },
                onError = { err ->
                    when (err) {
                        BalanceInsufficientException() -> {
                            Log.e("Exchange", "Balance not sufficient exception")
                            contentView?.showConversionResult(false, reason = "Balance insufficient")
                        }
                        else -> {
                            Log.e("Exchange", "$err")
                            contentView?.showConversionResult(false, reason = "${err}")
                        }
                    }
                }
            )
    }

    override fun getAccountsList(): RealmResults<Account> =
        Realm.getDefaultInstance().where(Account::class.java).findAll()

}
