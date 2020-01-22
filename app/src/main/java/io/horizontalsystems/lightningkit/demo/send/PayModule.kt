package io.horizontalsystems.lightningkit.demo.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object PayModule {
    interface IInteractor {
        fun payToInvoice(invoice: String)
        fun clear()
    }

    interface IInteractorDelegate {
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = PayInteractor()
            val presenter = PayPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
