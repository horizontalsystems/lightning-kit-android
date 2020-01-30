package io.horizontalsystems.lightningkit.demo.send

import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.PayReq
import com.github.lightningnetwork.lnd.lnrpc.SendResponse
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class PayPresenter(private val interactor: PayModule.IInteractor) : ViewModel(), PayModule.IInteractorDelegate {
    var invoice = ""
    val payReq = SingleLiveEvent<PayReq>()
    val clearInvoice = SingleLiveEvent<Unit>()
    val showPaidLiveEvent = SingleLiveEvent<Unit>()
    val paymentError = SingleLiveEvent<Throwable>()
    val invoiceError = SingleLiveEvent<Throwable>()

    fun send() {
        interactor.decodeInvoice(invoice)
    }

    fun confirmSend() {
        interactor.payInvoice(invoice)
    }

    override fun onPayReqDecode(payReq: PayReq) {
        this.payReq.postValue(payReq)
    }

    override fun onInvoiceDecodeFailed(throwable: Throwable) {
        invoiceError.postValue(throwable)
    }

    override fun onSuccessPayment(sendResponse: SendResponse) {
        showPaidLiveEvent.postValue(Unit)
        clearInvoice.postValue(Unit)
    }

    override fun onFailurePayment(throwable: Throwable) {
        paymentError.postValue(throwable)
    }

    // ViewModel

    override fun onCleared() {
        interactor.clear()
    }
}
