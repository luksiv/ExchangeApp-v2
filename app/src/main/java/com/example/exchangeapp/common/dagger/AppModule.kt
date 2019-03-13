package com.example.exchangeapp.common.dagger

import android.app.Application
import android.content.Context
import com.example.exchangeapp.common.repositories.UserRepository
import com.example.exchangeapp.common.services.BalanceManager
import com.example.exchangeapp.currencyconversion.helpers.CurrencyConversionHelper
import com.example.exchangeapp.common.SchedulerProvider
import com.example.exchangeapp.currencyconversion.api.CurrencyConversionApiClient
import com.example.exchangeapp.currencyconversion.api.EVPApiService
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    internal fun provideEVPApiService(): EVPApiService = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(EVPApiService.Constants.EVP_API_BASE)
        .build()
        .create(EVPApiService::class.java)

    @Provides
    @Singleton
    internal fun provideCurrencyConversionApiClient(evpApiService: EVPApiService): CurrencyConversionApiClient =
        CurrencyConversionApiClient(evpApiService)

    @Provides
    @Singleton
    internal fun provideBalanceManager(): BalanceManager = BalanceManager()

    @Provides
    @Singleton
    internal fun provideConversionManager(
        balanceManager: BalanceManager,
        currencyConversionApiClient: CurrencyConversionApiClient
    ): CurrencyConversionHelper =
        CurrencyConversionHelper(
            balanceManager,
            currencyConversionApiClient
        )

    @Provides
    @Singleton
    internal fun provideUserRepository(): UserRepository =
        UserRepository(Realm.getDefaultInstance())

    @Provides
    @Singleton
    internal fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @Singleton
    internal fun provideSchedulerProvider(): SchedulerProvider = SchedulerProvider()
}