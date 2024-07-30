package com.example.realtimedatafetching

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.realtimedatafetching.DataClasses.ExchangeRateResponse
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConversionActivity : AppCompatActivity() {

    private lateinit var sourceCurrencySpinner: Spinner
    private lateinit var targetCurrencySpinner: Spinner
    private lateinit var amountEditText: EditText
    private lateinit var conversionResult: TextView

    private var conversionRates: Map<String, Double> = mapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversion)

        sourceCurrencySpinner = findViewById(R.id.source_currency_spinner)
        targetCurrencySpinner = findViewById(R.id.target_currency_spinner)
        amountEditText = findViewById(R.id.amount_edit_text)
        conversionResult = findViewById(R.id.conversion_result)


        fetchExchangeRates()

        amountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                convertCurrency()
            }

            override fun afterTextChanged(s: Editable?) {}
        })




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
                        conversionRates = it.conversionRates
                        updateCurrencySpinners()
                    }
                }
            }

            override fun onFailure(call: Call<ExchangeRateResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun updateCurrencySpinners() {
        val currencies = conversionRates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sourceCurrencySpinner.adapter = adapter
        targetCurrencySpinner.adapter = adapter
    }

    private fun convertCurrency() {
        val sourceCurrency = sourceCurrencySpinner.selectedItem.toString()
        val targetCurrency = targetCurrencySpinner.selectedItem.toString()
        val amountText = amountEditText.text.toString()

        if (amountText.isNotEmpty()) {
            val amount = amountText.toDoubleOrNull()
            val sourceRate = conversionRates[sourceCurrency]
            val targetRate = conversionRates[targetCurrency]

            if (sourceRate != null && targetRate != null) {
                val convertedAmount = (amount ?: 0.0) * (targetRate / sourceRate)
                conversionResult.text = String.format("%.2f %s", convertedAmount, targetCurrency)
            }
        } else {
            conversionResult.text = "Converted amount will appear here"
        }
    }








}