package io.horizontalsystems.lightningkit.demo.openchannel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.OpenStatusUpdate
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class OpenChannelPresenter(private val interactor: OpenChannel.IInteractor) : OpenChannel.IInteractorDelegate, ViewModel() {
    val error = MutableLiveData<Throwable>()
    val notifyChannelOpened = SingleLiveEvent<Unit>()

    fun open(capacity: Long, nodePublicKey: String, nodeAddress: String) {
        interactor.openChannel(capacity, nodePublicKey, nodeAddress)
    }

    override fun onOpenChannelSuccess(openStatusUpdate: OpenStatusUpdate) {
        notifyChannelOpened.postValue(Unit)
    }

    override fun onOpenChannelFailed(throwable: Throwable) {
        error.postValue(throwable)
    }

    override fun onCleared() {
        interactor.clear()
    }
}
