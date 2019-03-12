package com.example.exchangeapp.currencyconversion.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.exchangeapp.CurrencyConversionApplication
import com.example.exchangeapp.common.controllers.BaseController
import com.example.exchangeapp.common.repositories.ExchangeHistoryRepository
import com.example.exchangeapp.currencyconversion.views.HistoryView
import io.realm.Realm

class HistoryController : BaseController() {

    private var contentView: HistoryView? = null

    override fun onCreateControllerView(inflater: LayoutInflater, container: ViewGroup): View {
        return HistoryView(inflater.context).also { view ->
            contentView = view
            ExchangeHistoryRepository(Realm.getDefaultInstance()).getAllHistory().let {
                view.setupAdapter(it)
            }
        }
    }

    override fun inject() {
        (applicationContext as CurrencyConversionApplication).mAppComponent.inject(this)
    }
}