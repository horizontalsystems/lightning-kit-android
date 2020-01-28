package io.horizontalsystems.lightningkit.demo.send

import io.horizontalsystems.lightningkit.LightningKit
import io.reactivex.disposables.CompositeDisposable

class PayInteractor(private val lightningKit: LightningKit) : PayModule.IInteractor {
    lateinit var delegate: PayModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    override fun decodeInvoice(invoice: String) {
        lightningKit.decodePayReq(invoice)
            .subscribe({
                delegate.onPayReqDecode(it)
            }, {

            })
            .let {
                disposables.add(it)
            }
    }

    override fun payInvoice(invoice: String) {
        lightningKit.payInvoice(invoice)
            .subscribe({
                delegate.onSuccessPayment(it)
            }, {
                delegate.onFailurePayment(it)
            })
            .let {
                disposables.add(it)
            }

    }

    override fun clear() {
        disposables.clear()
    }
}