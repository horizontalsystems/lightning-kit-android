package io.horizontalsystems.lightningkit.demo.channels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.*
import io.horizontalsystems.lightningkit.ILndNode

class ChannelsPresenter(private val interactor: ChannelsModule.IInteractor) : ViewModel(), ChannelsModule.IInteractorDelegate {
    val channels = MutableLiveData<List<Channel>>()
    val closedChannels = MutableLiveData<List<ChannelCloseSummary>>()
    val pendingChannels = MutableLiveData<PendingChannelsResponse>()
    val channelCloseStatusUpdate = MutableLiveData<CloseStatusUpdate>()
    val channelCloseFailure = MutableLiveData<Throwable>()

    init {
        interactor.subscribeToStatusUpdates()
        interactor.subscribeToChannelUpdates()
    }

    private fun sync() {
        interactor.listChannels()
        interactor.listClosedChannels()
        interactor.listPendingChannels()
    }

    fun onLoad() {
        sync()
    }

    // IInteractorDelegate

    override fun onReceiveChannels(info: ListChannelsResponse) {
        channels.postValue(info.channelsList)
    }

    override fun onReceiveClosedChannels(info: ClosedChannelsResponse) {
        closedChannels.postValue(info.channelsList)
    }

    override fun onReceivePendingChannels(info: PendingChannelsResponse) {
        pendingChannels.postValue(info)
    }

    override fun onReceivedError(e: Throwable) {
    }

    override fun onStatusUpdate(status: ILndNode.Status) {
        sync()
    }

    override fun onChannelsUpdate(channelEventUpdate: ChannelEventUpdate) {
        sync()
    }

    override fun closeChannel(channelPoint: String) {
        interactor.closeChannel(channelPoint)
    }

    override fun onChannelCloseStatusUpdate(closeStatus: CloseStatusUpdate) {
        channelCloseStatusUpdate.postValue(closeStatus)
    }

    override fun onChannelCloseFailure(e: Throwable) {
        channelCloseFailure.postValue(e)
    }

    override fun onCleared() {
        interactor.clear()
    }
}
