package io.horizontalsystems.lightningkit.demo.invoices

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.ListInvoiceResponse
import com.github.lightningnetwork.lnd.lnrpc.Invoice
import io.horizontalsystems.lightningkit.ILndNode

class InvoicesPresenter(private val interactor: InvoicesModule.IInteractor) : ViewModel(),
    InvoicesModule.IInteractorDelegate {
    val invoices = MutableLiveData<List<Invoice>>()

    fun onLoad() {
        interactor.subscribeToStatusUpdates()
        interactor.retrieveInvoices()
    }

    // IInteractorDelegate

    override fun onReceiveInvoices(info: ListInvoiceResponse) {
        invoices.postValue(info.invoicesList)
    }

    override fun onReceivedError(e: Throwable) {

    }

    override fun onStatusUpdate(status: ILndNode.Status) {
        if (status == ILndNode.Status.RUNNING) {
            interactor.retrieveInvoices()
        }
    }
}
