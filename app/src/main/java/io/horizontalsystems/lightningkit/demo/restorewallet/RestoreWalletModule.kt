package io.horizontalsystems.lightningkit.demo.restorewallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.core.App

object RestoreWalletModule {
    interface IInteractor {
        fun restoreWallet(mnemonicList: List<String>)
    }

    interface IInteractorDelegate {
        fun onRestoreWallet()
        fun onError(throwable: Throwable)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = RestoreWalletInteractor(App.instance.filesDir.toString(), App.storage)
            val presenter = RestoreWalletPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
