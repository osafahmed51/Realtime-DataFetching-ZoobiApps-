package com.example.realtimedatafetching.Interfaces

import com.example.realtimedatafetching.DataClasses.ExchangeRateResponse
import retrofit2.Call
import retrofit2.http.GET

interface ExchangeRateApi {
    @GET("latest/USD")
    fun getLatestRates(): Call<ExchangeRateResponse>
}