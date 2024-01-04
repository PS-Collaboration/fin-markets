package com.binarybricks.coinbit.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.features.coinsearch.CoinDiscoveryFragment
import com.binarybricks.coinbit.features.dashboard.CoinDashboardFragment
import com.binarybricks.coinbit.features.settings.SettingsFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private var mInterstitialAd: InterstitialAd? = null;

    companion object {
        @JvmStatic
        fun buildLaunchIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }

        const val FRAGMENT_HOME = "FRAGMENT_HOME"
        const val FRAGMENT_OTHER = "FRAGMENT_OTHER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        switchToDashboard(savedInstanceState)

        // if fragment exist reuse it
        // if not then add it

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.actionHome -> {
                    showInterstitialAd()
                    switchToDashboard(savedInstanceState)
                }

                R.id.actionSearch -> {
                    showInterstitialAd()
                    switchToSearch(savedInstanceState)
                }

                R.id.actionSettings -> {
                    showInterstitialAd()
                    switchToSettings(savedInstanceState)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                finish()
            } else if (!supportFragmentManager.fragments.isNullOrEmpty()) {
                when (supportFragmentManager.fragments[0]) {
                    is CoinDashboardFragment -> bottomNavigation.menu.getItem(0).isChecked = true
                    is CoinDiscoveryFragment -> bottomNavigation.menu.getItem(1).isChecked = true
                    is SettingsFragment -> bottomNavigation.menu.getItem(2).isChecked = true
                }
            }
        }

        FirebaseCrashlytics.getInstance().log("HomeScreen")
    }

    private fun switchToDashboard(savedInstanceState: Bundle?) {


        print("Dashboard")
        val coinDashboardFragment = supportFragmentManager.findFragmentByTag(CoinDashboardFragment.TAG)
            ?: CoinDashboardFragment()

        // if we switch to home clear everything
        supportFragmentManager.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLayout, coinDashboardFragment, CoinDashboardFragment.TAG)
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .addToBackStack(FRAGMENT_HOME)
            .commit()
    }

    private fun switchToSearch(savedInstanceState: Bundle?) {

        print("Coin Search")
        val coinDiscoveryFragment = supportFragmentManager.findFragmentByTag(CoinDiscoveryFragment.TAG)
            ?: CoinDiscoveryFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLayout, coinDiscoveryFragment, CoinDiscoveryFragment.TAG)
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .addToBackStack(FRAGMENT_OTHER)
            .commit()
    }

    private fun switchToSettings(savedInstanceState: Bundle?) {

        val settingsFragment = supportFragmentManager.findFragmentByTag(SettingsFragment.TAG)
            ?: SettingsFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLayout, settingsFragment, SettingsFragment.TAG)
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .addToBackStack(FRAGMENT_OTHER)
            .commit()
    }

    private fun showInterstitialAd(){
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-2462617926038936/7791224217", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("MainActivity", adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("MainActivity", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }

    }
}
