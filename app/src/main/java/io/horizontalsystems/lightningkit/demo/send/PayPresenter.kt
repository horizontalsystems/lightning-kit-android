package io.horizontalsystems.lightningkit.demo.send

import androidx.lifecycle.ViewModel

class PayPresenter(private val interactor: PayModule.IInteractor) : ViewModel(),
    PayModule.IInteractorDelegate {

    fun send(invoice: String) {
        interactor.payToInvoice(invoice)
    }

    // ViewModel

    override fun onCleared() {
        interactor.clear()
    }
}
