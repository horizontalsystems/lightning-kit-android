package io.horizontalsystems.lightningkit

import com.github.lightningnetwork.lnd.lnrpc.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class LightningKit(private val lndNode: ILndNode) {

    val statusObservable: Observable<ILndNode.Status>
        get() = lndNode.statusObservable
    val invoicesObservable: Observable<Invoice>
        get() = lndNode.invoicesObservable()

    fun getWalletBalance(): Single<WalletBalanceResponse> {
        return lndNode.getWalletBalance()
    }

    fun getChannelBalance(): Single<ChannelBalanceResponse> {
        return lndNode.getChannelBalance()
    }

    fun listChannels(): Single<ListChannelsResponse> {
        return lndNode.listChannels()
    }

    fun decodePayReq(req: String): Single<PayReq> {
        return lndNode.decodePayReq(req)
    }

    fun payInvoice(invoice: String): Single<SendResponse> {
        return lndNode.payInvoice(invoice)
            .doOnSuccess {
                if (it.paymentError.isEmpty()) {
                    paymentsUpdatedSubject.onNext(Unit)
                }
            }
    }

    fun addInvoice(amount: Long, memo: String): Single<AddInvoiceResponse> {
        return lndNode.addInvoice(amount, memo)
    }

    fun listPayments(): Single<ListPaymentsResponse> {
        return lndNode.listPayments()
    }

    fun listInvoices(pending_only: Boolean = false, offset: Long = 0, limit: Long = 1000, reversed: Boolean = false): Single<ListInvoiceResponse> {
        return lndNode.listInvoices(pending_only, offset, limit, reversed)
    }

    fun unlockWallet(password: String): Single<Unit> {
        return lndNode.unlockWallet(password)
    }

    fun openChannel(nodePubKey: String, amount: Long, nodeAddress: String): Single<OpenStatusUpdate> {
        return lndNode.connect(nodeAddress, nodePubKey)
            .map { Unit }
            .onErrorResumeNext {
                if (it.message?.contains("already connected to peer") == true) {
                    Single.just(Unit)
                } else {
                    Single.error(it)
                }
            }
            .flatMap {
                lndNode.openChannel(nodePubKey, amount)
            }
    }

    companion object {
        fun validateRemoteConnection(host: String, port: Int, certificate: String, macaroon: String): Single<Unit> {
            val remoteLndNode = RemoteLnd(host, port, certificate, macaroon)

            return remoteLndNode.validateAsync()
        }

        fun remote(host: String, port: Int, certificate: String, macaroon: String): LightningKit {
            val remoteLndNode = RemoteLnd(host, port, certificate, macaroon)
            remoteLndNode.scheduleStatusUpdates()

            return LightningKit(remoteLndNode)
        }
    }
}