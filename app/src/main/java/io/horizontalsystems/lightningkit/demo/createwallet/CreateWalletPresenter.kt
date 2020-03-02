package io.horizontalsystems.lightningkit.demo.createwallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class CreateWalletPresenter(private val interactor: CreateWalletInteractor) : CreateWalletModule.IInteractorDelegate, ViewModel() {
    val mnemonicList = MutableLiveData<List<String>>()
    val walletInit = SingleLiveEvent<Unit>()

    fun onLoad() {
        interactor.generateSeed()
    }

    fun initWallet(mnemonicList: List<String>) {
        interactor.initWallet(mnemonicList)
    }

    override fun onSeedGenerated(mnemonicList: List<String>) {
        this.mnemonicList.postValue(mnemonicList)
    }

    override fun onSeedGenerateError(throwable: Throwable) {
        TODO("not implemented")
    }

    override fun onWalletInit() {
        walletInit.postValue(Unit)
    }

    override fun onWalletInitError(throwable: Throwable) {
        TODO("not implemented")
    }

    override fun onCleared() {
        interactor.clear()
    }
}
