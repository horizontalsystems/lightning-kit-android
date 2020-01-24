package io.horizontalsystems.lightningkit

import com.github.lightningnetwork.lnd.lnrpc.ListChannelsResponse
import com.github.lightningnetwork.lnd.lnrpc.ListPaymentsResponse
import com.github.lightningnetwork.lnd.lnrpc.SendResponse
import com.github.lightningnetwork.lnd.lnrpc.UnlockWalletResponse
import io.reactivex.Observable
import io.reactivex.Single

class LightningKit(private val lndNode: ILndNode) {

    val statusObservable: Observable<ILndNode.Status>
        get() = lndNode.statusObservable

    fun listChannels(): Single<ListChannelsResponse> {
        return lndNode.listChannels()
    }

    fun payInvoice(invoice: String): Single<SendResponse> {
        return lndNode.payInvoice(invoice)
    }

    fun listPayments(): Single<ListPaymentsResponse> {
        return lndNode.listPayments()
    }

    fun unlockWallet(password: String): Single<UnlockWalletResponse> {
        return lndNode.unlockWallet(password)
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