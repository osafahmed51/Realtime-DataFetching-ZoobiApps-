package com.example.realtimedatafetching

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.realtimedatafetching.DataClasses.ExchangeRateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class ExchangeRateWorkManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("ExchangeRatesWorker", "Work started")
        fetchExchangeRates()
        return Result.success()
    }

    private fun fetchExchangeRates() {
        RetrofitInstance.api.getLatestRates().enqueue(object : Callback<ExchangeRateResponse> {
            override fun onResponse(
                call: Call<ExchangeRateResponse>,
                response: Response<ExchangeRateResponse>
            ) {
                if (response.isSuccessful) {
                    val exchangeRateResponse = response.body()
                    exchangeRateResponse?.let {
                        Log.d("ExchangeRatesWorker", "Base Code: ${it.baseCode}")
                        it.conversionRates.forEach { (currency, rate) ->
                            Log.d("ExchangeRatesWorker", "$currency: $rate")
                        }
                        saveExchangeRates(it.conversionRates)
                    }
                } else {
                    Log.e("ExchangeRatesWorker", "Failed to get rates: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ExchangeRateResponse>, t: Throwable) {
                Log.e("ExchangeRatesWorker", "Error fetching exchange rates: ${t.message}")
            }
        })
    }

    private fun saveExchangeRates(conversionRates: Map<String, Double>) {
        val sharedPreferences = applicationContext.getSharedPreferences("exchange_rates", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        conversionRates.forEach { (currency, rate) ->
            editor.putFloat(currency, rate.toFloat())
        }
        editor.apply()
        Log.d("ExchangeRatesWorker", "Exchange rates saved to SharedPreferences")
    }
}

fun schedulePeriodicWork(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<ExchangeRateWorkManager>(15, TimeUnit.MINUTES)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "FetchExchangeRates",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}
