package com.example.exchangeapp.currencyconversion.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchangeapp.CurrencyConversionApplication
import com.example.exchangeapp.common.controllers.BaseController
import com.example.exchangeapp.currencyconversion.adapters.ExchangeHistoryAdapter
import com.example.exchangeapp.currencyconversion.views.HistoryView
import com.paysera.currencyconverter.currencyconversion.entities.Exchange
import io.realm.Realm
import kotlinx.android.synthetic.main.view_history.view.*

class HistoryController : BaseController() {

    override fun onCreateControllerView(inflater: LayoutInflater, container: ViewGroup): View  = HistoryView(inflater.context).also {
        setupHistoryAdapter(it)
    }
    override fun inject() {
        (applicationContext as CurrencyConversionApplication).mAppComponent.inject(this)
    }

    fun setupHistoryAdapter(view: View){
        val exchangeHistory = Realm.getDefaultInstance().where(Exchange::class.java).findAll()
        val exchangeHistoryAdapter = ExchangeHistoryAdapter(exchangeHistory)
        view.rv_exchangeHistory.adapter = exchangeHistoryAdapter
        view.rv_exchangeHistory.layoutManager = LinearLayoutManager(view.context)
        exchangeHistoryAdapter.notifyDataSetChanged()
    }
}