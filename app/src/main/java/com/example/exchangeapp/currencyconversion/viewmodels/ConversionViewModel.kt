package com.example.exchangeapp.currencyconversion.viewmodels

import android.annotation.SuppressLint
import com.example.exchangeapp.currencyconversion.api.EVPApiResponse
import com.example.exchangeapp.currencyconversion.helpers.CurrencyConversionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import javax.inject.Inject

class ConversionViewModel @Inject internal constructor(private val conversionHelper: CurrencyConversionHelper) {

    val exchangeProcessingObservable: PublishSubject<Boolean> = PublishSubject.create()
    val exchangeValueCalculationObservable: PublishSubject<Money> = PublishSubject.create()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    fun processExchange(fromMoney: Money, toCurrency: CurrencyUnit) {
        // nededu i compositeDisposable, nes jis yra completable
        conversionHelper.performExchange(fromMoney, toCurrency)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    exchangeProcessingObservable.onNext(true)
                }

                override fun onError(e: Throwable) {
                    exchangeProcessingObservable.onError(e)
                }
            })
    }

    @SuppressLint("CheckResult")
    fun calculateExchangeValue(fromMoney: Money, toCurrency: CurrencyUnit) {
        // nededu i compositeDisposable, nes jis yra single
        conversionHelper.getExchangeValueTo(fromMoney, toCurrency)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<EVPApiResponse>() {
                override fun onSuccess(t: EVPApiResponse) {
                    exchangeValueCalculationObservable.onNext(t.getAmountMoney())
                }

                override fun onError(e: Throwable) {
                    exchangeValueCalculationObservable.onError(e)
                }
            })
    }

    fun clearCompositeDisposable(){
        compositeDisposable.clear()
    }
}