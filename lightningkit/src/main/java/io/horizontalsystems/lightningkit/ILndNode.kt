package io.horizontalsystems.lightningkit

import com.github.lightningnetwork.lnd.lnrpc.*
import io.reactivex.Observable
import io.reactivex.Single

interface ILndNode {
    enum class Status {
        CONNECTING,
        RUNNING,
        SYNCING,
        LOCKED,
        ERROR
    }

    val statusObservable: Observable<Status>

    fun getInfo(): Single<GetInfoResponse>
    fun listChannels(): Single<ListChannelsResponse>
    fun listPayments(): Single<ListPaymentsResponse>
    fun payInvoice(invoice: String): Single<SendResponse>
    fun unlockWallet(password: String): Single<UnlockWalletResponse>
}
