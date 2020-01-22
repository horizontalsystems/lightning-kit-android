package io.horizontalsystems.lightningkit.demo.send

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class SendPresenter(private val interactor: SendModule.IInteractor) : ViewModel(),
    SendModule.IInteractorDelegate {

    val invoice = MutableLiveData<String>()

    val error = SingleLiveEvent<String>()
    val navigateToHome = SingleLiveEvent<Unit>()

    // IInteractorDelegate

    fun send(view: View) {
        invoice.value?.let {
            interactor.payToInvoice(it)
        }
    }

    // ViewModel

    override fun onCleared() {
        interactor.clear()
    }
}
