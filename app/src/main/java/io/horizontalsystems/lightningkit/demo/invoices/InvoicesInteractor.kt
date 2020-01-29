package io.horizontalsystems.lightningkit.demo.invoices

import io.horizontalsystems.lightningkit.LightningKit
import io.reactivex.disposables.CompositeDisposable

class InvoicesInteractor(private val lightningKit: LightningKit) : InvoicesModule.IInteractor {
    lateinit var delegate: InvoicesModule.IInteractorDelegate

    private val disposables = CompositeDisposable()

    override fun retrieveInvoices() {
        lightningKit.listInvoices()
            .subscribe({
                delegate.onReceiveInvoices(it)
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

    override fun subscribeToInvoices() {
        lightningKit.invoicesObservable
            .subscribe {
                delegate.onInvoiceUpdate(it)
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
