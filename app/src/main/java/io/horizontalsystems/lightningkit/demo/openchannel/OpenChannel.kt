package io.horizontalsystems.lightningkit.demo.openchannel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lightningnetwork.lnd.lnrpc.OpenStatusUpdate
import io.horizontalsystems.lightningkit.demo.core.App

object OpenChannel {
    interface IInteractor {
        fun openChannel(capacity: Long, nodePublicKey: String, nodeAddress: String)
        fun clear()
    }

    interface IInteractorDelegate {
        fun onOpenChannelSuccess(openStatusUpdate: OpenStatusUpdate)
        fun onOpenChannelFailed(throwable: Throwable)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = OpenChannelInteractor(App.lightningKit)
            val presenter = OpenChannelPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
