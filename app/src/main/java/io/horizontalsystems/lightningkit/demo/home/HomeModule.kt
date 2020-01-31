package io.horizontalsystems.lightningkit.demo.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.demo.core.App

object HomeModule {
    interface IInteractor {
        fun subscribeToStatusUpdates()
        fun logout()
        fun unlock(password: String)
    }

    interface IInteractorDelegate {
        fun onStatusUpdate(status: ILndNode.Status)
        fun onLogout()
        fun onUnlockSuccess()
        fun onUnlockFailed(throwable: Throwable)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = HomeInteractor(App.lightningKit, App.storage)
            val presenter = HomePresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
