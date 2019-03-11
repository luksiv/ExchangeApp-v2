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
        btn_exchange.setOnClickListener {
            Log.v(
                "Exchange",
                "Request to convert ${et_fromAmount.text} ${spn_fromCurrency.selectedItem} to ${spn_toCurrency.selectedItem}"
            )
            if (et_fromAmount.text.isNotEmpty()) {
                if (spn_fromCurrency.selectedItem != spn_toCurrency.selectedItem) {
                    val fromMoney = Money.of(
                        CurrencyUnit.of(spn_fromCurrency.selectedItem.toString()),
                        BigDecimal(et_fromAmount.text.toString())
                    )
                    val toCurrency = CurrencyUnit.of(spn_toCurrency.selectedItem.toString())
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

        btn_toHistory.setOnClickListener {
            homeViewDelegate?.onHistoryClick()
        }
        // endregion

        // region Rx AdapterViews and TextViews
        RxAdapterView.itemSelections(spn_toCurrency)
            .subscribe(
                { changed ->
                    Log.v("RxUpdate", "calling updateToValue to_curr")
                    updateToValue()
                },
                { err ->
                    Log.e("RxSpinner", "$err")
                }
            )
        RxAdapterView.itemSelections(spn_fromCurrency)
            .subscribe(
                { changed ->
                    Log.v("RxUpdate", "calling updateToValue from_curr")
                    updateToValue()
                },
                { err ->
                    Log.e("RxSpinner", "$err")
                }
            )

        RxTextView.textChanges(et_fromAmount)
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
        spn_fromCurrency.adapter = spinnerAdapter
        spn_toCurrency.adapter = spinnerAdapter

        accountsAdapter = AccountsAdapter(accountsRealmResults)
        rv_userAccounts.layoutManager = LinearLayoutManager(context)
        rv_userAccounts.adapter = accountsAdapter
    }

    fun updateExchangeValue(toMoney: Money) {
        et_toAmount.setText(toMoney.amount.toPlainString())
    }
    //endregion

    // region Private methods
    private fun getFromMoney(): Money = Money.of(
        CurrencyUnit.of(spn_fromCurrency.selectedItem.toString()),
        BigDecimal(et_fromAmount.text.toString())
    )

    private fun updateToValue() {
        Log.v("RxUpdate", "updateToValue called")
        if (et_fromAmount.text.isNotEmpty()) {
            if (spn_fromCurrency.selectedItem != spn_toCurrency.selectedItem) {
                homeViewDelegate?.onAmountChanged(getFromMoney(), getToCurrency())
            } else {
                et_toAmount.text = et_fromAmount.text
            }
        }
    }

    private fun getToCurrency(): CurrencyUnit = CurrencyUnit.of(spn_toCurrency.selectedItem.toString())

    private fun updateAccountsList() {
        accountsAdapter.let {
            it.accounts = homeViewDelegate?.getAccountsList()
            it.notifyDataSetChanged()
        }
    }
    // endregion
}