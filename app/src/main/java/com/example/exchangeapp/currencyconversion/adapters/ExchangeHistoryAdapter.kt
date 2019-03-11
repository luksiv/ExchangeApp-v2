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
                it.tv_exchange_currencyFrom.text = ""
                it.tv_exchange_amountFrom.text = ""
                it.tv_exchange_currencyTo.text = ""
                it.tv_exchange_amountTo.text = ""
                it.tv_exchange_currencyFee.text = ""
                it.tv_exchange_amountFee.text = ""
            }
        }

        fun onBind(position: Int) {
            val history = exchangeHistory?.get(position)
            itemView.let {
                it.tv_transactionId.text = history?.transactionId.toString()
                it.tv_exchange_currencyFrom.text = CurrencyUnit.of(history?.currencyFrom).symbol
                it.tv_exchange_amountFrom.text = history?.amount
                it.tv_exchange_currencyTo.text = CurrencyUnit.of(history?.currencyTo).symbol
                it.tv_exchange_amountTo.text = history?.amountTo
                it.tv_exchange_currencyFee.text = CurrencyUnit.of(history?.currencyFrom).symbol
                it.tv_exchange_amountFee.text = history?.feeAmount
            }
        }

    }
}