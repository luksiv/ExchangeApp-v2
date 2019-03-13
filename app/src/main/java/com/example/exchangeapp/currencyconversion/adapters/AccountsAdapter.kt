package com.example.exchangeapp.currencyconversion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangeapp.R
import com.example.exchangeapp.currencyconversion.entities.Account
import io.realm.RealmList
import kotlinx.android.synthetic.main.account_item.view.*

class AccountsAdapter(var accounts: RealmList<Account>) : RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = accounts.size

    fun updateAccounts(accounts: RealmList<Account>){
        this.accounts = accounts
        this.notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(position: Int) {
            val account = accounts[position]
            val appliedFees = account?.getAppliedFeesSum()
            itemView.let {
                it.accountName.text = account.name
                it.accountCurrency.text = account.getBalance()?.currencyUnit?.symbol
                it.accountBalance.text = account.getBalance()?.amount.toString()
                it.feeCurrency.text = account.getBalance()?.currencyUnit?.symbol
                it.feeAmount.text = appliedFees?.amount.toString()
            }
        }
    }
}