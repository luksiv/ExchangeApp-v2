package com.example.exchangeapp.currencyconversion.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import java.math.BigDecimal

interface EVPApiService {

    object Constants {
        val EVP_API_BASE = "http://api.evp.lt/currency/commercial/exchange/"
    }

    @GET("{amount}-{currencyFrom}/{currencyTo}/latest")
    fun getExchangeValue(
        @Path("amount") amount: BigDecimal,
        @Path("currencyFrom") currencyFrom: String,
        @Path("currencyTo") currencyTo: String
    ): Single<EVPApiResponse>
}