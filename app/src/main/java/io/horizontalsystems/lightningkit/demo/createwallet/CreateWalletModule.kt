package io.horizontalsystems.lightningkit.demo.createwallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.core.App

object CreateWalletModule {
    interface IInteractor {
        fun createWallet()
    }

    interface IInteractorDelegate {
        fun onCreateWallet(mnemonicList: List<String>)
        fun onSeedGenerateError(throwable: Throwable)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = CreateWalletInteractor(App.instance.filesDir.toString(), App.storage)
            val presenter = CreateWalletPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
