package io.horizontalsystems.lightningkit.demo.addinvoice

import androidx.lifecycle.ViewModel

class AddInvoicePresenter(private val interactor: AddInvoiceModule.IInteractor) : ViewModel(),
    AddInvoiceModule.IInteractorDelegate {

    fun addInvoice(amount: Long, memo: String) {
        interactor.addInvoice(amount, memo)
    }

    // ViewModel

    override fun onCleared() {
        interactor.clear()
    }
}
