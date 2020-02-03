package io.horizontalsystems.lightningkit.demo.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lightningnetwork.lnd.lnrpc.ChannelEventUpdate
import com.github.lightningnetwork.lnd.lnrpc.ClosedChannelsResponse
import com.github.lightningnetwork.lnd.lnrpc.ListChannelsResponse
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.demo.core.App

object ChannelsModule {
    interface IInteractor {
        fun listChannels()
        fun listClosedChannels()
        fun subscribeToStatusUpdates()
        fun subscribeToChannelUpdates()
        fun clear()
    }

    interface IInteractorDelegate {
        fun onReceiveChannels(info: ListChannelsResponse)
        fun onReceiveClosedChannels(info: ClosedChannelsResponse)
        fun onReceivedError(e: Throwable)
        fun onStatusUpdate(status: ILndNode.Status)
        fun onChannelsUpdate(channelEventUpdate: ChannelEventUpdate)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = ChannelsInteractor(App.lightningKit)
            val presenter = ChannelsPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}