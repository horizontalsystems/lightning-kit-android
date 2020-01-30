package io.horizontalsystems.lightningkit.demo.balance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.ChannelBalanceResponse
import com.github.lightningnetwork.lnd.lnrpc.WalletBalanceResponse
import io.horizontalsystems.lightningkit.ILndNode

class BalancePresenter(private val interactor: BalanceModule.IInteractor) : ViewModel(), BalanceModule.IInteractorDelegate {
    val totalBalance = MutableLiveData<String>()
    val confirmedBalance = MutableLiveData<String>()
    val unconfirmedBalance = MutableLiveData<String>()

    val channelBalance = MutableLiveData<String>()
    val pendingOpenChannelBalance = MutableLiveData<String>()

    fun onLoad() {
        interactor.subscribeToStatusUpdates()
        interactor.getWalletBalance()
        interactor.getChannelBalance()
    }

    override fun onReceiveWalletBalance(balance: WalletBalanceResponse) {
        totalBalance.postValue(balance.totalBalance.toString(10))
        confirmedBalance.postValue(balance.confirmedBalance.toString(10))
        unconfirmedBalance.postValue(balance.unconfirmedBalance.toString(10))
    }

    override fun onReceiveWalletBalance(e: Throwable) {
    }

    override fun onReceiveChannelBalance(balance: ChannelBalanceResponse) {
        channelBalance.postValue(balance.balance.toString(10))
        pendingOpenChannelBalance.postValue(balance.pendingOpenBalance.toString(10))
    }

    override fun onReceiveChannelBalance(e: Throwable) {
    }

    override fun onStatusUpdate(status: ILndNode.Status) {
        if (status == ILndNode.Status.RUNNING) {
            interactor.getWalletBalance()
            interactor.getChannelBalance()
        }
    }

}
