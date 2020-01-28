package io.horizontalsystems.lightningkit

import com.github.lightningnetwork.lnd.lnrpc.*
import io.reactivex.Observable
import io.reactivex.Single

interface ILndNode {
    sealed class Status {
        object CONNECTING : Status()
        object RUNNING : Status()
        object SYNCING : Status()
        object LOCKED : Status()
        object UNLOCKING : Status()
        class ERROR(val throwable: Throwable) : Status() {
            override fun equals(other: Any?): Boolean {
                return other is ERROR
            }

            override fun hashCode(): Int {
                return throwable.hashCode()
            }
        }
    }

    val statusObservable: Observable<Status>

    fun getInfo(): Single<GetInfoResponse>
    fun listChannels(): Single<ListChannelsResponse>
    fun listPayments(): Single<ListPaymentsResponse>
    fun listInvoices(
        pendingOnly: Boolean,
        offset: Long,
        limit: Long,
        reversed: Boolean
    ): Single<ListInvoiceResponse>

    fun payInvoice(invoice: String): Single<SendResponse>
    fun addInvoice(amount: Long, memo: String): Single<AddInvoiceResponse>
    fun unlockWallet(password: String): Single<Unit>
    fun decodePayReq(req: String): Single<PayReq>
}
