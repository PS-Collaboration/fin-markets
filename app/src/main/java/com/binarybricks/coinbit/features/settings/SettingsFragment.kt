package com.binarybricks.coinbit.features.settings

import SettingsContract
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.binarybricks.coinbit.BuildConfig
import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.LoginActivity
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mynameismidori.currencypicker.CurrencyPicker
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import timber.log.Timber

class SettingsFragment : Fragment(), SettingsContract.View {
    private lateinit var auth: FirebaseAuth

    companion object {
        const val TAG = "SettingsFragment"
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(CoinBitApplication.database)
    }

    private val settingsPresenter: SettingsPresenter by lazy {
        SettingsPresenter(coinRepo)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val inflate = inflater.inflate(R.layout.fragment_settings, container, false)

        val toolbar = inflate.toolbar
        toolbar?.title = getString(R.string.settings)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        settingsPresenter.attachView(this)

        lifecycle.addObserver(settingsPresenter)

        initializeUI(inflate)

        FirebaseCrashlytics.getInstance().log("SettingsFragment")

        return inflate
    }

    private fun initializeUI(inflatedView: View) {

        val currency = PreferenceManager.getPreference(requireContext(), PreferenceManager.DEFAULT_CURRENCY, defaultCurrency)

        inflatedView.tvCurrencyValue.text = currency

        inflatedView.clCurrency.setOnClickListener {
            openCurrencyPicker()
        }

        inflatedView.clCurrencyList.setOnClickListener {
            settingsPresenter.refreshCoinList(currency)
            inflatedView.ivCurrencyList.visibility = View.INVISIBLE
            inflatedView.pbLoadingCurrencyList.visibility = View.VISIBLE
        }

        inflatedView.clExchangeList.setOnClickListener {
            settingsPresenter.refreshExchangeList()
            inflatedView.ivExchangeList.visibility = View.INVISIBLE
            inflatedView.pbLoadingExchangeList.visibility = View.VISIBLE
        }


        inflatedView.tvAppVersionValue.text = BuildConfig.VERSION_NAME

        inflatedView.clProfile.setOnClickListener(View.OnClickListener {
            // Invalidate the user's credentials.
            auth = FirebaseAuth.getInstance()
            auth.signOut()

            activity?.finishAffinity()

            // Then, clear the activity stack and start from the login activity again.
            val loginIntent = Intent(context, LoginActivity::class.java)
            startActivity(loginIntent)
        })
    }

    private fun openCurrencyPicker() {
        val picker = CurrencyPicker.newInstance(getString(R.string.select_currency)) // dialog title

        picker.setListener { name, code, _, _ ->
            Timber.d("Currency code selected $name,$code")
            PreferenceManager.setPreference(requireContext(), PreferenceManager.DEFAULT_CURRENCY, code)

            picker.dismiss() // Show currency that is picked.

            val currency = PreferenceManager.getPreference(requireContext(), PreferenceManager.DEFAULT_CURRENCY, defaultCurrency)

            tvCurrencyValue.text = currency

            // get list of all coins
            settingsPresenter.refreshCoinList(currency)
        }

        fragmentManager?.let {
            picker.show(it, "CURRENCY_PICKER")
        }
    }

    override fun onCoinListRefreshed() {
        ivCurrencyList.visibility = View.VISIBLE
        pbLoadingCurrencyList.visibility = View.GONE
    }

    override fun onExchangeListRefreshed() {
        ivExchangeList.visibility = View.VISIBLE
        pbLoadingExchangeList.visibility = View.GONE
    }

    override fun onNetworkError(errorMessage: String) {
        // TODO: Figure out what to do about this error message.
        // Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        (activity as AppCompatActivity).setSupportActionBar(null)
        super.onDestroyView()
    }
}
