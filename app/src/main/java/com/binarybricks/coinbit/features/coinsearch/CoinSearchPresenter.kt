package com.binarybricks.coinbit.features.coinsearch

import CoinSearchContract
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.epoxymodels.CoinItemView
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.network.api.api
import com.binarybricks.coinbit.network.models.NameSymbolSortedPair
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber



class CoinSearchPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinSearchContract.View>(),
    CoinSearchContract.Presenter {

    override fun loadAllCoins() {
        currentView?.showOrHideLoadingIndicator(true)

        launch {
            coinRepo.getAllCoins()
                ?.catch {
                    Timber.e(it)
                    currentView?.onNetworkError(it.localizedMessage)
                }
                ?.collect { it ->
                    Timber.d("All Coins Loaded")

                    val sortedNullsLast =
                        it.sortedWith(compareBy(nullsLast()) { it.position })

                    currentView?.showOrHideLoadingIndicator(false)
                    currentView?.onCoinsLoaded(sortedNullsLast)
                }
        }
    }

    fun loadCoinsSimilarTo(tsym: String) {
        currentView?.showOrHideLoadingIndicator(true)

        launch {
            val topCoins = coinRepo.getTopCoinsByTotalVolume(tsym)

        }
    }

    override fun updateCoinWatchedStatus(watched: Boolean, coinID: String, coinSymbol: String) {
        launch {
            try {
                coinRepo.updateCoinWatchedStatus(watched, coinID)
                Timber.d("Coin status updated")
                currentView?.onCoinWatchedStatusUpdated(watched, coinSymbol)
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(ex.localizedMessage ?: "Error")
            }
        }
    }
}
