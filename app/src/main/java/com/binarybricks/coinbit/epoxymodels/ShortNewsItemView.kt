package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ContentLoadingProgressBar
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.network.models.CryptoCompareNews
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.android.synthetic.main.dashboard_news_module.view.*


@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ShortNewsItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val pbLoading: ContentLoadingProgressBar
//    private val tvNewsTitle: TextView
    private val clNewsArticleContainer: View
    private val nativeTemplate: TemplateView

    init {


        View.inflate(context, R.layout.dashboard_news_module, this)

        nativeTemplate= findViewById(R.id.ivAnnouncement);
        pbLoading = findViewById(R.id.pbLoading)
//        tvNewsTitle = findViewById(R.id.tvNewsTitle)

        clNewsArticleContainer = findViewById(R.id.clNewsArticleContainer)

        val adLoader = AdLoader.Builder(context, "ca-app-pub-2462617926038936/7262967958")
                .forNativeAd { ad : NativeAd ->
                    nativeTemplate.setNativeAd(ad)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build()

        adLoader.loadAd(AdRequest.Builder().build())




    }

    @TextProp
    fun setNewsDate(news: CharSequence) {
        pbLoading.visibility = View.GONE
//        tvNewsTitle.text = news
    }

    @CallbackProp
    fun setItemClickListener(listener: OnClickListener?) {
        clNewsArticleContainer.setOnClickListener(listener)
    }

    data class ShortNewsModuleData(val news: CryptoCompareNews) : ModuleItem
}
