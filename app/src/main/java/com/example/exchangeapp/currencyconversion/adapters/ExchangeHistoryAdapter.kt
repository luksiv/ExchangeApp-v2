package com.example.exchangeapp.currencyconversion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangeapp.R
import com.example.exchangeapp.currencyconversion.entities.Exchange
import io.realm.RealmResults
import kotlinx.android.synthetic.main.history_item.view.*
import org.joda.money.CurrencyUnit

class ExchangeHistoryAdapter(val exchangeHistory: RealmResults<Exchange>) :
    RecyclerView.Adapter<ExchangeHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = exchangeHistory.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(position: Int) {
            val history = exchangeHistory[position]
            itemView.let {
                it.tv_transactionId.text = history.transactionId.toString()
                it.fromCurrency.text = CurrencyUnit.of(history.currencyFrom).symbol
                it.fromAmount.text = history.amount
                it.toCurrency.text = CurrencyUnit.of(history.currencyTo).symbol
                it.toAmount.text = history.amountTo
                it.feeCurrency.text = CurrencyUnit.of(history.currencyFrom).symbol
                it.feeAmount.text = history.feeAmount
            }
        }
    }
}