package com.binarybricks.coinbit.utils.resourcemanager

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

/**
 * This is used to inject the resources for testing
 */
interface AndroidResourceManager {

    fun getString(@StringRes resId: Int): String

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int): String

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String

    fun getColor(resId: Int): Int

    fun getDrawable(@DrawableRes resId: Int): Drawable?
}
