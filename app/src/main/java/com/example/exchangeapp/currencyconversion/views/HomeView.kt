package com.example.exchangeapp.currencyconversion.views

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchangeapp.R
import com.example.exchangeapp.common.AppConstants
import com.example.exchangeapp.currencyconversion.adapters.AccountsAdapter
import com.example.exchangeapp.currencyconversion.entities.Account
import com.jakewharton.rxbinding.widget.*
import io.realm.RealmList
import io.realm.RealmResults
import kotlinx.android.synthetic.main.view_home.view.*
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

interface HomeViewDelegate {
    fun onAmountChanged(fromMoney: Money, toCurrency: CurrencyUnit)
    fun onExchangeSubmit(fromMoney: Money, toCurrency: CurrencyUnit)
    fun onHistoryClick()
}

class HomeView(context: Context) : FrameLayout(context, null) {

    var homeViewDelegate: HomeViewDelegate? = null
    private lateinit var accountsAdapter: AccountsAdapter
    private var fromSpinnerAdapter: ArrayAdapter<String>? = null
    private var toSpinnerAdapter: ArrayAdapter<String>? = null

    private var fromCurrencyUnit: String = "EUR"
    private var toCurrencyUnit: String = "USD"

    init {
        View.inflate(context, R.layout.view_home, this)
        setCurrencySpinners()
        // region Setting onClickListeners
        btnExchange.setOnClickListener {
            Log.v(
                "Exchange",
                "Request to convert ${fromAmount.text} ${fromCurrency.selectedItem} to ${toCurrency.selectedItem}"
            )
            if (fromAmount.text.isNotEmpty()) {
                if (fromCurrency.selectedItem != toCurrency.selectedItem) {
                    val fromMoney = Money.of(
                        CurrencyUnit.of(fromCurrency.selectedItem.toString()),
                        BigDecimal(fromAmount.text.toString())
                    )
                    val toCurrency = CurrencyUnit.of(toCurrency.selectedItem.toString())
                    homeViewDelegate?.onExchangeSubmit(fromMoney, toCurrency)
                } else {
                    Toast.makeText(context, "Currencies must be different!", Toast.LENGTH_LONG).show()
                    Log.e("Exchange", "Request denied! Currencies must be different!")
                }
            } else {
                Toast.makeText(context, "Amount can't be empty!", Toast.LENGTH_LONG).show()
                Log.e("Exchange", "Request denied! Amount can't be empty!")
            }
        }

        btnToHistory.setOnClickListener {
            homeViewDelegate?.onHistoryClick()
        }
        // endregion

        // region RxTextView

        RxTextView.textChanges(fromAmount)
            .subscribe(
                {
                    updateToValue()
                },
                { err ->
                    Log.e("RxTextView", "$err")
                }
            )
        // endregion

    }

    //region Public methods
    fun showConversionResult(succeeded: Boolean, reason: String = "") {
        if (succeeded) {
            Toast.makeText(context, "Conversion successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Conversion unsuccessful ($reason)", Toast.LENGTH_SHORT).show()
        }
    }

    fun setupAccountAdapter(accounts: RealmList<Account>) {
        accountsAdapter = AccountsAdapter(accounts)
        accountsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        accountsList.adapter = accountsAdapter
    }

    fun setCurrencySpinners() {
        val fromList = getFilteredCurrencyList { it != toCurrencyUnit }
        val toList = getFilteredCurrencyList { it != fromCurrencyUnit }
        fromSpinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, fromList)
        toSpinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, toList)
        fromCurrency.let { spinner ->
            spinner.adapter = fromSpinnerAdapter
            spinner.selectionEvents()
                .subscribe(
                    {
                        fromCurrencyUnit = spinner.selectedItem.toString()
                        updateCurrencySpinners()
                        updateToValue()
                    },
                    { err ->
                        Log.e("fromSpinner", "ERROR: $err")
                    }
                )
        }
        toCurrency.let { spinner ->
            spinner.adapter = toSpinnerAdapter
            spinner.selectionEvents()
                .subscribe(
                    {
                        toCurrencyUnit = spinner.selectedItem.toString()
                        updateCurrencySpinners()
                        updateToValue()
                    },
                    { err ->
                        Log.e("toSpinner", "ERROR: $err")
                    }
                )
        }
    }

    fun updateExchangeValue(toMoney: Money) {
        toAmount.setText(toMoney.amount.toPlainString())
    }

    fun updateAccountsList(accounts: RealmList<Account>) {
        accountsAdapter.updateAccounts(accounts)
    }

//endregion

    // region Private methods
    private fun getFromMoney(): Money = Money.of(
        CurrencyUnit.of(fromCurrency.selectedItem.toString()),
        BigDecimal(fromAmount.text.toString())
    )

    private fun updateCurrencySpinners() {
        fromSpinnerAdapter?.let {
            val fromList = getFilteredCurrencyList { it != toCurrencyUnit }
            it.clear()
            it.addAll(fromList)
            fromCurrency.setSelection(fromList.indexOf(fromCurrencyUnit))
            it.notifyDataSetChanged()
        }
        toSpinnerAdapter?.let {
            val toList = getFilteredCurrencyList { it != fromCurrencyUnit }
            it.clear()
            it.addAll(toList)
            toCurrency.setSelection(toList.indexOf(toCurrencyUnit))
            it.notifyDataSetChanged()
        }
    }

    private fun updateToValue() {
        if (fromAmount.text.isNotEmpty()) {
            homeViewDelegate?.onAmountChanged(getFromMoney(), getToCurrency())
        }
    }

    private fun getToCurrency(): CurrencyUnit = CurrencyUnit.of(toCurrency.selectedItem.toString())

    private fun getFilteredCurrencyList(predicate: (String) -> Boolean): List<String> =
        AppConstants.currencies.map { it.currencyCode }.filter { predicate(it) }
    // endregion
}