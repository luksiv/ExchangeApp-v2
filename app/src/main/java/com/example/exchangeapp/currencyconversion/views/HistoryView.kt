package com.example.exchangeapp.currencyconversion.views

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchangeapp.R
import com.example.exchangeapp.currencyconversion.adapters.ExchangeHistoryAdapter
import com.example.exchangeapp.currencyconversion.entities.Exchange
import io.realm.RealmResults
import kotlinx.android.synthetic.main.view_history.view.*

class HistoryView(context: Context) : FrameLayout(context, null) {

    private lateinit var historyAdapter: ExchangeHistoryAdapter

    init {
        View.inflate(context, R.layout.view_history, this)
    }

    fun setupAdapter(history: RealmResults<Exchange>) {
        historyAdapter = ExchangeHistoryAdapter(history)
        exchangeHistoryList.let {
            it.adapter = historyAdapter
            it.layoutManager = LinearLayoutManager(this.context)
        }
    }
}