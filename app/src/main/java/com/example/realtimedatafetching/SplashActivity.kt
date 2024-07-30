// SplashActivity.kt
package com.example.realtimedatafetching

import AppOpenManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.dummyproject.MyApp

class SplashActivity : AppCompatActivity() {

    private lateinit var appOpenManager: AppOpenManager

    private lateinit var nextbtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        nextbtn=findViewById(R.id.nextbn)

        appOpenManager = (application as MyApp).appOpenManager



    nextbtn.setOnClickListener {
        appOpenManager.setAdDismissListener {
            navigateToMainScreen()
        }

        if (appOpenManager.isAdAvailable()) {
            appOpenManager.showAdIfAvailable()
        } else {
            navigateToMainScreen()
        }
    }

    }


    private fun navigateToMainScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, CurrentRates::class.java))
            finish()
        }, 1000)
    }
}
