package io.horizontalsystems.lightningkit.demo.send

import android.util.Log
import io.horizontalsystems.lightningkit.demo.core.App
import io.reactivex.disposables.CompositeDisposable

class PayInteractor() : PayModule.IInteractor {
    lateinit var delegate: PayModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    override fun payToInvoice(invoice: String) {
        Log.d("PAY", "Paying invoice $invoice")
        App.lightningKit.payInvoice(invoice)
            .subscribe({
                Log.d("PAY", "Payment Sent! ${it.paymentHash.toStringUtf8()}")
            }, {
                Log.e("PAY", "Payment send error: $it")
            })
            .let {
                disposables.add(it)
            }

    }

    override fun clear() {
        disposables.clear()
    }
}