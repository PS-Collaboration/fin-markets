package com.binarybricks.coinbit.features.settings

import SettingsContract
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.network.api.api
import com.binarybricks.coinbit.network.models.NameSymbolSortedPair
import com.binarybricks.coinbit.network.models.getCoinFromCCCoin
import com.binarybricks.coinbit.utils.defaultExchange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class SettingsPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<SettingsContract.View>(), SettingsContract.Presenter {

    suspend fun assignValuesFromOneSourceToAnother(coinList: MutableList<WatchedCoin>) {
        val sortedCoinPairs =
            NameSymbolSortedPair.fromJSON(api.getCoinsSortedByMarketCap(limit = 5000))

        withContext(Dispatchers.Default){
            sortedCoinPairs.forEachIndexed { index, nameSymbolSortedPair ->
                val found = coinList.find { it.coin.symbol == nameSymbolSortedPair.symbol }
                if (found != null) {
                    found.circulatingSupply = nameSymbolSortedPair.circulatingSupply
                    found.position = index + 1
                }
            }
        }
    }

    override fun refreshCoinList(defaultCurrency: String) {
        launch {
            try {
                val allCoinsFromAPI = coinRepo.getAllCoinsFromAPI()
                val coinList: MutableList<WatchedCoin> = mutableListOf()
                val ccCoinList = allCoinsFromAPI.first
                ccCoinList.forEach { ccCoin ->
                    val coinInfo = allCoinsFromAPI.second[ccCoin.symbol.toLowerCase()]
                    coinList.add(getCoinFromCCCoin(ccCoin, defaultExchange, defaultCurrency, coinInfo))
                }

                coinRepo.insertCoinsInWatchList(coinList)

                Timber.d("Inserted all coins in db with size ${coinList.size}")
                currentView?.onCoinListRefreshed()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(ex.localizedMessage ?: "")
            }
        }
    }

    override fun refreshExchangeList() {
        launch {
            try {
                coinRepo.insertExchangeIntoList(coinRepo.getExchangeInfo())
                Timber.d("all exchanges loaded and inserted into db")
                currentView?.onExchangeListRefreshed()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(ex.localizedMessage ?: "")
            }
        }
    }
}
