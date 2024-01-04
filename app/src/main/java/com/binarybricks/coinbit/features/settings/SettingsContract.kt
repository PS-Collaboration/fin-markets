import com.binarybricks.coinbit.features.BaseView


interface SettingsContract {

    interface View : BaseView {
        fun onCoinListRefreshed()
        fun onExchangeListRefreshed()
    }

    interface Presenter {
        fun refreshCoinList(defaultCurrency: String)
        fun refreshExchangeList()
    }
}
