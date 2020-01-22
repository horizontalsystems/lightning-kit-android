package io.horizontalsystems.lightningkit.demo.payments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.ListPaymentsResponse
import com.github.lightningnetwork.lnd.lnrpc.Payment

class PaymentsPresenter(private val interactor: PaymentsModule.IInteractor) : ViewModel(),
    PaymentsModule.IInteractorDelegate {
    val payments = MutableLiveData<List<Payment>>()

    fun onLoad() {
        interactor.retrievePayments()
    }

    // IInteractorDelegate

    override fun onReceivePayments(info: ListPaymentsResponse) {
        payments.postValue(info.paymentsList)
    }

    override fun onReceivedError(e: Throwable) {

    }
}
