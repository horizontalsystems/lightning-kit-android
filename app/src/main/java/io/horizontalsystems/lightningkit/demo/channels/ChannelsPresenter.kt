package io.horizontalsystems.lightningkit.demo.channels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.Channel
import com.github.lightningnetwork.lnd.lnrpc.ChannelEventUpdate
import com.github.lightningnetwork.lnd.lnrpc.ListChannelsResponse
import io.horizontalsystems.lightningkit.ILndNode

class ChannelsPresenter(private val interactor: ChannelsModule.IInteractor) : ViewModel(), ChannelsModule.IInteractorDelegate {
    val channels = MutableLiveData<List<Channel>>()

    fun onLoad() {
        interactor.subscribeToStatusUpdates()
        interactor.subscribeToChannelUpdates()
        interactor.listChannels()
    }

    // IInteractorDelegate

    override fun onReceiveChannels(info: ListChannelsResponse) {
        channels.postValue(info.channelsList)
    }

    override fun onReceivedError(e: Throwable) {

    }

    override fun onStatusUpdate(status: ILndNode.Status) {
        interactor.listChannels()
    }

    override fun onChannelsUpdate(channelEventUpdate: ChannelEventUpdate) {
        interactor.listChannels()
    }
}
