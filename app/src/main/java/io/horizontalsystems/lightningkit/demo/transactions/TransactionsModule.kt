package io.horizontalsystems.lightningkit.demo.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lightningnetwork.lnd.lnrpc.Transaction
import io.horizontalsystems.lightningkit.demo.core.App

object TransactionsModule {

    interface IInteractor {
        fun fetchTransactions()
        fun subscribeToTransactions()
    }

    interface IInteractorDelegate {
        fun didFetchTransactions(transactions: List<Transaction>)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val interactor = TransactionsInteractor(App.lightningKit)
            val presenter = TransactionsPresenter(interactor)

            interactor.delegate = presenter

            return presenter as T
        }
    }
}
