package io.horizontalsystems.lightningkit.demo.balance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.ChannelBalanceResponse
import com.github.lightningnetwork.lnd.lnrpc.NewAddressResponse
import com.github.lightningnetwork.lnd.lnrpc.WalletBalanceResponse
import io.horizontalsystems.lightningkit.ILndNode

class BalancePresenter(private val interactor: BalanceModule.IInteractor) : ViewModel(), BalanceModule.IInteractorDelegate {
    val totalBalance = MutableLiveData<String>()
    val confirmedBalance = MutableLiveData<String>()
    val unconfirmedBalance = MutableLiveData<String>()
    val onChainAddress = MutableLiveData<String>()

    val channelBalance = MutableLiveData<String>()
    val pendingOpenChannelBalance = MutableLiveData<String>()
    val statusLiveData = MutableLiveData<ILndNode.Status>()

    private fun sync() {
        interactor.getWalletBalance()
        interactor.getChannelBalance()
    }

    private var inited = false

    private fun init() {
        if (inited) return
        inited = true

        interactor.subscribeToStatusUpdates()
        interactor.subscribeToInvoices()
        interactor.subscribeToPayments()
    }

    fun onLoad() {
        init()
        sync()
        interactor.getOnChainAddress()
    }

    override fun onReceiveWalletBalance(balance: WalletBalanceResponse) {
        totalBalance.postValue(balance.totalBalance.toString(10))
        confirmedBalance.postValue(balance.confirmedBalance.toString(10))
        unconfirmedBalance.postValue(balance.unconfirmedBalance.toString(10))
    }

    override fun onReceiveWalletBalance(e: Throwable) {
    }

    override fun onReceiveOnChainAddress(newAddress: NewAddressResponse) {
        onChainAddress.postValue(newAddress.address)
    }

    override fun onReceiveOnChainAddress(e: Throwable) {
    }

    override fun onReceiveChannelBalance(balance: ChannelBalanceResponse) {
        channelBalance.postValue(balance.balance.toString(10))
        pendingOpenChannelBalance.postValue(balance.pendingOpenBalance.toString(10))
    }

    override fun onReceiveChannelBalance(e: Throwable) {
    }

    override fun onStatusUpdate(status: ILndNode.Status) {
        statusLiveData.postValue(status)
        if (status == ILndNode.Status.RUNNING) {
            interactor.getWalletBalance()
            interactor.getChannelBalance()
        }
    }

    override fun onInvoicesUpdate() {
        sync()
    }

    override fun onPaymentsUpdate() {
        sync()
    }

    override fun onCleared() {
        interactor.clear()
    }
}
