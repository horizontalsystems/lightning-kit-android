package io.horizontalsystems.lightningkit.demo.transactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.lightningnetwork.lnd.lnrpc.Transaction

class TransactionsPresenter(val interactor: TransactionsModule.IInteractor) : TransactionsModule.IInteractorDelegate, ViewModel() {
    val transactions = MutableLiveData<List<Transaction>>()

    fun onLoad() {
        init()

        interactor.fetchTransactions()
    }

    private var inited = false
    private fun init() {
        if (inited) return
        inited = true

//        interactor.subscribeToStatusUpdates()
        interactor.subscribeToTransactions()
    }


    override fun didFetchTransactions(transactions: List<Transaction>) {
        this.transactions.postValue(transactions)
    }
}
