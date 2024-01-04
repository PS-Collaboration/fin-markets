import com.binarybricks.coinbit.features.BaseView
import com.binarybricks.coinbit.network.models.CryptoTicker



interface CoinTickerContract {

    interface View : BaseView {
        fun showOrHideLoadingIndicatorForTicker(showLoading: Boolean = true)
        fun onPriceTickersLoaded(tickerData: List<CryptoTicker>)
    }

    interface Presenter {
        fun getCryptoTickers(coinName: String)
    }
}
