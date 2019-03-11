package com.example.exchangeapp.currencyconversion.views

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.example.exchangeapp.R

class HistoryView(context: Context) : FrameLayout(context, null) {

    init {
        View.inflate(context, R.layout.view_history, this)
    }
}