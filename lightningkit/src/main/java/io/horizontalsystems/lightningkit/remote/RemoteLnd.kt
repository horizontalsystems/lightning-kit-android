package io.horizontalsystems.lightningkit.remote

import android.util.Base64
import com.github.lightningnetwork.lnd.lnrpc.*
import com.google.protobuf.ByteString
import io.grpc.okhttp.OkHttpChannelBuilder
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.hexToByteArray
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class RemoteLnd(host: String, port: Int, cert: String, macaroon: String) : ILndNode {

    private val statusSubject = PublishSubject.create<ILndNode.Status>()
    override val statusObservable = statusSubject

    var status: ILndNode.Status = ILndNode.Status.CONNECTING
        set(value) {
            if (field != value) {
                field = value
                statusSubject.onNext(value)
            }
        }

    private val sslCertStr = Base64.decode(cert, Base64.DEFAULT)
    private val sslFactory = CustomSSLSocketFactory.create(sslCertStr)

    private val macaroonCallCredential = MacaroonCallCredential(macaroon)
    private val channel = OkHttpChannelBuilder
        .forAddress(host, port)
        .sslSocketFactory(sslFactory)
        .build()

    private val asyncStub = LightningGrpc.newStub(channel).withCallCredentials(macaroonCallCredential)
    private var walletUnlocker = WalletUnlocker(channel, macaroonCallCredential)
    private val disposables = CompositeDisposable()

    fun scheduleStatusUpdates() {
        disposables.add(Observable.interval(1, TimeUnit.SECONDS)
            .flatMap {
                fetchStatus().toObservable()
            }
            .subscribe {
                status = it
            })
    }

    override fun getInfo(): Single<GetInfoResponse> {
        val request = GetInfoRequest.newBuilder().build()

        return Single.create<GetInfoResponse> { asyncStub.getInfo(request, StreamObserverToSingle(it)) }
    }

    override fun getWalletBalance(): Single<WalletBalanceResponse> {
        val request = WalletBalanceRequest.newBuilder().build()

        return Single.create<WalletBalanceResponse> { asyncStub.walletBalance(request, StreamObserverToSingle(it)) }
    }

    override fun getChannelBalance(): Single<ChannelBalanceResponse> {
        val request = ChannelBalanceRequest.newBuilder().build()

        return Single.create<ChannelBalanceResponse> { asyncStub.channelBalance(request, StreamObserverToSingle(it)) }
    }

    override fun listChannels(): Single<ListChannelsResponse> {
        val request = ListChannelsRequest.newBuilder().build()

        return Single.create<ListChannelsResponse> { asyncStub.listChannels(request, StreamObserverToSingle(it)) }
    }

    override fun listClosedChannels(): Single<ClosedChannelsResponse> {
        val request = ClosedChannelsRequest.newBuilder().build()

        return Single.create<ClosedChannelsResponse> { asyncStub.closedChannels(request, StreamObserverToSingle(it)) }
    }

    override fun listPendingChannels(): Single<PendingChannelsResponse> {
        val request = PendingChannelsRequest.newBuilder().build()

        return Single.create<PendingChannelsResponse> { asyncStub.pendingChannels(request, StreamObserverToSingle(it)) }
    }

    override fun decodePayReq(req: String): Single<PayReq> {
        val request = PayReqString
            .newBuilder()
            .setPayReq(req)
            .build()

        return Single.create<PayReq> { asyncStub.decodePayReq(request, StreamObserverToSingle(it)) }
    }

    override fun payInvoice(invoice: String): Single<SendResponse> {
        val request = SendRequest
            .newBuilder()
            .setPaymentRequest(invoice)
            .build()

        return Single.create<SendResponse> { asyncStub.sendPaymentSync(request, StreamObserverToSingle(it)) }
    }

    override fun addInvoice(amount: Long, memo: String): Single<AddInvoiceResponse> {
        val invoice = Invoice
            .newBuilder()
            .setValue(amount)
            .setMemo(memo)
            .build()

        return Single.create<AddInvoiceResponse> { asyncStub.addInvoice(invoice, StreamObserverToSingle(it)) }
    }

    override fun unlockWallet(password: String): Single<Unit> {
        if (walletUnlocker.isUnlocking()) {
            return Single.error(WalletUnlocker.UnlockingException)
        }

        return walletUnlocker.startUnlock(password)
    }

    override fun listPayments(): Single<ListPaymentsResponse> {
        val request = ListPaymentsRequest.newBuilder().build()

        return Single.create<ListPaymentsResponse> { asyncStub.listPayments(request, StreamObserverToSingle(it)) }
    }

    override fun listInvoices(
        pendingOnly: Boolean,
        offset: Long,
        limit: Long,
        reversed: Boolean
    ): Single<ListInvoiceResponse> {
        val request = ListInvoiceRequest
            .newBuilder()
            .setPendingOnly(pendingOnly)
            .setIndexOffset(offset)
            .setNumMaxInvoices(limit)
            .setReversed(reversed)
            .build()

        return Single.create<ListInvoiceResponse> { asyncStub.listInvoices(request, StreamObserverToSingle(it)) }
    }

    override fun invoicesObservable(): Observable<Invoice> {
        val request = InvoiceSubscription
            .newBuilder()
            .build()

        return Observable.create<Invoice> { asyncStub.subscribeInvoices(request, StreamObserverToObserver(it)) }
    }

    override fun channelsObservable(): Observable<ChannelEventUpdate> {
        val channelEventSubscription = ChannelEventSubscription
            .newBuilder()
            .build()

        return Observable.create<ChannelEventUpdate> { asyncStub.subscribeChannelEvents(channelEventSubscription, StreamObserverToObserver(it)) }
    }

    override fun openChannel(nodePubKey: String, amount: Long): Single<OpenStatusUpdate> {
        val openChannelRequest = OpenChannelRequest.newBuilder()
            .setNodePubkey(ByteString.copyFrom(nodePubKey.hexToByteArray()))
            .setSatPerByte(2) // todo: extract as param
            .setLocalFundingAmount(amount)
            .build()

        return Single.create<OpenStatusUpdate> { asyncStub.openChannel(openChannelRequest, StreamObserverToSingle(it)) }
    }

    override fun closeChannel(channelPoint: String, forceClose: Boolean): Single<CloseStatusUpdate> {
        val channelPointParts = channelPoint.split(':')

        val channelPoint = ChannelPoint.newBuilder()
            .setFundingTxidStr(channelPointParts.first())
            .setOutputIndex(channelPointParts.last().toInt())

        val request = CloseChannelRequest.newBuilder()
            .setChannelPoint(channelPoint)
            .setSatPerByte(2) // todo: extract as param
            .setForce(forceClose)
            .build()

        return Single.create<CloseStatusUpdate> { asyncStub.closeChannel(request, StreamObserverToSingle(it)) }
    }

    override fun connect(nodeAddress: String, nodePubKey: String): Single<ConnectPeerResponse> {
        val lightningAddress = LightningAddress
            .newBuilder()
            .setPubkey(nodePubKey)
            .setHost(nodeAddress)
            .build()

        val request = ConnectPeerRequest
            .newBuilder()
            .setAddr(lightningAddress)
            .build()

        return Single.create<ConnectPeerResponse> { asyncStub.connectPeer(request, StreamObserverToSingle(it)) }
    }

    override fun getOnChainAddress(): Single<NewAddressResponse> {
        val request = NewAddressRequest.newBuilder().build()

        return Single.create<NewAddressResponse> { asyncStub.newAddress(request, StreamObserverToSingle(it)) }
    }

    fun validateAsync(): Single<Unit> {
        return fetchStatus()
            .flatMap {
                if (it is ILndNode.Status.ERROR) {
                    Single.error(it.throwable)
                } else {
                    Single.just(Unit)
                }
            }
    }

    private fun fetchStatus(): Single<ILndNode.Status> {
        return getInfo()
            .map {
                if (it.syncedToGraph) {
                    ILndNode.Status.RUNNING
                } else {
                    ILndNode.Status.SYNCING
                }
            }
            .onErrorResumeNext { throwable: Throwable ->
                val message = throwable.message?.toLowerCase(Locale.ENGLISH) ?: ""

                val status = if (message.contains("unimplemented")) {
                    ILndNode.Status.LOCKED
                } else if (message.contains("unavailable") && walletUnlocker.isUnlocking()) {
                    ILndNode.Status.UNLOCKING
                } else {
                    ILndNode.Status.ERROR(throwable)
                }

                Single.just(status)
            }
    }

    override fun logout(): Single<Unit> {
        return Single.fromCallable {
            disposables.clear()
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)

            Unit
        }
    }
}
