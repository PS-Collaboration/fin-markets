package com.binarybricks.coinbit.network.models

import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class NameSymbolSortedPair(val name: String, val symbol: String, val circulatingSupply: Double) {
    companion object {
        fun fromJSON(data: JsonObject): MutableList<NameSymbolSortedPair> {
            val pairs: MutableList<NameSymbolSortedPair> = mutableListOf()

            val dataArray: JsonArray = data["data"] as JsonArray

            dataArray.forEach { jsonElement ->
                val pair = NameSymbolSortedPair(
                    name = jsonElement.asJsonObject["name"].asString,
                    symbol = jsonElement.asJsonObject["symbol"].asString,
                    circulatingSupply = jsonElement.asJsonObject["circulating_supply"].asDouble
                )
                pairs.add(pair)
            }

            return pairs
        }
    }
}