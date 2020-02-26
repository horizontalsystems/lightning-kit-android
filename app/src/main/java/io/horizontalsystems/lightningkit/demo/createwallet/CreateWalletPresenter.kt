package io.horizontalsystems.lightningkit.demo.createwallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateWalletPresenter(private val interactor: CreateWalletInteractor) : CreateWalletModule.IInteractorDelegate, ViewModel() {
    val mnemonicList = MutableLiveData<List<String>>()

    fun onLoad() {
        interactor.createWallet()
    }

    override fun onCreateWallet(mnemonicList: List<String>) {
        this.mnemonicList.postValue(mnemonicList)
    }

    override fun onSeedGenerateError(throwable: Throwable) {
        TODO("not implemented")
    }

    override fun onCleared() {
        interactor.clear()
    }
}
