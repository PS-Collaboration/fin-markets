package com.binarybricks.coinbit.featurecomponents.cointickermodule

import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.data.database.CoinBitDatabase
import com.binarybricks.coinbit.data.database.entities.Exchange
import com.binarybricks.coinbit.network.api.api
import com.binarybricks.coinbit.network.models.CryptoTicker
import com.binarybricks.coinbit.utils.getCoinTickerFromJson



class CoinTickerRepository(
    private val coinBitDatabase: CoinBitDatabase?
) {

    /**
     * Get the ticker info from coin gecko
     */
    suspend fun getCryptoTickers(coinName: String): List<CryptoTicker>? {

        return if (CoinBitCache.ticker.containsKey(coinName)) {
            CoinBitCache.ticker[coinName]
        } else {
            val exchangeList = loadExchangeList()
            val coinTickerFromJson = getCoinTickerFromJson(api.getCoinTicker(coinName), exchangeList)
            if (coinTickerFromJson.isNotEmpty()) {
                CoinBitCache.ticker[coinName] = coinTickerFromJson
                coinTickerFromJson
            } else {
                null
            }
        }
    }

    /**
     * Get list of all exchanges, this is needed for logo
     */
    private suspend fun loadExchangeList(): List<Exchange>? {
        coinBitDatabase?.let {
            return it.exchangeDao().getAllExchanges()
        }
        return null
    }
}
