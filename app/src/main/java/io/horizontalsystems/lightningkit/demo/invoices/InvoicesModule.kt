package io.horizontalsystems.lightningkit.demo.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lightningnetwork.lnd.lnrpc.Invoice
import com.github.lightningnetwork.lnd.lnrpc.ListInvoiceResponse
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.demo.core.App

object InvoicesModule {
    interface IInteractor {
        fun retrieveInvoices()
        fun subscribeToStatusUpdates()
        fun clear()
        fun subscribeToInvoices()
    }

    interface IInteractorDelegate {
        fun onReceiveInvoices(info: ListInvoiceResponse)
        fun onReceivedError(e: Throwable)
        fun onStatusUpdate(status: ILndNode.Status)
        fun onInvoiceUpdate(invoice: Invoice)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = InvoicesInteractor(App.lightningKit)
            val presenter = InvoicesPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
