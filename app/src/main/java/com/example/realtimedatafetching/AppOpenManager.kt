import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.realtimedatafetching.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date


class AppOpenManager(private val application: Application) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var isShowingAd = false
    private var loadTime: Long = 0

    private var adDismissListener: (() -> Unit)? = null

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun setAdDismissListener(listener: () -> Unit) {
        adDismissListener = listener
    }

    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val numHours = 4
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    fun isAdAvailable(): Boolean {
        val isLoaded = appOpenAd != null
        val isRecent = wasLoadTimeLessThanNHoursAgo()
        Log.d("AppOpenManager", "Is ad loaded: $isLoaded, Is ad recent: $isRecent")
        return isLoaded && isRecent
    }

     fun fetchAd() {
        if (isAdAvailable() || currentActivity == null) {
            Log.d("AppOpenManager", "Ad not available or currentActivity is null")
            return
        }

        Log.d("AppOpenManager", "Fetching ad...")

        val loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                Log.e("AppOpenManager", "Failed to load ad: ${adError.message}")
                adDismissListener?.invoke()
            }

            override fun onAdLoaded(ad: AppOpenAd) {
                super.onAdLoaded(ad)
                appOpenAd = ad
                loadTime = Date().time
                Log.d("AppOpenManager", "Ad loaded successfully")
            }
        }

        val request = AdRequest.Builder().build()
        currentActivity?.let {
            AppOpenAd.load(
                application,
                it.getString(R.string.appOpenAdsss), // Ensure this ID is correct
                request,
                loadCallback
            )
        } ?: Log.e("AppOpenManager", "CurrentActivity is null while loading ad")
    }

     fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            Log.d("AppOpenManager", "Showing ad")
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Log.d("AppOpenManager", "Ad dismissed")
                    appOpenAd = null
                    isShowingAd = false
                    adDismissListener?.invoke()
                    fetchAd()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("AppOpenManager", "Ad showed")
                    isShowingAd = true
                }
            }
            appOpenAd?.show(currentActivity!!)
        } else {
            Log.d("AppOpenManager", "No ad available or ad already showing")
            fetchAd()
            adDismissListener?.invoke()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        Log.d("AppOpenManager", "Activity started: ${activity.localClassName}")
        if (isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d("AppOpenManager", "Activity resumed: ${activity.localClassName}")
        if (!isShowingAd) {
            currentActivity = activity
        }
    }


    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        showAdIfAvailable()
    }
}
