package com.example.exchangeapp

import android.app.Activity
import android.app.Application
import com.paysera.currencyconverter.common.dagger.AppComponent
import com.paysera.currencyconverter.common.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Inject

class CurrencyConversionApplication
    :
        Application(),
        HasActivityInjector {
    @Inject
    internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    lateinit var mAppComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        mAppComponent = DaggerAppComponent.builder()
                .application(this)
                .build()

        mAppComponent.inject(this)

        Realm.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity> = activityDispatchingAndroidInjector
}