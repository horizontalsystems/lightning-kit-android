package io.horizontalsystems.lightningkit.demo.payments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.ListPaymentsResponse
import com.github.lightningnetwork.lnd.lnrpc.Payment
import io.horizontalsystems.lightningkit.ILndNode

class PaymentsPresenter(private val interactor: PaymentsModule.IInteractor) : ViewModel(), PaymentsModule.IInteractorDelegate {
    val payments = MutableLiveData<List<Payment>>()

    init {
        interactor.subscribeToStatusUpdates()
    }

    fun onLoad() {
        interactor.retrievePayments()
    }

    // IInteractorDelegate

    override fun onReceivePayments(info: ListPaymentsResponse) {
        payments.postValue(info.paymentsList)
    }

    override fun onReceivedError(e: Throwable) {

    }

    override fun onStatusUpdate(status: ILndNode.Status) {
        if (status == ILndNode.Status.RUNNING) {
            interactor.retrievePayments()
        }
    }
}
