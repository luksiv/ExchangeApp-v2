package com.example.exchangeapp.currencyconversion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangeapp.R
import com.paysera.currencyconverter.currencyconversion.entities.Exchange
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.history_item.view.*
import org.joda.money.CurrencyUnit

class ExchangeHistoryAdapter(val exchangeHistory: RealmResults<Exchange>?) :
    RealmRecyclerViewAdapter<Exchange, ExchangeHistoryAdapter.ViewHolder>(exchangeHistory, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.clear()
        holder.onBind(position)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun clear() {
            itemView.let {
                it.tv_transactionId.text = ""
                it.fromCurrency.text = ""
                it.fromAmount.text = ""
                it.toCurrency.text = ""
                it.toAmount.text = ""
                it.feeCurrency.text = ""
                it.feeAmount.text = ""
            }
        }

        fun onBind(position: Int) {
            val history = exchangeHistory?.get(position)
            itemView.let {
                it.tv_transactionId.text = history?.transactionId.toString()
                it.fromCurrency.text = CurrencyUnit.of(history?.currencyFrom).symbol
                it.fromAmount.text = history?.amount
                it.toCurrency.text = CurrencyUnit.of(history?.currencyTo).symbol
                it.toAmount.text = history?.amountTo
                it.feeCurrency.text = CurrencyUnit.of(history?.currencyFrom).symbol
                it.feeAmount.text = history?.feeAmount
            }
        }

    }
}