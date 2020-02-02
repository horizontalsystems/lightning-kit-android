package io.horizontalsystems.lightningkit.demo.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lightningnetwork.lnd.lnrpc.ListPaymentsResponse
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.demo.core.App

object PaymentsModule {
    interface IInteractor {
        fun retrievePayments()
        fun subscribeToStatusUpdates()
        fun subscribeToPayments()
        fun clear()
    }

    interface IInteractorDelegate {
        fun onReceivePayments(info: ListPaymentsResponse)
        fun onReceivedError(e: Throwable)
        fun onStatusUpdate(status: ILndNode.Status)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = PaymentsInteractor(App.lightningKit)
            val presenter = PaymentsPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
