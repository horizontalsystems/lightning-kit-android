package io.horizontalsystems.lightningkit.demo.remoteconnection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.core.App

object RemoteConnectionModule {
    interface IInteractor {
        fun validateConnection(connectionParams: ConnectionParams)
        fun saveConnectionParams(connectionParams: ConnectionParams)
        fun clear()
    }

    interface IInteractorDelegate {
        fun onValidationSuccess(connectionParams: ConnectionParams)
        fun onValidationFailed(e: Throwable)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = RemoteConnectionInteractor(App.storage)

            val presenter = RemoteConnectionPresenter(interactor)

            interactor.delegate = presenter


            return presenter as T
        }
    }
}
