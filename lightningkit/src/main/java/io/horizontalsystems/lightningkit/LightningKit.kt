package io.horizontalsystems.lightningkit

import com.github.lightningnetwork.lnd.lnrpc.ListChannelsResponse
import io.reactivex.Single

class LightningKit(private val lndNode: ILndNode) {

    fun listChannels(): Single<ListChannelsResponse> {
        return lndNode.listChannels()
    }

    companion object {
        fun validateRemoteConnection(host: String, port: Int, certificate: String, macaroon: String): Single<Unit> {
            val remoteLndNode = RemoteLnd(host, port, certificate, macaroon)

            return remoteLndNode
                .getInfo()
                .map {
                    Unit
                }

        }

        fun remote(host: String, port: Int, certificate: String, macaroon: String): LightningKit {
            val remoteLndNode = RemoteLnd(host, port, certificate, macaroon)
            return LightningKit(remoteLndNode)
        }
    }
}