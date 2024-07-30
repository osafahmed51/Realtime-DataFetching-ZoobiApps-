package com.example.realtimedatafetching.DataClasses

import com.google.gson.annotations.SerializedName

data class ExchangeRateResponse(
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("conversion_rates") val conversionRates: Map<String, Double>
)
