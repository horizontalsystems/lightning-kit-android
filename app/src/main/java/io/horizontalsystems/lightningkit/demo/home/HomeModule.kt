package io.horizontalsystems.lightningkit.demo.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lightningnetwork.lnd.lnrpc.ListChannelsResponse
import io.horizontalsystems.lightningkit.demo.core.App

object HomeModule {
    interface IInteractor {
        fun listChannels()
        fun clear()
    }

    interface IInteractorDelegate {
        fun onReceiveChannels(info: ListChannelsResponse)
        fun onReceivedError(e: Throwable)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = HomeInteractor(App.lightningKit)

            val presenter = HomePresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}