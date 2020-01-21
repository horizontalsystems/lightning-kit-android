package io.horizontalsystems.lightningkit

import android.util.Base64
import com.github.lightningnetwork.lnd.lnrpc.*
import io.grpc.okhttp.OkHttpChannelBuilder
import io.reactivex.Single

class RemoteLnd(host: String, port: Int, cert: String, macaroon: String) : ILndNode {
    private val sslCertStr = Base64.decode(cert, Base64.DEFAULT)
    private val sslFactory = CustomSSLSocketFactory.create(sslCertStr)

    private val macaroonCallCredential = MacaroonCallCredential(macaroon)
    private val channel = OkHttpChannelBuilder
        .forAddress(host, port)
        .sslSocketFactory(sslFactory)
        .build()

    private val asyncStub = LightningGrpc.newStub(channel).withCallCredentials(macaroonCallCredential)

    override fun getInfo(): Single<GetInfoResponse> {
        val request = GetInfoRequest.newBuilder().build()

        return Single.create<GetInfoResponse> { asyncStub.getInfo(request, StreamObserverToSingle(it)) }
    }

    override fun listChannels(): Single<ListChannelsResponse> {
        val request = ListChannelsRequest.newBuilder().build()

        return Single.create<ListChannelsResponse> { asyncStub.listChannels(request, StreamObserverToSingle(it)) }
    }
}
