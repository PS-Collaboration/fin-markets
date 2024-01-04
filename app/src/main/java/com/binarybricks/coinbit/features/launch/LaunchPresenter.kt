package com.binarybricks.coinbit.features.launch

import LaunchContract
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.features.BasePresenter
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.features.getTop5CoinsToWatch
import com.binarybricks.coinbit.network.api.api
import com.binarybricks.coinbit.network.models.CCCoin
import com.binarybricks.coinbit.network.models.CoinInfo
import com.binarybricks.coinbit.network.models.NameSymbolSortedPair
import com.binarybricks.coinbit.network.models.getCoinFromCCCoin
import com.binarybricks.coinbit.utils.defaultExchange
import com.google.gson.JsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class LaunchPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<LaunchContract.View>(), LaunchContract.Presenter {

    private var coinList: ArrayList<CCCoin>? = null
    private var coinInfoMap: Map<String, CoinInfo>? = null

    override fun loadAllCoins() {
        launch {
            try {
                val allCoinsFromAPI = coinRepo.getAllCoinsFromAPI(coinList, coinInfoMap)
                coinList = allCoinsFromAPI.first
                coinInfoMap = allCoinsFromAPI.second
                currentView?.onCoinsLoaded()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }

        loadExchangeFromAPI()
    }

    private fun loadExchangeFromAPI() {
        launch {
            try {
                coinRepo.insertExchangeIntoList(coinRepo.getExchangeInfo())
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

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


    override fun getAllSupportedCoins(defaultCurrency: String) {
        launch {
            try {
                val allCoinsFromAPI = coinRepo.getAllCoinsFromAPI(coinList, coinInfoMap)
                val coinList: MutableList<WatchedCoin> = mutableListOf()
                val ccCoinList = allCoinsFromAPI.first

                ccCoinList.forEach { ccCoin ->
                    val coinInfo = allCoinsFromAPI.second[ccCoin.symbol.toLowerCase()]
                    coinList.add(getCoinFromCCCoin(ccCoin, defaultExchange, defaultCurrency, coinInfo))
                }

                assignValuesFromOneSourceToAnother(coinList)

                coinRepo.insertCoinsInWatchList(coinList)

                val top5CoinsToWatch = getTop5CoinsToWatch()
                top5CoinsToWatch.forEach { coinId ->
                    coinRepo.updateCoinWatchedStatus(true, coinId)
                }

                Timber.d("Loaded all the coins and inserted in DB")
                currentView?.onAllSupportedCoinsLoaded()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
