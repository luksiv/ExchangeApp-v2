package com.example.exchangeapp.currencyconversion.controllers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.example.exchangeapp.CurrencyConversionApplication
import com.example.exchangeapp.common.controllers.BaseController
import com.example.exchangeapp.common.repositories.UserRepository
import com.example.exchangeapp.currencyconversion.exceptions.BalanceInsufficientException
import com.example.exchangeapp.currencyconversion.helpers.CurrencyConversionHelper
import com.example.exchangeapp.currencyconversion.viewmodels.ConversionViewModel
import com.example.exchangeapp.currencyconversion.views.HomeView
import com.example.exchangeapp.currencyconversion.views.HomeViewDelegate
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import javax.inject.Inject

class HomeController : BaseController(), HomeViewDelegate {
    @Inject
    lateinit var conversionHelper: CurrencyConversionHelper
    private val compositeDisposable = CompositeDisposable()
    private var contentView: HomeView? = null
    private var conversionViewModel: ConversionViewModel? = null

    override fun inject() {
        (applicationContext as CurrencyConversionApplication).appComponent.inject(this)
    }

    override fun onCreateControllerView(inflater: LayoutInflater, container: ViewGroup): View {
        return HomeView(inflater.context).also {
            Realm.getDefaultInstance().use { realm ->
                UserRepository(realm).setupUser()
                realm.close()
            }
            contentView = it
            it.homeViewDelegate = this
            val accountsResults = UserRepository(Realm.getDefaultInstance()).getUserAccounts()
            it.setupAccountAdapter(accountsResults)
            it.setCurrencySpinners()
        }
    }


    override fun onAttach(view: View) {
        super.onAttach(view)

        conversionViewModel = ConversionViewModel(conversionHelper).also {
            val exchangeDisposable = it.exchangeProcessingObservable.subscribe(
                {
                    Log.v("Exchange", "ConversionViewModel success")
                    Realm.getDefaultInstance().let {realm ->
                        UserRepository(realm).getUserAccounts().let {
                            contentView?.updateAccountsList(it)
                        }
                        realm.close()
                    }
                    contentView?.showConversionResult(true, "ConversionViewModel successful")
                },
                { err ->
                    when (err) {
                        BalanceInsufficientException() -> {
                            Log.e("Exchange", "Balance not sufficient exception")
                            contentView?.showConversionResult(false, reason = "Balance insufficient")
                        }
                        else -> {
                            Log.e("Exchange", "$err")
                            contentView?.showConversionResult(false, reason = "$err")
                        }
                    }
                }
            )
            compositeDisposable.add(exchangeDisposable)
            val valueCalcDisposable = it.exchangeValueCalculationObservable.subscribe(
                { exchangeValue ->
                    contentView?.updateExchangeValue(exchangeValue)
                },
                { err ->
                    Log.e("onAmountChangedEx", "$err")
                }
            )
            compositeDisposable.add(valueCalcDisposable)

        }
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        compositeDisposable.clear()
        contentView?.compositeDisposable?.clear()
        conversionViewModel?.clearCompositeDisposable()

    }

    override fun onHistoryClick() {
        router.pushController(
            RouterTransaction.with(HistoryController())
                .popChangeHandler(HorizontalChangeHandler())
                .pushChangeHandler(HorizontalChangeHandler())
        )
    }

    override fun onAmountChanged(fromMoney: Money, toCurrency: CurrencyUnit) {
        conversionViewModel?.calculateExchangeValue(fromMoney, toCurrency)
    }

    override fun onExchangeSubmit(fromMoney: Money, toCurrency: CurrencyUnit) {
        conversionViewModel?.processExchange(fromMoney, toCurrency)
    }
}

