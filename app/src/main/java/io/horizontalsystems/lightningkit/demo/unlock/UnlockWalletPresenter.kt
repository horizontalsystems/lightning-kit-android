package io.horizontalsystems.lightningkit.demo.unlock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class UnlockWalletPresenter(private val interactor: UnlockWalletModule.IInteractor) :
    UnlockWalletModule.IInteractorDelegate, ViewModel() {
    val error = MutableLiveData<Throwable>()
    val closeEvent = SingleLiveEvent<Unit>()

    override fun onCleared() {
        interactor.clear()
    }

    override fun unlock(password: String) {
        interactor.unlock(password)
    }

    override fun onUnlockSuccess() {
        closeEvent.postValue(Unit)
    }

    override fun onUnlockFailed(throwable: Throwable) {
        error.postValue(throwable)
    }
}