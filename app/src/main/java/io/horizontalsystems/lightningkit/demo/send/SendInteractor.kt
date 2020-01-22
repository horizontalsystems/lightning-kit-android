package io.horizontalsystems.lightningkit.demo.send

import android.util.Log
import io.horizontalsystems.lightningkit.demo.core.App
import io.reactivex.disposables.CompositeDisposable

class SendInteractor() : SendModule.IInteractor {
    lateinit var delegate: SendModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    override fun payToInvoice(invoice: String) {
        Log.d("SEND", "Paying invoice $invoice")
        App.lightningKit.payInvoice(invoice)
            .subscribe({
                Log.d("Send", "Payment Sent! ${it.paymentHash.toStringUtf8()}")
            }, {
                Log.e("Send", "Payment send error: $it")
            })
            .let {
                disposables.add(it)
            }

    }

    override fun clear() {
        disposables.clear()
    }
}