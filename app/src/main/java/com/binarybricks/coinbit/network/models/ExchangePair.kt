package com.binarybricks.coinbit.network.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class ExchangePair(val exchangeName: String, val pairList: MutableList<String>) : Parcelable
