package io.horizontalsystems.lightningkit.remote

import android.util.Base64
import com.github.lightningnetwork.lnd.lnrpc.*
import com.google.protobuf.ByteString
import io.grpc.ManagedChannel
import io.grpc.okhttp.OkHttpChannelBuilder
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.hexToByteArray
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory

class RemoteLnd(remoteLndCredentials: RemoteLndCredentials) : ILndNode {
    override var status: ILndNode.Status = ILndNode.Status.CONNECTING
        set(value) {
            if (field != value) {
                field = value
                statusSubject.onNext(value)
            }
        }

    private val statusSubject = BehaviorSubject.createDefault(status)
    override val statusObservable = statusSubject

    private val sslCertStr = Base64.decode(remoteLndCredentials.certificate, Base64.DEFAULT)
    private lateinit var sslFactory: SSLSocketFactory

    private val macaroonCallCredential = MacaroonCallCredential(remoteLndCredentials.macaroon)
    private lateinit var channel: ManagedChannel

    private lateinit var asyncRpcStub: LightningGrpc.LightningStub
    private lateinit var asyncWalletUnlockerStub: WalletUnlockerGrpc.WalletUnlockerStub

    private lateinit var walletUnlocker: WalletUnlocker
    private val disposables = CompositeDisposable()

    init {
        try {
            sslFactory = CustomSSLSocketFactory.create(sslCertStr)
            channel = OkHttpChannelBuilder
                .forAddress(remoteLndCredentials.host, remoteLndCredentials.port)
                .sslSocketFactory(sslFactory)
                .build()

            asyncRpcStub = LightningGrpc.newStub(channel).withCallCredentials(macaroonCallCredential)
            asyncWalletUnlockerStub = WalletUnlockerGrpc.newStub(channel).withCallCredentials(macaroonCallCredential)

            walletUnlocker = WalletUnlocker(asyncWalletUnlockerStub)

            status = fetchStatus().blockingGet()

            scheduleStatusUpdates()
        } catch (e: Exception) {
            status = ILndNode.Status.ERROR(e)
        }
    }

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

        return Single.create<GetInfoResponse> { asyncRpcStub.getInfo(request, StreamObserverToSingle(it)) }
    }

    override fun getWalletBalance(): Single<WalletBalanceResponse> {
        val request = WalletBalanceRequest.newBuilder().build()

        return Single.create<WalletBalanceResponse> { asyncRpcStub.walletBalance(request, StreamObserverToSingle(it)) }
    }

    override fun getChannelBalance(): Single<ChannelBalanceResponse> {
        val request = ChannelBalanceRequest.newBuilder().build()

        return Single.create<ChannelBalanceResponse> { asyncRpcStub.channelBalance(request, StreamObserverToSingle(it)) }
    }

    override fun listChannels(): Single<ListChannelsResponse> {
        val request = ListChannelsRequest.newBuilder().build()

        return Single.create<ListChannelsResponse> { asyncRpcStub.listChannels(request, StreamObserverToSingle(it)) }
    }

    override fun listClosedChannels(): Single<ClosedChannelsResponse> {
        val request = ClosedChannelsRequest.newBuilder().build()

        return Single.create<ClosedChannelsResponse> { asyncRpcStub.closedChannels(request, StreamObserverToSingle(it)) }
    }

    override fun listPendingChannels(): Single<PendingChannelsResponse> {
        val request = PendingChannelsRequest.newBuilder().build()

        return Single.create<PendingChannelsResponse> { asyncRpcStub.pendingChannels(request, StreamObserverToSingle(it)) }
    }

    override fun decodePayReq(req: String): Single<PayReq> {
        val request = PayReqString
            .newBuilder()
            .setPayReq(req)
            .build()

        return Single.create<PayReq> { asyncRpcStub.decodePayReq(request, StreamObserverToSingle(it)) }
    }

    override fun payInvoice(invoice: String): Single<SendResponse> {
        val request = SendRequest
            .newBuilder()
            .setPaymentRequest(invoice)
            .build()

        return Single.create<SendResponse> { asyncRpcStub.sendPaymentSync(request, StreamObserverToSingle(it)) }
    }

    override fun addInvoice(amount: Long, memo: String): Single<AddInvoiceResponse> {
        val invoice = Invoice
            .newBuilder()
            .setValue(amount)
            .setMemo(memo)
            .build()

        return Single.create<AddInvoiceResponse> { asyncRpcStub.addInvoice(invoice, StreamObserverToSingle(it)) }
    }

    override fun unlockWallet(password: String): Single<Unit> {
        if (walletUnlocker.isUnlocking()) {
            return Single.error(WalletUnlocker.UnlockingException)
        }

        return walletUnlocker.startUnlock(password)
    }

    override fun unlockWalletBlocking(password: String) {
        unlockWallet(password)
            .subscribe()
            .let {
                disposables.add(it)
            }
    }

    override fun listPayments(): Single<ListPaymentsResponse> {
        val request = ListPaymentsRequest.newBuilder().build()

        return Single.create<ListPaymentsResponse> { asyncRpcStub.listPayments(request, StreamObserverToSingle(it)) }
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

        return Single.create<ListInvoiceResponse> { asyncRpcStub.listInvoices(request, StreamObserverToSingle(it)) }
    }

    override fun getTransactions(): Single<TransactionDetails> {
        val request = GetTransactionsRequest
            .newBuilder()
            .build()

        return Single.create<TransactionDetails> { asyncRpcStub.getTransactions(request, StreamObserverToSingle(it)) }
    }

    override fun invoicesObservable(): Observable<Invoice> {
        val request = InvoiceSubscription
            .newBuilder()
            .build()

        return Observable.create<Invoice> { asyncRpcStub.subscribeInvoices(request, StreamObserverToObserver(it)) }
    }

    override fun channelsObservable(): Observable<ChannelEventUpdate> {
        val channelEventSubscription = ChannelEventSubscription
            .newBuilder()
            .build()

        return Observable.create<ChannelEventUpdate> { asyncRpcStub.subscribeChannelEvents(channelEventSubscription, StreamObserverToObserver(it)) }
    }

    override fun transactionsObservable(): Observable<Transaction> {
        val request = GetTransactionsRequest
            .newBuilder()
            .build()

        return Observable.create<Transaction> { asyncRpcStub.subscribeTransactions(request, StreamObserverToObserver(it)) }
    }

    override fun openChannel(nodePubKey: String, amount: Long): Observable<OpenStatusUpdate> {
        val openChannelRequest = OpenChannelRequest.newBuilder()
            .setNodePubkey(ByteString.copyFrom(nodePubKey.hexToByteArray()))
            .setSatPerByte(2) // todo: extract as param
            .setLocalFundingAmount(amount)
            .build()

        return Observable.create<OpenStatusUpdate> { asyncRpcStub.openChannel(openChannelRequest, StreamObserverToObserver(it)) }
    }

    override fun closeChannel(channelPoint: String, forceClose: Boolean): Observable<CloseStatusUpdate> {
        val channelPointParts = channelPoint.split(':')

        val channelPoint = ChannelPoint.newBuilder()
            .setFundingTxidStr(channelPointParts.first())
            .setOutputIndex(channelPointParts.last().toInt())

        val request = CloseChannelRequest.newBuilder()
            .setChannelPoint(channelPoint)
            .setSatPerByte(2) // todo: extract as param
            .setForce(forceClose)
            .build()

        return Observable.create<CloseStatusUpdate> { asyncRpcStub.closeChannel(request, StreamObserverToObserver(it)) }
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

        return Single.create<ConnectPeerResponse> { asyncRpcStub.connectPeer(request, StreamObserverToSingle(it)) }
    }

    override fun getOnChainAddress(): Single<NewAddressResponse> {
        val request = NewAddressRequest.newBuilder()
            .setType(AddressType.UNUSED_WITNESS_PUBKEY_HASH)
            .build()

        return Single.create<NewAddressResponse> { asyncRpcStub.newAddress(request, StreamObserverToSingle(it)) }
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

    override fun genSeed(): Single<GenSeedResponse> {
        val request = GenSeedRequest.newBuilder().build()

        return Single.create<GenSeedResponse> { asyncWalletUnlockerStub.genSeed(request, StreamObserverToSingle(it)) }
    }

    override fun initWallet(mnemonicList: List<String>, password: String): Single<InitWalletResponse> {
        val request = InitWalletRequest
            .newBuilder()
            .setWalletPassword(ByteString.copyFromUtf8(password))
            .addAllCipherSeedMnemonic(mnemonicList)
            .build()

        return Single.create<InitWalletResponse> { asyncWalletUnlockerStub.initWallet(request, StreamObserverToSingle(it)) }
    }
}
