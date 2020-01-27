package io.horizontalsystems.lightningkit

import com.github.lightningnetwork.lnd.lnrpc.*
import io.reactivex.Observable
import io.reactivex.Single

interface ILndNode {
    sealed class Status {
        object CONNECTING: Status()
        object RUNNING: Status()
        object SYNCING: Status()
        object LOCKED: Status()
        object UNLOCKING: Status()
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
    fun payInvoice(invoice: String): Single<SendResponse>
    fun unlockWallet(password: String): Single<Unit>
}
