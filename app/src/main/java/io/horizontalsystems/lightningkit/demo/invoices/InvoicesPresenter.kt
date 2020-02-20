package io.horizontalsystems.lightningkit.demo.invoices

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.Invoice
import com.github.lightningnetwork.lnd.lnrpc.ListInvoiceResponse
import io.horizontalsystems.lightningkit.ILndNode

class InvoicesPresenter(private val interactor: InvoicesModule.IInteractor) : ViewModel(),
    InvoicesModule.IInteractorDelegate {
    val invoicesUpdate = MutableLiveData<List<Invoice>>()
    val invoiceUpdate = MutableLiveData<Invoice>()

    private var inited = false

    private fun init() {
        if (inited) return
        inited = true

        interactor.subscribeToStatusUpdates()
        interactor.subscribeToInvoices()
    }

    fun onLoad() {
        init()
        interactor.retrieveInvoices()
    }

    // IInteractorDelegate

    override fun onReceiveInvoices(info: ListInvoiceResponse) {
        invoicesUpdate.postValue(info.invoicesList)
    }

    override fun onReceivedError(e: Throwable) {

    }

    override fun onStatusUpdate(status: ILndNode.Status) {
        if (status == ILndNode.Status.RUNNING) {
            interactor.retrieveInvoices()
        }
    }

    override fun onInvoiceUpdate(invoice: Invoice) {
        invoiceUpdate.postValue(invoice)
    }

    override fun onCleared() {
        interactor.clear()
    }
}
