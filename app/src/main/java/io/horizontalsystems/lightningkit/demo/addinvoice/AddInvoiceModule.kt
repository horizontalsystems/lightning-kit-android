package io.horizontalsystems.lightningkit.demo.addinvoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object AddInvoiceModule {
    interface IInteractor {
        fun addInvoice(amount: Long, memo: String)
        fun clear()
    }

    interface IInteractorDelegate {
        fun onSuccessAdd(invoiceString: String)
        fun onFailureAdd(throwable: Throwable)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = AddInvoiceInteractor()
            val presenter = AddInvoicePresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
