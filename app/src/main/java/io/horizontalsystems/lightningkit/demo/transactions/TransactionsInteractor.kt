package io.horizontalsystems.lightningkit.demo.transactions

import io.horizontalsystems.lightningkit.LightningKit
import io.reactivex.disposables.CompositeDisposable

class TransactionsInteractor(val lightningKit: LightningKit) : TransactionsModule.IInteractor {
    lateinit var delegate: TransactionsModule.IInteractorDelegate
    val disposables = CompositeDisposable()

    override fun subscribeToTransactions() {
        lightningKit.transactionsObservable
            .subscribe {
                fetchTransactions()
            }
            .let {
                disposables.add(it)
            }
    }

    override fun fetchTransactions() {
        lightningKit.getTransactions().subscribe({
            delegate.didFetchTransactions(it.transactionsList)
        }, {

        }).let {
            disposables.add(it)
        }
    }
}
