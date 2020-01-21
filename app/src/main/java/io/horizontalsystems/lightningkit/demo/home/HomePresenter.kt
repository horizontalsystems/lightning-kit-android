package io.horizontalsystems.lightningkit.demo.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.Channel
import com.github.lightningnetwork.lnd.lnrpc.ListChannelsResponse

class HomePresenter(private val interactor: HomeModule.IInteractor) : ViewModel(), HomeModule.IInteractorDelegate {
    val channels = MutableLiveData<List<Channel>>()

    fun onLoad() {
        interactor.listChannels()
    }

    // IInteractorDelegate

    override fun onReceiveChannels(info: ListChannelsResponse) {
        channels.postValue(info.channelsList)
    }

    override fun onReceivedError(e: Throwable) {

    }
}
