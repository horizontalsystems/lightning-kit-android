package io.horizontalsystems.lightningkit.demo.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class HomePresenter(private val interactor: HomeModule.IInteractor) : HomeModule.IInteractorDelegate, ViewModel() {
    val toggleUnlockWalletDialog = SingleLiveEvent<Boolean>()
    val unlockingProgress = SingleLiveEvent<Boolean>()
    val goToMainLiveEvent = SingleLiveEvent<Unit>()
    val error = MutableLiveData<String?>()
    val unlockError = MutableLiveData<Throwable>()

    private var inited = false

    private fun init() {
        if (inited) return
        inited = true

        interactor.subscribeToStatusUpdates()
    }

    fun onLoad() {
        init()
    }

    override fun onStatusUpdate(status: ILndNode.Status) {
        toggleUnlockWalletDialog.postValue(status is ILndNode.Status.LOCKED)
        unlockingProgress.postValue(status is ILndNode.Status.UNLOCKING)

        if (status is ILndNode.Status.ERROR) {
            val throwable = status.throwable

            var text = "${throwable.message}"
            throwable.cause?.message?.let {
                text += ", $it"
            }

            error.postValue(text)
        } else {
            error.postValue(null)
        }

        Log.e("AAA", "Status updated to: ${status}")
    }

    fun logout() {
        interactor.logout()
    }

    override fun onLogout() {
        goToMainLiveEvent.postValue(Unit)
    }

    fun unlock(password: String) {
        interactor.unlock(password)
    }

    override fun onUnlockSuccess() {

    }

    override fun onUnlockFailed(throwable: Throwable) {
        unlockError.postValue(throwable)
    }

    override fun onCleared() {
        interactor.clear()
    }
}