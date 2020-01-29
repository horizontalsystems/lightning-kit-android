package io.horizontalsystems.lightningkit.demo.addinvoice

import android.util.Log
import io.horizontalsystems.lightningkit.demo.core.App
import io.reactivex.disposables.CompositeDisposable

class AddInvoiceInteractor() : AddInvoiceModule.IInteractor {
    lateinit var delegate: AddInvoiceModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    override fun addInvoice(amount: Long, memo: String) {
        Log.d("PAY", "Adding new invoice with amount $amount")
        App.lightningKit.addInvoice(amount, memo)
            .subscribe({
                Log.d("NEW INVOICE", "Invoice Added! ${it.paymentRequest}")
                delegate.onSuccessAdd(it.paymentRequest)
            }, {
                Log.e("NEW INVOICE", "Invoice add error: $it")
                delegate.onFailureAdd(it)
            })
            .let {
                disposables.add(it)
            }

    }

    override fun clear() {
        disposables.clear()
    }
}