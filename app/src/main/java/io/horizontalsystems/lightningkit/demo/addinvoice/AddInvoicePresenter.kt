package io.horizontalsystems.lightningkit.demo.addinvoice

import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class AddInvoicePresenter(private val interactor: AddInvoiceModule.IInteractor) : ViewModel(),
    AddInvoiceModule.IInteractorDelegate {

    val showAddedLiveEvent = SingleLiveEvent<String>()
    val addInvoiceFailure = SingleLiveEvent<Throwable>()

    fun addInvoice(amount: Long, memo: String) {
        interactor.addInvoice(amount, memo)
    }

    // ViewModel

    override fun onCleared() {
        interactor.clear()
    }

    override fun onSuccessAdd(invoiceString: String) {
        showAddedLiveEvent.postValue(invoiceString)
    }

    override fun onFailureAdd(throwable: Throwable) {
        addInvoiceFailure.postValue(throwable)
    }
}
