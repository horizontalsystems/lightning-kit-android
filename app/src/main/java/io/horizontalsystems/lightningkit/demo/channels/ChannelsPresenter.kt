package io.horizontalsystems.lightningkit.demo.channels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.*
import io.horizontalsystems.lightningkit.ILndNode

class ChannelsPresenter(private val interactor: ChannelsModule.IInteractor) : ViewModel(), ChannelsModule.IInteractorDelegate {
    val channels = MutableLiveData<List<Channel>>()
    val closedChannels = MutableLiveData<List<ChannelCloseSummary>>()

    init {
        interactor.subscribeToStatusUpdates()
        interactor.subscribeToChannelUpdates()
    }

    fun onLoad() {
        interactor.listChannels()
        interactor.listClosedChannels()
    }

    // IInteractorDelegate

    override fun onReceiveChannels(info: ListChannelsResponse) {
        channels.postValue(info.channelsList)
    }

    override fun onReceiveClosedChannels(info: ClosedChannelsResponse) {
        closedChannels.postValue(info.channelsList)
    }

    override fun onReceivedError(e: Throwable) {

    }

    override fun onStatusUpdate(status: ILndNode.Status) {
        interactor.listChannels()
    }

    override fun onChannelsUpdate(channelEventUpdate: ChannelEventUpdate) {
        interactor.listChannels()
    }

    override fun onCleared() {
        interactor.clear()
    }
}
