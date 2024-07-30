package com.example.realtimedatafetching

import AdLoaderHelper
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

class CurrentRates : AppCompatActivity() {

    private lateinit var ratesListView: ListView
    private lateinit var goToConversionButton: Button
    private var conversionRates: Map<String, Double> = mapOf()
    private var nativeAd: NativeAd? = null
    private lateinit var adLoaderHelper: AdLoaderHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_rates)

        ratesListView = findViewById(R.id.ratesListView)
        goToConversionButton = findViewById(R.id.go_to_conversion_button)

        goToConversionButton.setOnClickListener {
            val intent = Intent(this, ConversionActivity::class.java)
            startActivity(intent)
        }

        adLoaderHelper = AdLoaderHelper(this)
        adLoaderHelper.initializeAdLoader()
        adLoaderHelper.loadAd()

        schedulePeriodicWork(this)
    }

    override fun onResume() {
        super.onResume()
        loadExchangeRates()
        adLoaderHelper.loadAd()
    }

    private fun loadExchangeRates() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("exchange_rates", MODE_PRIVATE)
        val conversionRates = mutableMapOf<String, Double>()
        sharedPreferences.all.forEach { (key, value) ->
            conversionRates[key] = (value as Float).toDouble()
        }
        this.conversionRates = conversionRates
        updateRatesList()
        Log.d("CurrentRates", "Exchange rates loaded from SharedPreferences")
    }

    private fun updateRatesList() {
        val ratesList = conversionRates.entries
            .sortedBy { it.key }
            .map { "${it.key}: ${it.value}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ratesList)
        ratesListView.adapter = adapter
    }




    override fun onDestroy() {
        nativeAd?.destroy()
        super.onDestroy()
    }

}
