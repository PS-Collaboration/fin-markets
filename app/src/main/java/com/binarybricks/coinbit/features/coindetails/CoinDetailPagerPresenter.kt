package com.binarybricks.coinbit.features.coindetails

import CoinDetailsPagerContract
import com.binarybricks.coinbit.features.BasePresenter
import kotlinx.coroutines.launch
import timber.log.Timber


class CoinDetailPagerPresenter(private val coinDetailsPagerRepository: CoinDetailsPagerRepository) :
    BasePresenter<CoinDetailsPagerContract.View>(), CoinDetailsPagerContract.Presenter {
    override fun loadWatchedCoins() {
        launch {
            try {
                currentView?.onWatchedCoinsLoaded(coinDetailsPagerRepository.loadWatchedCoins())
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
