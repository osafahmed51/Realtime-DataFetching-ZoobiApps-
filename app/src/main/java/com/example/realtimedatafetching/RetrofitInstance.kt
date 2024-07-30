package com.example.realtimedatafetching

import com.example.realtimedatafetching.Interfaces.ExchangeRateApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://v6.exchangerate-api.com/v6/1c3b2aa0c488d7baf0039db6/"

    val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }
}