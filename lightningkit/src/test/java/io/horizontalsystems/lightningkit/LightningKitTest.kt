package io.horizontalsystems.lightningkit

import com.github.lightningnetwork.lnd.lnrpc.Invoice
import com.github.lightningnetwork.lnd.lnrpc.SendResponse
import com.github.lightningnetwork.lnd.lnrpc.Transaction
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test

class LightningKitTest {

    private lateinit var lndNode: ILndNode
    private lateinit var lightningKit: LightningKit

    @Before
    fun setup() {
        lndNode = mock()
        lightningKit = LightningKit(lndNode)

        // Need statusObservable as not completed observable in retryWhenStatusIsSyncingOrRunning. Otherwise all retry-observables will be empty
        whenever(lndNode.statusObservable).thenReturn(PublishSubject.create())
    }

    @Test
    fun getBalanceObservable_triggerWhenInvoiceSettled() {
        val invoice = Invoice.newBuilder().setState(Invoice.InvoiceState.SETTLED).build()
        val invoicesObservable = Observable.just<Invoice>(invoice)

        whenever(lndNode.invoicesObservable()).thenReturn(invoicesObservable)
        whenever(lndNode.transactionsObservable()).thenReturn(Observable.empty())

        lightningKit.balanceObservable
            .test()
            .assertValue(Unit)
    }

    @Test
    fun getBalanceObservable_triggerWhenTransactionUpdate() {
        val transactionObservable = Observable.just<Transaction>(Transaction.getDefaultInstance())

        whenever(lndNode.invoicesObservable()).thenReturn(Observable.empty())
        whenever(lndNode.transactionsObservable()).thenReturn(transactionObservable)

        lightningKit.balanceObservable
            .test()
            .assertValue(Unit)
    }

    @Test
    fun getBalanceObservable_triggerWhenInvoicePayed() {
        whenever(lndNode.invoicesObservable()).thenReturn(Observable.empty())
        whenever(lndNode.transactionsObservable()).thenReturn(Observable.empty())
        whenever(lndNode.payInvoice("invoice")).thenReturn(Single.just(SendResponse.getDefaultInstance()))

        val testObserver = lightningKit.balanceObservable.test()

        lightningKit.payInvoice("invoice").subscribe()

        testObserver.assertValue(Unit)
    }
}