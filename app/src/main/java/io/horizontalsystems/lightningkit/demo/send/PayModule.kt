package io.horizontalsystems.lightningkit.demo.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lightningnetwork.lnd.lnrpc.PayReq
import com.github.lightningnetwork.lnd.lnrpc.SendResponse
import io.horizontalsystems.lightningkit.demo.core.App

object PayModule {
    interface IInteractor {
        fun decodeInvoice(invoice: String)
        fun payInvoice(invoice: String)
        fun clear()
    }

    interface IInteractorDelegate {
        fun onPayReqDecode(payReq: PayReq)
        fun onInvoiceDecodeFailed(throwable: Throwable)
        fun onSuccessPayment(sendResponse: SendResponse)
        fun onFailurePayment(throwable: Throwable)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = PayInteractor(App.lightningKit)
            val presenter = PayPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
