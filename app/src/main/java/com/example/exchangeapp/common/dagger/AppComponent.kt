package com.example.exchangeapp.common.dagger

import android.app.Application
import com.example.exchangeapp.CurrencyConversionApplication
import com.example.exchangeapp.currencyconversion.controllers.HistoryController
import com.example.exchangeapp.currencyconversion.controllers.HomeController
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityBuilder::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: CurrencyConversionApplication)
    fun inject(homeController: HomeController)
    fun inject(historyController: HistoryController)
}