package io.horizontalsystems.lightningkit.demo.balance

import io.horizontalsystems.lightningkit.LightningKit
import io.reactivex.disposables.CompositeDisposable

class BalanceInteractor(private val lightningKit: LightningKit) : BalanceModule.IInteractor {
    lateinit var delegate: BalanceModule.IInteractorDelegate

    private val disposables = CompositeDisposable()

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
                delegate.onInvoicesUpdate()
            }
            .let {
                disposables.add(it)
            }
    }

    override fun subscribeToPayments() {
        lightningKit.paymentsObservable
            .subscribe {
                delegate.onPaymentsUpdate()
            }
            .let {
                disposables.add(it)
            }
    }

    override fun getWalletBalance() {
        lightningKit.getWalletBalance()
            .subscribe({
                delegate.onReceiveWalletBalance(it)
            }, {
                delegate.onReceiveWalletBalance(it)
            })
            .let {
                disposables.add(it)
            }
    }

    override fun getOnChainAddress() {
        lightningKit.getOnChainAddress()
            .subscribe({
                delegate.onReceiveOnChainAddress(it)
            }, {
                delegate.onReceiveOnChainAddress(it)
            })
            .let {
                disposables.add(it)
            }
    }

    override fun getChannelBalance() {
        lightningKit.getChannelBalance()
            .subscribe({
                delegate.onReceiveChannelBalance(it)
            }, {
                delegate.onReceiveChannelBalance(it)
            })
            .let {
                disposables.add(it)
            }
    }

    override fun clear() {
        disposables.clear()
    }
}
