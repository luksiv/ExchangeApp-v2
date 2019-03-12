package com.example.exchangeapp.currencyconversion.views

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchangeapp.R
import com.example.exchangeapp.currencyconversion.adapters.AccountsAdapter
import com.jakewharton.rxbinding.widget.RxAdapterView
import com.jakewharton.rxbinding.widget.RxTextView
import com.paysera.currencyconverter.currencyconversion.entities.Account
import io.realm.RealmResults
import kotlinx.android.synthetic.main.view_home.view.*
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal

interface HomeViewDelegate {
    fun onAmountChanged(fromMoney: Money, toCurrency: CurrencyUnit)
    fun onExchangeSubmit(fromMoney: Money, toCurrency: CurrencyUnit)
    fun onHistoryClick()
    fun getAccountsList(): RealmResults<Account>
}

class HomeView(context: Context) : FrameLayout(context, null) {

    var homeViewDelegate: HomeViewDelegate? = null
    private lateinit var accountsAdapter: AccountsAdapter


    init {
        View.inflate(context, R.layout.view_home, this)

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

        // region Rx AdapterViews and TextViews
        RxAdapterView.itemSelections(toCurrency)
            .subscribe(
                { changed ->
                    Log.v("RxUpdate", "calling updateToValue to_curr")
                    updateToValue()
                },
                { err ->
                    Log.e("RxSpinner", "$err")
                }
            )
        RxAdapterView.itemSelections(fromCurrency)
            .subscribe(
                { changed ->
                    Log.v("RxUpdate", "calling updateToValue from_curr")
                    updateToValue()
                },
                { err ->
                    Log.e("RxSpinner", "$err")
                }
            )

        RxTextView.textChanges(fromAmount)
            .subscribe(
                { fromText ->
                    Log.v("RxUpdate", "calling updateToValue et_fromAmount")
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
            updateAccountsList()
            Toast.makeText(context, "Conversion successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Conversion unsuccessful ($reason)", Toast.LENGTH_SHORT).show()
        }
    }

    fun configureViews(currencies: List<String>, accountsRealmResults: RealmResults<Account>) {
        val spinnerAdapter =
            ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, currencies)
        fromCurrency.adapter = spinnerAdapter
        toCurrency.adapter = spinnerAdapter

        accountsAdapter = AccountsAdapter(accountsRealmResults)
        accountsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        accountsList.adapter = accountsAdapter
    }

    fun updateExchangeValue(toMoney: Money) {
        toAmount.setText(toMoney.amount.toPlainString())
    }
    //endregion

    // region Private methods
    private fun getFromMoney(): Money = Money.of(
        CurrencyUnit.of(fromCurrency.selectedItem.toString()),
        BigDecimal(fromAmount.text.toString())
    )

    private fun updateToValue() {
        Log.v("RxUpdate", "updateToValue called")
        if (fromAmount.text.isNotEmpty()) {
            if (fromCurrency.selectedItem != toCurrency.selectedItem) {
                homeViewDelegate?.onAmountChanged(getFromMoney(), getToCurrency())
            } else {
                toAmount.text = fromAmount.text
            }
        }
    }

    private fun getToCurrency(): CurrencyUnit = CurrencyUnit.of(toCurrency.selectedItem.toString())

    private fun updateAccountsList() {
        homeViewDelegate?.getAccountsList()?.let {
            accountsAdapter.accounts = it
            accountsAdapter.notifyDataSetChanged()
        }
    }
    // endregion
}