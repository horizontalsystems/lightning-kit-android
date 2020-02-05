package io.horizontalsystems.lightningkit.demo.restorewallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class RestoreWalletPresenter(private val interactor: RestoreWalletModule.IInteractor) : RestoreWalletModule.IInteractorDelegate, ViewModel() {
    var mnemonic: String = ""
    var mnemonicError = MutableLiveData<String>()
    var goToMainLiveEvent = SingleLiveEvent<Unit>()

    override fun onError(throwable: Throwable) {
        mnemonicError.postValue(throwable.message)
    }

    override fun onRestoreWallet() {
        goToMainLiveEvent.postValue(Unit)
    }

    fun restore() {
        val mnemonicList = mnemonic.split(" ").map { it.trim() }

        if (mnemonicList.size == 24) {
            interactor.restoreWallet(mnemonicList)
        } else {
            mnemonicError.postValue("Please enter 24 words separated by space")
        }
    }
}
