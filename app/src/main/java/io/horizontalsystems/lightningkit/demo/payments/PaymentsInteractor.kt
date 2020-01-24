package io.horizontalsystems.lightningkit.demo.payments

import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.LightningKit
import io.reactivex.disposables.CompositeDisposable

class PaymentsInteractor(private val lightningKit: LightningKit) : ViewModel(), PaymentsModule.IInteractor {
    lateinit var delegate: PaymentsModule.IInteractorDelegate

    private val disposables = CompositeDisposable()

    override fun retrievePayments() {
        lightningKit.listPayments()
            .subscribe({
                delegate.onReceivePayments(it)
            }, {
                delegate.onReceivedError(it)
            })
            .let {
                disposables.add(it)
            }
    }

    override fun subscribeToStatusUpdates() {
        lightningKit.statusObservable
            .subscribe {
                delegate.onStatusUpdate(it)
            }
            .let {
                disposables.add(it)
            }
    }

    // ViewModel

    override fun clear() {
        disposables.clear()
    }
}
