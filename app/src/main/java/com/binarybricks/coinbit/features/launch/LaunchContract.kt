import com.binarybricks.coinbit.features.BaseView


interface LaunchContract {

    interface View : BaseView {
        fun onCoinsLoaded()
        fun onAllSupportedCoinsLoaded()
    }

    interface Presenter {
        fun loadAllCoins()
        fun getAllSupportedCoins(defaultCurrency: String)
    }
}
