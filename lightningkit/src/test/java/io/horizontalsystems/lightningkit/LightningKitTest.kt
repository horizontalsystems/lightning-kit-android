package io.horizontalsystems.lightningkit

import com.github.lightningnetwork.lnd.lnrpc.*
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
    fun getWalletBalanceObservable_triggerWhenTransactionUpdate() {
        val transactionObservable = Observable.just<Transaction>(mock(), mock())

        val walletBalanceResponse1 = mock<WalletBalanceResponse>()
        val walletBalanceResponse2 = mock<WalletBalanceResponse>()

        whenever(lndNode.getWalletBalance()).thenReturn(Single.just(walletBalanceResponse1), Single.just(walletBalanceResponse2))
        whenever(lndNode.transactionsObservable()).thenReturn(transactionObservable)

        lightningKit.walletBalanceObservable
            .test()
            .assertValues(walletBalanceResponse1, walletBalanceResponse2)
    }

    @Test
    fun getChannelBalanceObservable_triggerWhenInvoiceSettled() {
        val invoice = Invoice.newBuilder().setState(Invoice.InvoiceState.SETTLED).build()
        val invoicesObservable = Observable.just<Invoice>(invoice)

        val channelBalanceResponse = mock<ChannelBalanceResponse>()
        whenever(lndNode.getChannelBalance()).thenReturn(Single.just(channelBalanceResponse))
        whenever(lndNode.invoicesObservable()).thenReturn(invoicesObservable)
        whenever(lndNode.channelsObservable()).thenReturn(Observable.empty())

        lightningKit.channelBalanceObservable
            .test()
            .assertValue(channelBalanceResponse)
    }

    @Test
    fun getChannelBalanceObservable_triggerWhenInvoicePayed() {
        val channelBalanceResponse = mock<ChannelBalanceResponse>()
        whenever(lndNode.getChannelBalance()).thenReturn(Single.just(channelBalanceResponse))
        whenever(lndNode.invoicesObservable()).thenReturn(Observable.empty())
        whenever(lndNode.channelsObservable()).thenReturn(Observable.empty())
        whenever(lndNode.payInvoice("invoice")).thenReturn(Single.just(SendResponse.getDefaultInstance()))

        val testObserver = lightningKit.channelBalanceObservable.test()

        lightningKit.payInvoice("invoice").subscribe()

        testObserver.assertValue(channelBalanceResponse)
    }

    @Test
    fun getChannelBalanceObservable_triggerWhenChannelUpdate() {
        val channelBalanceResponse = mock<ChannelBalanceResponse>()
        whenever(lndNode.getChannelBalance()).thenReturn(Single.just(channelBalanceResponse))
        whenever(lndNode.invoicesObservable()).thenReturn(Observable.empty())
        whenever(lndNode.channelsObservable()).thenReturn(Observable.just(mock()))

        lightningKit.channelBalanceObservable
            .test()
            .assertValue(channelBalanceResponse)
    }
}