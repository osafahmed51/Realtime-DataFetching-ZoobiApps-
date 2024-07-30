package com.example.dummyproject

import AppOpenManager
import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds

class MyApp : Application() {


    lateinit var appOpenManager: AppOpenManager

    override fun onCreate() {
        super.onCreate()

        try {
            MobileAds.initialize(this) {
                Log.d("mobilead", "onCreate: ", )
            }
            appOpenManager=AppOpenManager(this)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }


    }

}