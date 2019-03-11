package com.example.exchangeapp.currencyconversion.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangeapp.R
import com.paysera.currencyconverter.currencyconversion.entities.Account
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.account_item.view.*

class AccountsAdapter(var accounts: RealmResults<Account>?) : RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.clear()
        holder.onBind(position)
    }

    override fun getItemCount(): Int = accounts?.size ?: 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun clear() {
            itemView.let{
                it.tv_accountName.text = ""
                it.tv_currency.text = ""
                it.tv_amount.text = ""
                it.tv_currencyFee.text = ""
                it.tv_amountFee.text = ""
            }
        }

        fun onBind(position: Int) {
            val account = accounts?.get(position)
            val appliedFees = account?.getAppliedFeesSum()
            itemView.let{
                it.tv_accountName.text = account?.name ?: "N/A"
                it.tv_currency.text = account?.getBalance()?.currencyUnit?.symbol
                it.tv_amount.text = account?.getBalance()?.amount.toString()
                it.tv_currencyFee.text = account?.getBalance()?.currencyUnit?.symbol
                it.tv_amountFee.text = appliedFees?.amount.toString()
            }
        }

    }
}