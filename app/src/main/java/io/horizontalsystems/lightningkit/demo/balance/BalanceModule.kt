package io.horizontalsystems.lightningkit.demo.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lightningnetwork.lnd.lnrpc.ChannelBalanceResponse
import com.github.lightningnetwork.lnd.lnrpc.WalletBalanceResponse
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.demo.core.App

object BalanceModule {
    interface IInteractor {
        fun subscribeToStatusUpdates()
        fun getWalletBalance()
        fun getChannelBalance()
        fun clear()
    }

    interface IInteractorDelegate {
        fun onReceiveWalletBalance(balance: WalletBalanceResponse)
        fun onReceiveChannelBalance(balance: ChannelBalanceResponse)
        fun onReceiveWalletBalance(e: Throwable)
        fun onReceiveChannelBalance(e: Throwable)
        fun onStatusUpdate(it: ILndNode.Status)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = BalanceInteractor(App.lightningKit)
            val presenter = BalancePresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}