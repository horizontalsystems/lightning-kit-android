package io.horizontalsystems.lightningkit.demo.unlock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.core.App

object UnlockWalletModule {
    interface IInteractor {
        fun clear()
        fun unlock(password: String)
    }

    interface IInteractorDelegate {
        fun unlock(password: String)
        fun onUnlockSuccess()
        fun onUnlockFailed(throwable: Throwable)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = UnlockWalletInteractor(App.lightningKit)
            val presenter = UnlockWalletPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
