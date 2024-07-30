import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.example.realtimedatafetching.R
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView

class AdLoaderHelper(private val activity: Activity) {

    private var adLoader: AdLoader? = null
    private var isLoading: Boolean = false

    fun initializeAdLoader() {
        adLoader = AdLoader.Builder(activity, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                if (!isLoading) {
                    isLoading = false
                }

                if (activity.isDestroyed) {
                    ad.destroy()
                }

                val style = NativeTemplateStyle.Builder()
                    .withMainBackgroundColor(ColorDrawable(Color.WHITE))
                    .build()

                val template: TemplateView = activity.findViewById(R.id.my_template)

                template.setStyles(style)
                template.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    if (!isLoading) {
                        Log.d("AdLoader", "Ad successfully loaded.")
                    }
                    isLoading = false // Update the loading state
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdLoader", "Ad failed to load: ${adError.message}")
                    isLoading = false // Update the loading state
                }

                override fun onAdOpened() {
                    // Handle the ad being opened
                }

                override fun onAdClosed() {
                    // Handle the ad being closed
                }

                override fun onAdClicked() {
                    // Handle the ad being clicked
                }

                override fun onAdImpression() {
                    // Handle the ad impression being recorded
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()
    }

    fun loadAd() {
        isLoading = true
        adLoader?.loadAd(AdRequest.Builder().build())
    }
}
