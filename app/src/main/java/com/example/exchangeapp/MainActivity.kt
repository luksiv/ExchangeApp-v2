package com.example.exchangeapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.example.exchangeapp.currencyconversion.controllers.HomeController
import com.paysera.currencyconverter.currencyconversion.SchedulerProvider
import com.paysera.currencyconverter.currencyconversion.api.CurrencyConversionApiClient
import com.example.exchangeapp.common.repositories.UserRepository
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mCurrencyConversionApiClient: CurrencyConversionApiClient
    @Inject
    lateinit var mCompositeDisposable: CompositeDisposable
    @Inject
    lateinit var mSchedulerProvider: SchedulerProvider
    @Inject
    lateinit var mUserRepository: UserRepository

    lateinit var mRouter: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidInjection.inject(this)

        mRouter = Conductor.attachRouter(this, conductorChangeHandler, savedInstanceState)
        if(!mRouter.hasRootController()){
            mRouter.setRoot(RouterTransaction.with(HomeController()))
        }
//        setOnClickListeners()
//        setupCurrencySpinners()

//        Realm.getDefaultInstance().executeTransaction{
//            it.delete(User::class.java)
//            it.delete(Account::class.java)
//        }
//
//        Log.v("Realm", "Calling setupUser")
//        mUserRepository.setupUser()
//        val user = mUserRepository.getUser()
//        Log.v("Realm","User #${user.userId}")
//        Log.v("Realm", "${user}")
//        for (account in user.accounts){
//            Log.v("Realm", "Account #${account.accountId}")
//            Log.v("Realm", "${account}")
//            Log.v("Realm", "${account.getBalance()}")
//        }
//        val accountsAdapter = AccountsAdapter(Realm.getDefaultInstance().where(Account::class.java).findAll())
//        rv_userAccounts.layoutManager = LinearLayoutManager(this)
//        rv_userAccounts.adapter = accountsAdapter
//        accountsAdapter.notifyDataSetChanged()


//        mRealm.executeTransaction {
//            it.deleteAll()
//        }
//
//        mRealm.executeTransactionAsync(Realm.Transaction {
//            val user = it.createObject(User::class.java, 0)
//            val account = it.createObject(Account::class.java, 0)
//            account.name = "Savings account"
//            account.currency = CurrencyUnit.EUR.code
//            account.balance = "10.21"
//            Log.v("EVP", "New account: $account")
//            user.accounts.add(account)
//            Log.v("EVP", "New user: $user")
//            Log.v("EVP", "User balance: ${user.accounts.get(0).getBalance()}")
//
//
//        })
//        testEVPApiService()
    }

//    private fun setOnClickListeners() {
//        btn_exchange.setOnClickListener {
//            Toast.makeText(it.context, "Not implemented yet", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun setupCurrencySpinners() {
////        val currencies = AppConstants.currencies.map{ currencyUnit ->  currencyUnit.currencyCode}.toList()
//        val spinnerAdapter = ArrayAdapter<String>(
//            this,
//            android.R.layout.simple_spinner_dropdown_item,
//            AppConstants.currencies.map{ currencyUnit ->  currencyUnit.currencyCode}.toList()
//        )
//        spn_fromCurrency?.adapter = spinnerAdapter
//        spn_toCurrency?.adapter = spinnerAdapter
//
//    }

    override fun onBackPressed() {
        if (!mRouter.handleBack()) {
            super.onBackPressed()
        }
    }

    fun testEVPApiService() {
        val testMoney = Money.of(CurrencyUnit.of("EUR"), BigDecimal("100"))
        val toCurrency = "USD"
        Log.v("MainActivity", "testMoney: ${testMoney}")

        mCompositeDisposable.add(
                mCurrencyConversionApiClient.calculate(testMoney, toCurrency)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result ->
                                    Log.v("EVPResponse", "result: ${result}")
                                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                                },
                                { err ->
                                    Log.e("EVPResponse", "Error occured: ${err.message}")
                                    Log.e("EVPResponse", "${err}")
                                }
                        )
        )
    }
}
